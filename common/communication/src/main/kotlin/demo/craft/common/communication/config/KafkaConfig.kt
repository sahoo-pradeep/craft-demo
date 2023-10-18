package demo.craft.common.communication.config

import demo.craft.common.communication.kafka.KafkaPublisher
import demo.craft.common.communication.kafka.KafkaPublisherImpl
import org.apache.kafka.clients.consumer.ConsumerConfig
import org.apache.kafka.clients.producer.ProducerConfig
import org.apache.kafka.common.serialization.IntegerDeserializer
import org.apache.kafka.common.serialization.IntegerSerializer
import org.apache.kafka.common.serialization.StringDeserializer
import org.apache.kafka.common.serialization.StringSerializer
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.kafka.annotation.EnableKafka
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory
import org.springframework.kafka.config.KafkaListenerContainerFactory
import org.springframework.kafka.core.DefaultKafkaConsumerFactory
import org.springframework.kafka.core.DefaultKafkaProducerFactory
import org.springframework.kafka.core.KafkaTemplate
import org.springframework.kafka.listener.ConcurrentMessageListenerContainer

@Configuration
@EnableKafka
class KafkaConfig(
    communicationProperties: CommunicationProperties
) {
    private val kafkaProperties = communicationProperties.kafka

    @Bean
    fun kafkaPublisher(): KafkaPublisher = KafkaPublisherImpl(kafkaTemplate())

    @Bean
    fun kafkaListenerContainerFactory(): KafkaListenerContainerFactory<ConcurrentMessageListenerContainer<Int, String>> {
        val factory = ConcurrentKafkaListenerContainerFactory<Int, String>()
        factory.consumerFactory = kafkaConsumerFactory()
        factory.setConcurrency(kafkaProperties.consumer.concurrency)
        factory.containerProperties.pollTimeout = 3000
        factory.setAutoStartup(kafkaProperties.consumer.enabled)
        return factory
    }

    private fun kafkaTemplate(): KafkaTemplate<Int, String> =
        KafkaTemplate<Int, String>(DefaultKafkaProducerFactory(producerConfigs()))

    private fun producerConfigs(): Map<String, Any> = mapOf(
        ProducerConfig.BOOTSTRAP_SERVERS_CONFIG to kafkaProperties.bootstrapServers,
        ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG to IntegerSerializer::class.java,
        ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG to StringSerializer::class.java
    )

    private fun kafkaConsumerFactory() = DefaultKafkaConsumerFactory<Int, String>(consumerProps())

    private fun consumerProps(): Map<String, Any> =
        mapOf(
            ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG to kafkaProperties.bootstrapServers,
            ConsumerConfig.GROUP_ID_CONFIG to kafkaProperties.consumer.groupId,
            ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG to kafkaProperties.consumer.enableAutoCommitConfig,
            ConsumerConfig.AUTO_COMMIT_INTERVAL_MS_CONFIG to kafkaProperties.consumer.autoCommitInterval.toMillis().toInt(),
            ConsumerConfig.SESSION_TIMEOUT_MS_CONFIG to kafkaProperties.consumer.sessionTimeout.toMillis().toInt(),
            ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG to IntegerDeserializer::class.java,
            ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG to StringDeserializer::class.java
        )
}
