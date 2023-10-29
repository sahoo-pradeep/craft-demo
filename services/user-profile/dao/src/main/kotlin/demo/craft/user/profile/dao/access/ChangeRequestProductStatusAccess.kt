package demo.craft.user.profile.dao.access

import demo.craft.common.domain.enums.Product
import demo.craft.user.profile.common.exception.ProductStatusIllegalStateException
import demo.craft.user.profile.domain.entity.ChangeRequestProductStatus
import demo.craft.user.profile.domain.enums.ChangeRequestStatus

interface ChangeRequestProductStatusAccess {
    /** Get all product status by [requestId] */
    fun findAllByRequestId(requestId: String): List<ChangeRequestProductStatus>

    /** Get product status by [requestId] and [product] */
    fun findByRequestIdAndProduct(requestId: String, product: Product): ChangeRequestProductStatus?

    /** Creates new product statuses */
    fun createNewProductStatuses(productStatuses: List<ChangeRequestProductStatus>): List<ChangeRequestProductStatus>

    /** Update product status for given [requestId] and [product] to [status]
     * @throws [ProductStatusIllegalStateException] if status update is not allowed
     */
    fun updateStatus(requestId: String, product: Product, status: ChangeRequestStatus): ChangeRequestProductStatus
}