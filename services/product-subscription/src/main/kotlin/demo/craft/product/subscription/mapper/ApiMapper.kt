package demo.craft.product.subscription.mapper

import demo.craft.common.domain.enums.Product
import demo.craft.product.subscription.domain.entity.ProductSubscription
import demo.craft.product.subscription.domain.enums.ProductSubscriptionStatus

fun List<ProductSubscription>.toApiModel(): List<demo.craft.product.subscription.model.ProductSubscription> =
    this.map { it.toApiModel() }

fun ProductSubscription.toApiModel(): demo.craft.product.subscription.model.ProductSubscription =
    demo.craft.product.subscription.model.ProductSubscription(
        product = this.product.toApiModel(),
        status = this.status.toApiModel()
    )

fun demo.craft.product.subscription.model.ProductSubscription.toDomainModel(userId: String): ProductSubscription =
    ProductSubscription(
        userId = userId,
        product = this.product.toDomainModel(),
        status = this.status.toDomainModel()
    )

fun Product.toApiModel(): demo.craft.product.subscription.model.Product =
    when (this) {
        Product.QUICKBOOKS_ACCOUNTING -> demo.craft.product.subscription.model.Product.QUICKBOOKS_ACCOUNTING
        Product.QUICKBOOKS_PAYROLL -> demo.craft.product.subscription.model.Product.QUICKBOOKS_PAYROLL
        Product.QUICKBOOKS_PAYMENTS -> demo.craft.product.subscription.model.Product.QUICKBOOKS_PAYMENTS
        Product.TSHEETS -> demo.craft.product.subscription.model.Product.TSHEETS
    }

fun demo.craft.product.subscription.model.Product.toDomainModel(): Product =
    when (this) {
        demo.craft.product.subscription.model.Product.QUICKBOOKS_ACCOUNTING -> Product.QUICKBOOKS_ACCOUNTING
        demo.craft.product.subscription.model.Product.QUICKBOOKS_PAYROLL -> Product.QUICKBOOKS_PAYROLL
        demo.craft.product.subscription.model.Product.QUICKBOOKS_PAYMENTS -> Product.QUICKBOOKS_PAYMENTS
        demo.craft.product.subscription.model.Product.TSHEETS -> Product.TSHEETS
    }

fun ProductSubscriptionStatus.toApiModel(): demo.craft.product.subscription.model.ProductSubscriptionStatus =
    when (this) {
        ProductSubscriptionStatus.ACTIVE -> demo.craft.product.subscription.model.ProductSubscriptionStatus.ACTIVE
        ProductSubscriptionStatus.CLOSED -> demo.craft.product.subscription.model.ProductSubscriptionStatus.CLOSED
    }

fun demo.craft.product.subscription.model.ProductSubscriptionStatus.toDomainModel(): ProductSubscriptionStatus =
    when (this) {
        demo.craft.product.subscription.model.ProductSubscriptionStatus.ACTIVE -> ProductSubscriptionStatus.ACTIVE
        demo.craft.product.subscription.model.ProductSubscriptionStatus.CLOSED -> ProductSubscriptionStatus.CLOSED
    }
