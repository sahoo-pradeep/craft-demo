package demo.craft.user.profile.common.domain.domain.entity

import demo.craft.user.profile.common.domain.domain.enums.FieldName
import demo.craft.user.profile.common.domain.domain.enums.Product
import java.time.LocalDateTime
import javax.persistence.Entity
import javax.persistence.EnumType
import javax.persistence.Enumerated
import javax.persistence.GeneratedValue
import javax.persistence.GenerationType
import javax.persistence.Id
import org.hibernate.annotations.CreationTimestamp

@Entity
data class UpdateRequestProductFailureReason(
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null,
    val requestUuid: String,
    @Enumerated(EnumType.STRING)
    val product: Product,
    @Enumerated(EnumType.STRING)
    val field: FieldName,
    val reason: String,
    @CreationTimestamp
    val createdAt: LocalDateTime? = null
)
