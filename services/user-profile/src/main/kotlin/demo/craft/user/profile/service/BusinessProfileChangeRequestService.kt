package demo.craft.user.profile.service

import demo.craft.user.profile.common.exception.BusinessProfileChangeRequestNotFoundException
import demo.craft.user.profile.common.exception.UnauthorizedUserException
import demo.craft.user.profile.dao.access.BusinessProfileChangeRequestAccess
import demo.craft.user.profile.dao.access.ChangeRequestFailureReasonAccess
import demo.craft.user.profile.dao.access.ChangeRequestProductStatusAccess
import demo.craft.user.profile.domain.entity.BusinessProfileChangeRequest
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
        return businessProfileChangeRequestAccess.findByRequestId(requestId)?.let {
            if (it.userId != userId) {
                throw UnauthorizedUserException(userId)
            } else {
                getCompleteChangeRequestStatus(it)
            }
        } ?: throw BusinessProfileChangeRequestNotFoundException(userId, requestId)
    }

    fun getLatestBusinessProfileChangeRequest(userId: String): BusinessProfileChangeRequestWrapper {
        return businessProfileChangeRequestAccess.findTopChangeRequest(userId)?.let {
            getCompleteChangeRequestStatus(it)
        } ?: throw BusinessProfileChangeRequestNotFoundException(userId, null)
    }

    private fun getCompleteChangeRequestStatus(
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