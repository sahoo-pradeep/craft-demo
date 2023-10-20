package demo.craft.user.profile.kafka.listener

import com.fasterxml.jackson.databind.SerializationFeature
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import demo.craft.user.profile.common.config.UserProfileProperties
import demo.craft.user.profile.domain.kafka.BusinessProfileValidationResponse
import mu.KotlinLogging
import org.springframework.kafka.annotation.KafkaListener
import org.springframework.stereotype.Component

@Component
class ValidationResponseListener(
    private val userProfileProperties: UserProfileProperties
) {
    private val log = KotlinLogging.logger {}
    private val objectMapper = jacksonObjectMapper().apply {
        registerModule(JavaTimeModule()) // Register JavaTimeModule to handle Java 8 date/time types
        configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false)
    }

    @KafkaListener(
        id = "ChangeRequestProductListener",
        topics = ["\${demo.craft.user-profile.businessProfile.kafka.changeRequestProductTopicName}"]
    )
    fun onMessage(kafkaMessage: String) {
        val topicName = userProfileProperties.kafka.businessProfileValidationResponseTopic
        log.info { "Received kafka message. Topic: $topicName. Message: $kafkaMessage" }
        val message = objectMapper.readValue(kafkaMessage, BusinessProfileValidationResponse::class.java)
        // update status in change request product status
        // add errors if exist
        // get all the product status.
        // if all the status are approved -> change the change request as approved and update business profile
        // if any status is rejected and change request is not rejected yet, mark it rejected
        // if pending -> no change in change requests status
    }
}