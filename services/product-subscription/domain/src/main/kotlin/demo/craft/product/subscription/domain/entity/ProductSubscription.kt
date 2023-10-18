package demo.craft.product.subscription.domain.entity

import demo.craft.common.domain.enums.Product
import demo.craft.product.subscription.domain.enums.ProductSubscriptionStatus
import java.time.LocalDateTime
import javax.persistence.Entity
import javax.persistence.EnumType
import javax.persistence.Enumerated
import javax.persistence.GeneratedValue
import javax.persistence.GenerationType
import javax.persistence.Id
import org.hibernate.annotations.CreationTimestamp
import org.hibernate.annotations.UpdateTimestamp

@Entity
data class ProductSubscription(
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null,
    val userId: String,
    @Enumerated(EnumType.STRING)
    val product: Product,
    @Enumerated(EnumType.STRING)
    val status: ProductSubscriptionStatus,
    @CreationTimestamp
    val createdAt: LocalDateTime? = null,
    @UpdateTimestamp
    val updatedAt: LocalDateTime? = null,
)
