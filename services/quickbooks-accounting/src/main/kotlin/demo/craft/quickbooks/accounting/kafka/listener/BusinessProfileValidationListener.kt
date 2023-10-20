package demo.craft.quickbooks.accounting.kafka.listener

import com.fasterxml.jackson.databind.SerializationFeature
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import demo.craft.common.communication.kafka.KafkaPublisher
import demo.craft.quickbooks.accounting.config.QuickbooksAccountingProperties
import demo.craft.user.profile.domain.kafka.BusinessProfileChangeRequestKafkaPayload
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
            id = "BusinessProfileValidationListener",
            topics = ["\${demo.craft.quickbooks-accounting.kafka.businessProfileValidationRequestTopic}"]
    )
    fun onMessage(kafkaMessage: String) {
        val topicName = kafkaProperties.businessProfileValidationRequestTopic
        log.info { "Received kafka message. Topic: $topicName. Message: $kafkaMessage" }
        // validate the update request
        // publish to kafka with response
    }
}