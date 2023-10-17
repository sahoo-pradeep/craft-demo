package demo.craft.model.kafka

import java.time.LocalDateTime

data class BusinessProfileChangeRequestKafkaPayload(
    val userId: String,
    val requestId: String,
    val createdAt: LocalDateTime
)