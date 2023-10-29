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
import demo.craft.user.profile.lock.UserProfileLockManager
import mu.KotlinLogging
import org.springframework.kafka.annotation.KafkaListener
import org.springframework.stereotype.Component

@Component
class ValidationResponseListener(
    private val businessProfileAccess: BusinessProfileAccess,
    private val businessProfileChangeRequestAccess: BusinessProfileChangeRequestAccess,
    private val changeRequestProductStatusAccess: ChangeRequestProductStatusAccess,
    private val changeRequestFailureReasonAccess: ChangeRequestFailureReasonAccess,
    private val lockManager: UserProfileLockManager,
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
        val userId = validationResponse.userId
        val requestId = validationResponse.requestId
        lockManager.doExclusively(userId) {
            val currentProductStatus = changeRequestProductStatusAccess.findByRequestIdAndProduct(requestId, validationResponse.product)
                ?: log.warn { "Investigate! Change request product status not found. payload: $kafkaMessage" }.run { return@doExclusively }

            if (currentProductStatus.status.isTerminal()) {
                log.warn { "Validation status for given product is already in terminal state. payload: $kafkaMessage" }
                return@doExclusively
            }

            changeRequestProductStatusAccess
                .updateStatus(requestId, validationResponse.product, validationResponse.status)

            if (validationResponse.status == ChangeRequestStatus.REJECTED && validationResponse.failureReasons.isNotEmpty()) {
                changeRequestFailureReasonAccess.saveAllFailureReason(
                    requestId,
                    validationResponse.product,
                    validationResponse.failureReasons
                )
            }

            // Update status in change request based on product validation status
            val changeRequest = businessProfileChangeRequestAccess.findByUserIdAndRequestId(userId, requestId)!!
            if (changeRequest.status.isTerminal()) {
                // change request is already in terminal state.
                return@doExclusively
            }

            val allProductStatuses = changeRequestProductStatusAccess.findAllByRequestId(requestId)

            if (allProductStatuses.any { it.status == ChangeRequestStatus.REJECTED }) {
                businessProfileChangeRequestAccess.updateStatus(userId, requestId, ChangeRequestStatus.REJECTED)
                return@doExclusively
            }

            if (allProductStatuses.all { it.status == ChangeRequestStatus.ACCEPTED }) {
                businessProfileChangeRequestAccess.updateStatus(userId, requestId, ChangeRequestStatus.ACCEPTED)
                businessProfileAccess.createOrUpdateBusinessProfile(changeRequest)
                return@doExclusively
            }
        }
    }
}