package demo.craft.user.profile.dao.access

import demo.craft.user.profile.domain.entity.ChangeRequestFailureReason

interface ChangeRequestFailureReasonAccess {
    /** Get change request failure reasons by [requestId] */
    fun findByRequestId(requestId: String): List<ChangeRequestFailureReason>
}
