package demo.craft.user.profile.dao.access

import demo.craft.common.domain.enums.Product
import demo.craft.user.profile.domain.entity.ChangeRequestProductStatus
import demo.craft.user.profile.domain.enums.ChangeRequestStatus

interface ChangeRequestProductStatusAccess {
    /** Get change request product status by [requestId] */
    fun findAllByRequestId(requestId: String): List<ChangeRequestProductStatus>

    fun existsByRequestId(requestId: String): Boolean

    fun findByRequestIdAndProduct(requestId: String, product: Product): ChangeRequestProductStatus?

    fun createNewEntries(entities: List<ChangeRequestProductStatus>): List<ChangeRequestProductStatus>

    fun updateStatus(requestId: String, product: Product, status: ChangeRequestStatus): ChangeRequestProductStatus
}