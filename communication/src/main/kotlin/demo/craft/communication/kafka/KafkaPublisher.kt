package demo.craft.communication.kafka

interface KafkaPublisher {
    fun publish(topic: String, key: Int, payload: String)
}