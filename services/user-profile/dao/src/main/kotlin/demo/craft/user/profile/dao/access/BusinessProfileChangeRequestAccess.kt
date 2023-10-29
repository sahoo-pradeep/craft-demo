package demo.craft.user.profile.dao.access

import demo.craft.user.profile.common.exception.BusinessProfileUpdateAlreadyInProgressException
import demo.craft.user.profile.domain.entity.BusinessProfileChangeRequest
import demo.craft.user.profile.domain.enums.ChangeRequestStatus

interface BusinessProfileChangeRequestAccess {
    /** Get all change requests for the given [userId] */
    fun findAllByUserId(userId: String): List<BusinessProfileChangeRequest>

    /** Get change request by [requestId] */
    fun findByRequestId(requestId: String): BusinessProfileChangeRequest?

    fun findTopChangeRequest(userId: String): BusinessProfileChangeRequest?

    /**
     * Creates a new change request for the user
     * @throws [BusinessProfileUpdateAlreadyInProgressException] if change request is already in progress
     */
    fun createChangeRequest(businessProfileChangeRequest: BusinessProfileChangeRequest): BusinessProfileChangeRequest

    fun updateStatus(userId: String, requestId: String, updatedStatus: ChangeRequestStatus): BusinessProfileChangeRequest
}