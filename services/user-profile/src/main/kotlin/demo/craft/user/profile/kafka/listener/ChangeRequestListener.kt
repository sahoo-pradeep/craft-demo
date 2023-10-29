package demo.craft.user.profile.kafka.listener

import com.fasterxml.jackson.databind.SerializationFeature
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import demo.craft.common.communication.kafka.KafkaPublisher
import demo.craft.product.subscription.client.model.ProductSubscriptionStatus
import demo.craft.user.profile.common.config.UserProfileProperties
import demo.craft.user.profile.common.exception.BusinessProfileChangeRequestNotFoundException
import demo.craft.user.profile.dao.access.BusinessProfileAccess
import demo.craft.user.profile.dao.access.BusinessProfileChangeRequestAccess
import demo.craft.user.profile.dao.access.ChangeRequestProductStatusAccess
import demo.craft.user.profile.domain.enums.ChangeRequestStatus
import demo.craft.user.profile.domain.kafka.BusinessProfileChangeRequestKafkaPayload
import demo.craft.user.profile.domain.kafka.BusinessProfileValidationRequest
import demo.craft.user.profile.integration.ProductSubscriptionIntegration
import demo.craft.user.profile.integration.mapper.toChangeRequestProductStatuses
import demo.craft.user.profile.lock.UserProfileLockManager
import mu.KotlinLogging
import org.springframework.kafka.annotation.KafkaListener
import org.springframework.stereotype.Component

@Component
class ChangeRequestListener(
    private val businessProfileAccess: BusinessProfileAccess,
    private val businessProfileChangeRequestAccess: BusinessProfileChangeRequestAccess,
    private val changeRequestProductStatusAccess: ChangeRequestProductStatusAccess,
    private val productSubscriptionIntegration: ProductSubscriptionIntegration,
    private val kafkaPublisher: KafkaPublisher,
    private val lockManager: UserProfileLockManager,
    userProfileProperties: UserProfileProperties,
) {
    private val log = KotlinLogging.logger {}
    private val objectMapper = jacksonObjectMapper().apply {
        registerModule(JavaTimeModule()) // Register JavaTimeModule to handle Java 8 date/time types
        configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false)
    }
    private val kafkaProperties = userProfileProperties.kafka

    @KafkaListener(
        topics = ["\${demo.craft.user-profile.kafka.businessProfileChangeRequestTopic}"]
    )
    fun onMessage(message: String) {
        val topicName = kafkaProperties.businessProfileChangeRequestTopic
        log.info { "Received kafka message. Topic: $topicName. Message: $message" }
        val changeRequestKafkaPayload = objectMapper.readValue(message, BusinessProfileChangeRequestKafkaPayload::class.java)
        val userId = changeRequestKafkaPayload.userId
        val requestId = changeRequestKafkaPayload.requestId
        if (changeRequestProductStatusAccess.findAllByRequestId(requestId).isNotEmpty()) {
            log.error {
                "Change request is already published to subscribed products. " +
                    "Ignoring publishing change request: $changeRequestKafkaPayload"
            }
            return
        }

        lockManager.doExclusively(userId) {
            val changeRequest = businessProfileChangeRequestAccess.findByUserIdAndRequestId(userId, requestId)
                ?: throw BusinessProfileChangeRequestNotFoundException(userId, requestId)

            val currentProfile = businessProfileAccess.findByUserId(userId)

            val activeProductSubscriptions = productSubscriptionIntegration.getProductSubscriptions(userId)
                .filter { it.status == ProductSubscriptionStatus.ACTIVE }

            if (activeProductSubscriptions.isEmpty()) {
                log.error { "Investigate! Change request received for a user ${userId} with no active product subscriptions" }
                businessProfileChangeRequestAccess.updateStatus(userId, requestId, ChangeRequestStatus.FAILED)
                return@doExclusively
            }

            changeRequestProductStatusAccess.createNewProductStatuses(
                activeProductSubscriptions.toChangeRequestProductStatuses(requestId, ChangeRequestStatus.IN_PROGRESS)
            )

            val kafkaPayload = objectMapper.writeValueAsString(BusinessProfileValidationRequest(currentProfile, changeRequest))
            kafkaPublisher.publish(kafkaProperties.businessProfileValidationRequestTopic, userId.hashCode(), kafkaPayload)
        }
    }
}