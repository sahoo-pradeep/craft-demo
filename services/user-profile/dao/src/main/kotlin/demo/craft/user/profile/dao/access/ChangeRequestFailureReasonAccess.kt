package demo.craft.user.profile.dao.access

import demo.craft.common.domain.enums.Product
import demo.craft.user.profile.domain.entity.ChangeRequestFailureReason
import demo.craft.user.profile.domain.enums.FieldName

interface ChangeRequestFailureReasonAccess {
    /** Get change request failure reasons by [requestId] */
    fun findByRequestId(requestId: String): List<ChangeRequestFailureReason>

    fun saveAllFailureReason(
        requestId: String,
        product: Product,
        failureReasons: List<Pair<FieldName, String>>
    ): List<ChangeRequestFailureReason>
}
