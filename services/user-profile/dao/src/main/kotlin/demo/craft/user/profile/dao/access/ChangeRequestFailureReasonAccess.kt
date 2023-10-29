package demo.craft.user.profile.dao.access

import demo.craft.common.domain.enums.Product
import demo.craft.user.profile.domain.entity.ChangeRequestFailureReason
import demo.craft.user.profile.domain.enums.FieldName

interface ChangeRequestFailureReasonAccess {
    /** Get all failure reasons by [requestId] */
    fun findAllByRequestId(requestId: String): List<ChangeRequestFailureReason>

    /** save all failure reasons for given [requestId] amd [product] */
    fun saveAllFailureReason(
        requestId: String,
        product: Product,
        failureReasons: List<Pair<FieldName, String>>
    ): List<ChangeRequestFailureReason>
}
