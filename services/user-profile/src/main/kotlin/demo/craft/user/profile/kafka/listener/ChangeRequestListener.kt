package demo.craft.user.profile.kafka.listener

import com.fasterxml.jackson.databind.SerializationFeature
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import demo.craft.common.communication.kafka.KafkaPublisher
import demo.craft.user.profile.common.config.UserProfileProperties
import demo.craft.user.profile.dao.access.BusinessProfileChangeRequestAccess
import demo.craft.user.profile.domain.kafka.BusinessProfileChangeRequestKafkaPayload
import demo.craft.user.profile.integration.ProductSubscriptionIntegration
import mu.KotlinLogging
import org.springframework.kafka.annotation.KafkaListener
import org.springframework.stereotype.Component

@Component
class ChangeRequestListener(
    private val businessProfileChangeRequestAccess: BusinessProfileChangeRequestAccess,
    private val productSubscriptionIntegration: ProductSubscriptionIntegration,
    private val kafkaPublisher: KafkaPublisher,
    userProfileProperties: UserProfileProperties,
) {
    private val log = KotlinLogging.logger {}
    private val objectMapper = jacksonObjectMapper().apply {
        registerModule(JavaTimeModule()) // Register JavaTimeModule to handle Java 8 date/time types
        configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false)
    }
    private val businessProfileProperties = userProfileProperties.businessProfile

    @KafkaListener(
        id = "BusinessProfileChangeRequestListener",
        topics = ["\${demo.craft.user-profile.businessProfile.kafka.changeRequestTopicName}"]
    )
    fun onMessage(kafkaMessage: String) {
        log.info { "Received kafka message. Topic: ${businessProfileProperties.kafka.changeRequestTopicName}. Message: $kafkaMessage" }
        val message = objectMapper.readValue(kafkaMessage, BusinessProfileChangeRequestKafkaPayload::class.java)
        // get change request
        // get current business profile
        // get all products subscribed by the user
        val subscribedProducts = productSubscriptionIntegration.getProductSubscriptions(message.userId)
        println(subscribedProducts)
        // save entries in db
        // publish kafka message
    }
}