package demo.craft.user.profile.dao.access

import demo.craft.user.profile.domain.entity.BusinessProfileChangeRequest
import demo.craft.user.profile.domain.enums.ChangeRequestStatus

interface BusinessProfileChangeRequestAccess {
    /**
     * Get all change requests for the given user filter by [status].
     * If status is null, then status filter is not applied
     */
    fun getChangeRequests(userId: String, status: ChangeRequestStatus? = null): List<BusinessProfileChangeRequest>

    /**
     * Creates a new change request for the user
     */
    fun createChangeRequest(businessProfileChangeRequest: BusinessProfileChangeRequest): BusinessProfileChangeRequest

}