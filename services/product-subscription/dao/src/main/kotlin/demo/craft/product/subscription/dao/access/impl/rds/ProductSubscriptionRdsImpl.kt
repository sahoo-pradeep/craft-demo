package demo.craft.product.subscription.dao.access.impl.rds

import demo.craft.common.domain.enums.Product
import demo.craft.product.subscription.common.exception.ProductSubscriptionAlreadyExistsException
import demo.craft.product.subscription.common.exception.ProductSubscriptionNotFoundException
import demo.craft.product.subscription.dao.access.ProductSubscriptionAccess
import demo.craft.product.subscription.dao.repository.ProductSubscriptionRepository
import demo.craft.product.subscription.domain.entity.ProductSubscription
import mu.KotlinLogging
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDateTime

@Component
internal class ProductSubscriptionRdsImpl(
    private val businessProfileRepository: ProductSubscriptionRepository,
) : ProductSubscriptionAccess {
    private val log = KotlinLogging.logger {}

    override fun findByUserId(userId: String): List<ProductSubscription> =
        businessProfileRepository.findByUserId(userId)

    override fun findByUserIdAndProduct(userId: String, product: Product): ProductSubscription? =
        businessProfileRepository.findByUserIdAndProduct(userId, product)

    @Transactional
    override fun saveProductSubscription(productSubscription: ProductSubscription): ProductSubscription {
        findByUserIdAndProduct(productSubscription.userId, productSubscription.product)?.let {
            throw ProductSubscriptionAlreadyExistsException(productSubscription.userId, productSubscription.product)
        }

        val newProductSubscription = ProductSubscription(
            userId = productSubscription.userId,
            product = productSubscription.product,
            status = productSubscription.status
        )

        return businessProfileRepository.save(newProductSubscription).also {
            log.info { "new product subscription is created: $it" }
        }
    }

    @Transactional
    override fun updateProductSubscription(productSubscription: ProductSubscription): ProductSubscription {
        val currentProductSubscription = findByUserIdAndProduct(productSubscription.userId, productSubscription.product)
            ?: throw ProductSubscriptionNotFoundException(productSubscription.userId, productSubscription.product)

        if (currentProductSubscription.status == productSubscription.status) {
            log.info { "no change in product subscription. ignoring update for $productSubscription" }
            return currentProductSubscription
        }

        val updatedProductSubscription = currentProductSubscription.copy(
            status = productSubscription.status,
            updatedAt = LocalDateTime.now()
        )

        return businessProfileRepository.save(updatedProductSubscription).also {
            log.info { "product subscription is updated: $it" }
        }
    }
}