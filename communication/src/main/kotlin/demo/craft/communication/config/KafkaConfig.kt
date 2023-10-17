package demo.craft.communication.config

import demo.craft.communication.kafka.KafkaPublisher
import demo.craft.communication.kafka.KafkaPublisherImpl
import org.apache.kafka.clients.producer.ProducerConfig
import org.apache.kafka.common.serialization.IntegerSerializer
import org.apache.kafka.common.serialization.StringSerializer
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.kafka.annotation.EnableKafka
import org.springframework.kafka.core.DefaultKafkaProducerFactory
import org.springframework.kafka.core.KafkaTemplate

@Configuration
@EnableKafka
class KafkaConfig(
    communicationProperties: CommunicationProperties
) {
    private val kafkaProperties = communicationProperties.kafka

    @Bean
    fun kafkaPublisher(): KafkaPublisher = KafkaPublisherImpl(kafkaTemplate())

    private fun kafkaTemplate(): KafkaTemplate<Int, String> =
        KafkaTemplate<Int, String>(DefaultKafkaProducerFactory(producerConfigs()))

    private fun producerConfigs(): Map<String, Any> = mapOf(
        ProducerConfig.BOOTSTRAP_SERVERS_CONFIG to kafkaProperties.bootstrapServers,
        ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG to IntegerSerializer::class.java,
        ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG to StringSerializer::class.java
    )
}
