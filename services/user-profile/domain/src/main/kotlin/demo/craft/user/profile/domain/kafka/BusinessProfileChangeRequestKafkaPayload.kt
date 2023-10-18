package demo.craft.user.profile.domain.kafka

import java.time.LocalDateTime

data class BusinessProfileChangeRequestKafkaPayload(
    val userId: String,
    val requestId: String,
    val createdAt: LocalDateTime
)