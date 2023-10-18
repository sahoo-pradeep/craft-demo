package demo.craft.common.communication.kafka

import mu.KotlinLogging
import org.springframework.kafka.core.KafkaTemplate

internal class KafkaPublisherImpl(
    private val kafkaTemplate: KafkaTemplate<Int, String>,
) : KafkaPublisher {

    private val log = KotlinLogging.logger {}

    override fun publish(topic: String, key: Int, payload: String) {
        log.info { "Sending to the topic '$topic', key: $key, message: $payload" }
        kafkaTemplate.send(topic, key, payload).addCallback(
            /* onSuccess */
            { sendResult ->
                log.info { "Successfully sent message to kafka topic; $sendResult" }
            },

            /* onFail */
            { failureException ->
                log.error(failureException) { "Error sending message to kafka topic. message: $payload" }
            }
        )
    }
}
