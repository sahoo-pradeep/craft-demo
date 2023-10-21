package demo.craft.user.profile.kafka.listener

import com.fasterxml.jackson.databind.SerializationFeature
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import demo.craft.user.profile.common.config.UserProfileProperties
import demo.craft.user.profile.dao.access.BusinessProfileAccess
import demo.craft.user.profile.dao.access.BusinessProfileChangeRequestAccess
import demo.craft.user.profile.dao.access.ChangeRequestFailureReasonAccess
import demo.craft.user.profile.dao.access.ChangeRequestProductStatusAccess
import demo.craft.user.profile.domain.enums.ChangeRequestStatus
import demo.craft.user.profile.domain.kafka.BusinessProfileValidationResponse
import mu.KotlinLogging
import org.springframework.kafka.annotation.KafkaListener
import org.springframework.stereotype.Component

@Component
class ValidationResponseListener(
    private val businessProfileAccess: BusinessProfileAccess,
    private val businessProfileChangeRequestAccess: BusinessProfileChangeRequestAccess,
    private val changeRequestProductStatusRepository: ChangeRequestProductStatusAccess,
    private val changeRequestFailureReasonAccess: ChangeRequestFailureReasonAccess,
    private val userProfileProperties: UserProfileProperties
) {
    private val log = KotlinLogging.logger {}
    private val objectMapper = jacksonObjectMapper().apply {
        registerModule(JavaTimeModule()) // Register JavaTimeModule to handle Java 8 date/time types
        configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false)
    }

    @KafkaListener(
        topics = ["\${demo.craft.user-profile.kafka.businessProfileValidationResponseTopic}"]
    )
    fun onMessage(kafkaMessage: String) {
        val topicName = userProfileProperties.kafka.businessProfileValidationResponseTopic
        log.info { "Received kafka message. Topic: $topicName. Message: $kafkaMessage" }
        val validationResponse = objectMapper.readValue(kafkaMessage, BusinessProfileValidationResponse::class.java)

        changeRequestProductStatusRepository.findByRequestIdAndProduct(validationResponse.requestId, validationResponse.product)?.let {
            if (it.status.isTerminal()) {
                log.warn { "Validation status for given product is already in terminal state. payload: $kafkaMessage" }
                return
            }
        } ?: log.error { "Investigate! Change request product status not found. payload: $kafkaMessage" }.run { return }

        changeRequestProductStatusRepository
            .updateStatus(validationResponse.requestId, validationResponse.product, validationResponse.status)

        if (validationResponse.status == ChangeRequestStatus.REJECTED && validationResponse.failureReasons.isNotEmpty()) {
            changeRequestFailureReasonAccess.saveAllFailureReason(
                validationResponse.requestId,
                validationResponse.product,
                validationResponse.failureReasons
            )
        }

        val changeRequest = businessProfileChangeRequestAccess.findByRequestId(validationResponse.requestId)!!

        if (changeRequest.status == ChangeRequestStatus.REJECTED) {
            // no need to update change request status
            return
        }

        val changeRequestProductStatuses = changeRequestProductStatusRepository.findByRequestId(validationResponse.requestId)

        if (changeRequestProductStatuses.any { it.status == ChangeRequestStatus.REJECTED }) {
            businessProfileChangeRequestAccess.updateStatus(validationResponse.userId, validationResponse.requestId, ChangeRequestStatus.REJECTED)
            return
        }

        if (changeRequestProductStatuses.all { it.status == ChangeRequestStatus.ACCEPTED }) {
            businessProfileChangeRequestAccess.updateStatus(validationResponse.userId, validationResponse.requestId, ChangeRequestStatus.ACCEPTED)
            businessProfileAccess.updateBusinessProfile(changeRequest)
            return
        }
    }
}