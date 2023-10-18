package demo.craft.common.communication.kafka

interface KafkaPublisher {
    fun publish(topic: String, key: Int, payload: String)
}