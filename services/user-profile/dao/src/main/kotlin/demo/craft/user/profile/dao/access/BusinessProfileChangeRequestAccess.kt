package demo.craft.user.profile.dao.access

import demo.craft.user.profile.common.exception.BusinessProfileUpdateAlreadyInProgressException
import demo.craft.user.profile.domain.entity.BusinessProfileChangeRequest
import demo.craft.user.profile.domain.enums.ChangeRequestStatus

interface BusinessProfileChangeRequestAccess {
    /**
     * Get all change requests for the given [userId] filtered by [status].
     * If status is null, then status filter is not applied
     */
    fun findByUserIdAndStatus(userId: String, status: ChangeRequestStatus? = null): List<BusinessProfileChangeRequest>

    /** Get change request by [requestId] */
    fun findByRequestId(requestId: String): BusinessProfileChangeRequest?

    /**
     * Creates a new change request for the user
     * @throws [BusinessProfileUpdateAlreadyInProgressException] if change request is already in progress
     */
    fun createChangeRequest(businessProfileChangeRequest: BusinessProfileChangeRequest): BusinessProfileChangeRequest

    fun updateStatus(requestId: String, updatedStatus: ChangeRequestStatus): BusinessProfileChangeRequest
}