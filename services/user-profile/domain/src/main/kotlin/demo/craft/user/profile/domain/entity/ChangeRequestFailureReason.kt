package demo.craft.user.profile.domain.entity

import demo.craft.common.domain.enums.Product
import demo.craft.user.profile.domain.enums.FieldName
import java.time.LocalDateTime
import javax.persistence.Entity
import javax.persistence.EnumType
import javax.persistence.Enumerated
import javax.persistence.GeneratedValue
import javax.persistence.GenerationType
import javax.persistence.Id
import org.hibernate.annotations.CreationTimestamp

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
