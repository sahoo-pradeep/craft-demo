package demo.craft.quickbooks.accounting.kafka.listener

import com.fasterxml.jackson.databind.SerializationFeature
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import demo.craft.common.communication.kafka.KafkaPublisher
import demo.craft.common.domain.enums.Product
import demo.craft.quickbooks.accounting.config.QuickbooksAccountingProperties
import demo.craft.user.profile.domain.enums.ChangeRequestOperation
import demo.craft.user.profile.domain.enums.ChangeRequestStatus
import demo.craft.user.profile.domain.enums.FieldName
import demo.craft.user.profile.domain.kafka.BusinessProfileValidationRequest
import demo.craft.user.profile.domain.kafka.BusinessProfileValidationResponse
import mu.KotlinLogging
import org.springframework.kafka.annotation.KafkaListener
import org.springframework.stereotype.Component

@Component
class BusinessProfileValidationListener(
    private val kafkaPublisher: KafkaPublisher,
    properties: QuickbooksAccountingProperties
) {
    private val log = KotlinLogging.logger {}
    private val objectMapper = jacksonObjectMapper().apply {
        registerModule(JavaTimeModule()) // Register JavaTimeModule to handle Java 8 date/time types
        configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false)
    }
    private val kafkaProperties = properties.kafka

    @KafkaListener(
        topics = ["\${demo.craft.quickbooks-accounting.kafka.businessProfileValidationRequestTopic}"]
    )
    fun onMessage(kafkaMessage: String) {
        val topicName = kafkaProperties.businessProfileValidationRequestTopic
        log.info { "Received kafka message. Topic: $topicName. Message: $kafkaMessage" }
        val validationRequest = objectMapper.readValue(kafkaMessage, BusinessProfileValidationRequest::class.java)
        val failureReasons = validateBusinessProfile(validationRequest)
        val status = when (failureReasons.isEmpty()) {
            true -> ChangeRequestStatus.ACCEPTED
            false -> ChangeRequestStatus.REJECTED
        }

        val responsePayload = objectMapper.writeValueAsString(
            BusinessProfileValidationResponse(
                userId = validationRequest.profileChangeRequest.userId,
                requestId = validationRequest.profileChangeRequest.requestId,
                product = Product.QUICKBOOKS_ACCOUNTING,
                status = status,
                failureReasons = failureReasons
            )
        )

        kafkaPublisher.publish(
            kafkaProperties.businessProfileValidationResponseTopic,
            validationRequest.profileChangeRequest.userId.hashCode(),
            responsePayload
        )
    }

    private fun validateBusinessProfile(request: BusinessProfileValidationRequest): List<Pair<FieldName, String>> {
        val failureReasons = mutableListOf<Pair<FieldName, String>>()

        // validate non-updatable fields
        if (request.profileChangeRequest.operation == ChangeRequestOperation.UPDATE) {
            when {
                request.currentProfile!!.pan != request.profileChangeRequest.pan -> failureReasons.add(Pair(FieldName.PAN, "Pan update is not allowed"))
            }
        }

        if (!isZipServiceable(request.profileChangeRequest.legalAddress.zip)) {
            failureReasons.add(Pair(FieldName.LEGAL_ADDRESS_ZIP, "Zip is not serviceable"))
        }

        if (!isZipServiceable(request.profileChangeRequest.businessAddress.zip)) {
            failureReasons.add(Pair(FieldName.BUSINESS_ADDRESS_ZIP, "Zip is not serviceable"))
        }

        return failureReasons
    }

    private fun isZipServiceable(zip: String): Boolean =
        when (zip.first()) {
            '1', '2', '3', '4', '5' -> true
            else -> false
        }
}