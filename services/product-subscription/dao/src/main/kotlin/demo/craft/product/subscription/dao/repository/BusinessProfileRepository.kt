package demo.craft.product.subscription.dao.repository

import demo.craft.common.domain.enums.Product
import demo.craft.product.subscription.domain.entity.ProductSubscription
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
internal interface ProductSubscriptionRepository : JpaRepository<ProductSubscription, Long> {
    fun findByUserId(userId: String): List<ProductSubscription>

    fun findByUserIdAndProduct(userId: String, product: Product): ProductSubscription?
}