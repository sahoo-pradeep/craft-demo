package demo.craft.user.profile.dao.access

import demo.craft.user.profile.common.domain.domain.entity.UpdateRequest
import demo.craft.user.profile.common.domain.domain.enums.UpdateRequestStatus

interface UpdateRequestAccess {
    /**
     * Get all update requests for the given user filter by [status].
     * If status is null, then status filter is not applied
     */
    fun getUpdateRequests(userId: String, status: UpdateRequestStatus? = null): List<UpdateRequest>

    /**
     * Creates a new update request for the user
     */
    fun createUpdateRequest(updateRequest: UpdateRequest): UpdateRequest

}