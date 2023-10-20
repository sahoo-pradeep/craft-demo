package demo.craft.user.profile.kafka.listener

import com.fasterxml.jackson.databind.SerializationFeature
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import demo.craft.common.communication.kafka.KafkaPublisher
import demo.craft.product.subscription.client.model.ProductSubscriptionStatus
import demo.craft.user.profile.common.config.UserProfileProperties
import demo.craft.user.profile.dao.access.BusinessProfileAccess
import demo.craft.user.profile.dao.access.BusinessProfileChangeRequestAccess
import demo.craft.user.profile.dao.access.ChangeRequestProductStatusAccess
import demo.craft.user.profile.domain.enums.ChangeRequestStatus
import demo.craft.user.profile.domain.kafka.BusinessProfileChangeRequestKafkaPayload
import demo.craft.user.profile.domain.kafka.BusinessProfileValidationRequest
import demo.craft.user.profile.integration.ProductSubscriptionIntegration
import demo.craft.user.profile.mapper.toChangeRequestProductStatuses
import mu.KotlinLogging
import org.springframework.kafka.annotation.KafkaListener
import org.springframework.stereotype.Component

@Component
class ChangeRequestListener(
    private val businessProfileAccess: BusinessProfileAccess,
    private val businessProfileChangeRequestAccess: BusinessProfileChangeRequestAccess,
    private val changeRequestProductStatusRepository: ChangeRequestProductStatusAccess,
    private val productSubscriptionIntegration: ProductSubscriptionIntegration,
    private val kafkaPublisher: KafkaPublisher,
    userProfileProperties: UserProfileProperties,
) {
    private val log = KotlinLogging.logger {}
    private val objectMapper = jacksonObjectMapper().apply {
        registerModule(JavaTimeModule()) // Register JavaTimeModule to handle Java 8 date/time types
        configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false)
    }
    private val kafkaProperties = userProfileProperties.kafka

    @KafkaListener(
        id = "BusinessProfileChangeRequestListener",
        topics = ["\${demo.craft.user-profile.businessProfile.kafka.changeRequestTopicName}"]
    )
    fun onMessage(kafkaMessage: String) {
        val topicName = kafkaProperties.businessProfileChangeRequestTopic
        log.info { "Received kafka message. Topic: $topicName. Message: $kafkaMessage" }
        val message = objectMapper.readValue(kafkaMessage, BusinessProfileChangeRequestKafkaPayload::class.java)

        if (changeRequestProductStatusRepository.existsByRequestId(message.requestId)) {
            log.error { "Change request is already published to subscribed products. " +
                "Ignoring publishing change request: $kafkaMessage" }
            return
        }

        val changeRequest = businessProfileChangeRequestAccess.findByRequestId(message.requestId)
            ?: throw IllegalArgumentException("requestId in kafka message $message is invalid")

        val currentProfile = businessProfileAccess.findByUserId(message.userId)

        val activeProductSubscriptions = productSubscriptionIntegration.getProductSubscriptions(message.userId)
            .filter { it.status == ProductSubscriptionStatus.ACTIVE }

        if (activeProductSubscriptions.isEmpty()) {
            log.error { "Investigate! Change request received for a user ${message.userId} with no active product subscriptions" }
            return
        }

        changeRequestProductStatusRepository.saveAll(
            activeProductSubscriptions.toChangeRequestProductStatuses(message.requestId, ChangeRequestStatus.IN_PROGRESS)
        )

        val kafkaPayload = objectMapper.writeValueAsString(BusinessProfileValidationRequest(currentProfile,changeRequest))
        kafkaPublisher.publish(kafkaProperties.businessProfileChangeRequestTopic, message.userId.hashCode(), kafkaPayload)
    }
}