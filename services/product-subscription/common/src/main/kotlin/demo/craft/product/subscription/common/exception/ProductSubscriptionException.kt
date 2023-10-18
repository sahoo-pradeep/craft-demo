package demo.craft.product.subscription.common.exception

import demo.craft.common.domain.enums.Product

/** All the custom exceptions should be created extending [ProductSubscriptionException]. */
sealed class ProductSubscriptionException(
    override val message: String,
    final override val cause: Throwable? = null
) : RuntimeException(message) {
    init {
        cause?.let { initCause(cause) }
    }
}

class ProductSubscriptionAlreadyExistsException(userId: String, product: Product) :
    ProductSubscriptionException("Product subscription already exist for userId $userId and $product")

class ProductSubscriptionNotFoundException(userId: String, product: Product) :
    ProductSubscriptionException("Product subscription not found for userId $userId and $product")
