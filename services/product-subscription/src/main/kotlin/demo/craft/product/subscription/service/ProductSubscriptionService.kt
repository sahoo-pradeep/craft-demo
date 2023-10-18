package demo.craft.product.subscription.service

import demo.craft.product.subscription.dao.access.ProductSubscriptionAccess
import demo.craft.product.subscription.domain.entity.ProductSubscription
import org.springframework.stereotype.Service

@Service
class ProductSubscriptionService(
    private val productSubscriptionAccess: ProductSubscriptionAccess,
) {
    fun getAllProductSubscriptions(userId: String): List<ProductSubscription> =
        productSubscriptionAccess.findByUserId(userId)

    fun saveProductSubscription(
        productSubscription: ProductSubscription
    ): ProductSubscription =
        productSubscriptionAccess.saveProductSubscription(productSubscription)

    fun updateProductSubscription(
        productSubscription: ProductSubscription
    ): ProductSubscription =
        productSubscriptionAccess.updateProductSubscription(productSubscription)
}