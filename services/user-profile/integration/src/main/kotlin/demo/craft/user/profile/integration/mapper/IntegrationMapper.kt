package demo.craft.user.profile.integration.mapper

import demo.craft.common.domain.enums.Product
import demo.craft.product.subscription.client.model.ProductSubscription
import demo.craft.user.profile.domain.entity.ChangeRequestProductStatus
import demo.craft.user.profile.domain.enums.ChangeRequestStatus

fun List<ProductSubscription>.toChangeRequestProductStatuses(
    requestId: String,
    status: ChangeRequestStatus
): List<ChangeRequestProductStatus> =
    this.map { it.toChangeRequestProductStatus(requestId, status) }

fun ProductSubscription.toChangeRequestProductStatus(
    requestId: String,
    status: ChangeRequestStatus
): ChangeRequestProductStatus =
    ChangeRequestProductStatus(
        requestId = requestId,
        product = this.product.toDomainModel(),
        status = status
    )

fun demo.craft.product.subscription.client.model.Product.toDomainModel(): Product =
    when (this) {
        demo.craft.product.subscription.client.model.Product.QUICKBOOKS_ACCOUNTING -> Product.QUICKBOOKS_ACCOUNTING
        demo.craft.product.subscription.client.model.Product.QUICKBOOKS_PAYROLL -> Product.QUICKBOOKS_PAYROLL
        demo.craft.product.subscription.client.model.Product.QUICKBOOKS_PAYMENTS -> Product.QUICKBOOKS_PAYMENTS
        demo.craft.product.subscription.client.model.Product.TSHEETS -> Product.TSHEETS
    }