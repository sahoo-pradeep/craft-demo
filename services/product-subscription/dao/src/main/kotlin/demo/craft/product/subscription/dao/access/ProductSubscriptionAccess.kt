package demo.craft.product.subscription.dao.access

import demo.craft.common.domain.enums.Product
import demo.craft.product.subscription.domain.entity.ProductSubscription

interface ProductSubscriptionAccess {
    /** Get product subscriptions for the given user */
    fun findByUserId(userId: String): List<ProductSubscription>

    /** Get product subscriptions for the given user and product */
    fun findByUserIdAndProduct(userId: String, product: Product): ProductSubscription?

    fun saveProductSubscription(productSubscription: ProductSubscription): ProductSubscription

    fun updateProductSubscription(productSubscription: ProductSubscription): ProductSubscription
}