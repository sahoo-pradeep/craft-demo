package demo.craft.user.profile.integration

import demo.craft.product.subscription.client.api.ProductSubscriptionApi
import demo.craft.product.subscription.client.model.ProductSubscription
import org.springframework.stereotype.Component

@Component
class ProductSubscriptionIntegration(
    private val productSubscriptionApi: ProductSubscriptionApi
) {
    fun getProductSubscriptions(userId: String): List<ProductSubscription> =
        productSubscriptionApi.getAllProductSubscriptions(userId).body!!.productSubscriptions
}
