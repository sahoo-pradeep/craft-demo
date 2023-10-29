package demo.craft.user.profile.service

import demo.craft.user.profile.common.exception.BusinessProfileChangeRequestNotFoundException
import demo.craft.user.profile.common.exception.UnauthorizedUserException
import demo.craft.user.profile.dao.access.BusinessProfileChangeRequestAccess
import demo.craft.user.profile.dao.access.ChangeRequestFailureReasonAccess
import demo.craft.user.profile.dao.access.ChangeRequestProductStatusAccess
import demo.craft.user.profile.domain.entity.BusinessProfileChangeRequest
import demo.craft.user.profile.domain.enums.ChangeRequestStatus
import demo.craft.user.profile.domain.enums.SortOrder
import demo.craft.user.profile.domain.model.BusinessProfileChangeRequestWrapper
import demo.craft.user.profile.domain.model.ChangeRequestProductStatusWrapper
import org.springframework.stereotype.Service

@Service
class BusinessProfileChangeRequestService(
    private val businessProfileChangeRequestAccess: BusinessProfileChangeRequestAccess,
    private val changeRequestProductStatusAccess: ChangeRequestProductStatusAccess,
    private val changeRequestFailureReasonAccess: ChangeRequestFailureReasonAccess,
) {

    fun getBusinessProfileChangeRequest(userId: String, requestId: String): BusinessProfileChangeRequestWrapper {
        val changeRequest = businessProfileChangeRequestAccess.findByRequestId(requestId)
            ?: throw BusinessProfileChangeRequestNotFoundException(userId, requestId)

        if (changeRequest.userId != userId) {
            throw UnauthorizedUserException(userId)
        }

        return getChangeRequestStatusDetails(changeRequest)
    }

    fun getAllBusinessProfileChangeRequestWithFilters(
        userId: String,
        status: ChangeRequestStatus?,
        page: Int,
        pageSize: Int,
        sortOrder: SortOrder
    ): List<BusinessProfileChangeRequest> {
        // add filters only if filters are not null
        val changeRequests = businessProfileChangeRequestAccess.findAllByUserId(userId)

        val sortedChangeRequests = when (sortOrder) {
            SortOrder.ASC -> changeRequests.sortedBy { it.id }
            SortOrder.DESC -> changeRequests.sortedByDescending { it.id }
        }

        return sortedChangeRequests
            .filter { changeRequest -> status?.let { changeRequest.status == it } ?: true }
            .drop(page * pageSize)
            .take(pageSize)
    }

    private fun getChangeRequestStatusDetails(
        changeRequest: BusinessProfileChangeRequest
    ): BusinessProfileChangeRequestWrapper {
        val productStatuses = changeRequestProductStatusAccess.findAllByRequestId(changeRequest.requestId)
        val failureReasons = changeRequestFailureReasonAccess.findAllByRequestId(changeRequest.requestId)

        return BusinessProfileChangeRequestWrapper(
            changeRequest = changeRequest,
            productStatuses = productStatuses.map {
                ChangeRequestProductStatusWrapper(
                    productStatus = it,
                    failureReasons = failureReasons.filter { failureReason -> failureReason.product == it.product }
                )
            }
        )
    }
}