package demo.craft.user.profile.dao.access

import demo.craft.user.profile.common.exception.BusinessProfileChangeRequestIllegalStateException
import demo.craft.user.profile.common.exception.BusinessProfileUpdateAlreadyInProgressException
import demo.craft.user.profile.domain.entity.BusinessProfileChangeRequest
import demo.craft.user.profile.domain.enums.ChangeRequestStatus

interface BusinessProfileChangeRequestAccess {
    /** Get all change requests for the given [userId] */
    fun findAllByUserId(userId: String): List<BusinessProfileChangeRequest>

    /** Get change request of a given [userId] with given [requestId] */
    fun findByUserIdAndRequestId(userId: String, requestId: String): BusinessProfileChangeRequest?

    /**
     * Creates a new change request for the user
     * @throws [BusinessProfileUpdateAlreadyInProgressException] if change request is already in progress
     */
    fun createChangeRequest(businessProfileChangeRequest: BusinessProfileChangeRequest): BusinessProfileChangeRequest

    /**
     * Updates status of the change request with given [requestId]
     * @throws [BusinessProfileChangeRequestIllegalStateException] when status update is illegal
     */
    fun updateStatus(userId: String, requestId: String, updatedStatus: ChangeRequestStatus): BusinessProfileChangeRequest
}