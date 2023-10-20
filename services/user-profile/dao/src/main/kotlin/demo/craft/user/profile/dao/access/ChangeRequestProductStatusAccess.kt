package demo.craft.user.profile.dao.access

import demo.craft.common.domain.enums.Product
import demo.craft.user.profile.domain.entity.ChangeRequestProductStatus

interface ChangeRequestProductStatusAccess {
    /** Get change request product status by [requestId] */
    fun findByRequestId(requestId: String): List<ChangeRequestProductStatus>

    fun existsByRequestId(requestId: String): Boolean

    fun findByRequestIdAndProduct(requestId: String, product: Product): ChangeRequestProductStatus?

    fun saveAll(entities: List<ChangeRequestProductStatus>): List<ChangeRequestProductStatus>
}