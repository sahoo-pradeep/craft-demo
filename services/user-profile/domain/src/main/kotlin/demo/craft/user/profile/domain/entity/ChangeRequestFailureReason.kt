package demo.craft.user.profile.domain.entity

import demo.craft.common.domain.enums.Product
import demo.craft.user.profile.domain.enums.FieldName
import org.hibernate.annotations.CreationTimestamp
import java.time.LocalDateTime
import javax.persistence.*

@Entity
data class ChangeRequestFailureReason(
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null,
    val requestId: String,
    @Enumerated(EnumType.STRING)
    val product: Product,
    @Enumerated(EnumType.STRING)
    val field: FieldName,
    val reason: String,
    @CreationTimestamp
    val createdAt: LocalDateTime? = null
)
