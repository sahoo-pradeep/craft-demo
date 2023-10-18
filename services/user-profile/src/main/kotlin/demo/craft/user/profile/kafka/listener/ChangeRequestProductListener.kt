package demo.craft.user.profile.kafka.listener

import com.fasterxml.jackson.databind.SerializationFeature
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import demo.craft.user.profile.common.config.UserProfileProperties
import demo.craft.user.profile.domain.kafka.BusinessProfileChangeRequestProductValidationResponse
import mu.KotlinLogging
import org.springframework.kafka.annotation.KafkaListener
import org.springframework.stereotype.Component

@Component
class ChangeRequestProductListener(
    userProfileProperties: UserProfileProperties,

    ) {
    private val log = KotlinLogging.logger {}
    private val objectMapper = jacksonObjectMapper().apply {
        registerModule(JavaTimeModule()) // Register JavaTimeModule to handle Java 8 date/time types
        configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false)
    }
    private val businessProfileProperties = userProfileProperties.businessProfile

    @KafkaListener(
        id = "ChangeRequestProductListener",
        topics = ["\${demo.craft.user-profile.businessProfile.kafka.changeRequestProductTopicName}"]
    )
    fun onMessage(kafkaMessage: String) {
        log.info { "Received kafka message. Topic: ${businessProfileProperties.kafka.changeRequestProductTopicName}. Message: $kafkaMessage" }
        val message =
            objectMapper.readValue(kafkaMessage, BusinessProfileChangeRequestProductValidationResponse::class.java)
        // update status in change request product status
        // add errors if exist
        // get all the product status.
        // if all the status are approved -> change the change request as approved and update business profile
        // if any status is rejected and change request is not rejected yet, mark it rejected
        // if pending -> no change in change requests status
    }
}