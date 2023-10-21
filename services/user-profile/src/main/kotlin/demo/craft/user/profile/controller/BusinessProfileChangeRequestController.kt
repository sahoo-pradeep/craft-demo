package demo.craft.user.profile.controller

import demo.craft.user.profile.api.BusinessProfileChangeRequestApi
import demo.craft.user.profile.mapper.toApiModel
import demo.craft.user.profile.model.GetBusinessProfileChangeRequestLatestStatusResponse
import demo.craft.user.profile.model.GetBusinessProfileChangeRequestStatusResponse
import demo.craft.user.profile.service.BusinessProfileChangeRequestService
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.RestController

@RestController
class BusinessProfileChangeRequestController(
    private val businessProfileChangeRequestService: BusinessProfileChangeRequestService
) : BusinessProfileChangeRequestApi {

    override fun getBusinessProfileChangeRequestStatus(
        xMinusUserMinusId: String,
        requestId: String
    ): ResponseEntity<GetBusinessProfileChangeRequestStatusResponse> =
        ResponseEntity.ok(
            GetBusinessProfileChangeRequestStatusResponse(
                businessProfileChangeRequestService.getBusinessProfileChangeRequest(xMinusUserMinusId, requestId).toApiModel()
            )
        )

    override fun getBusinessProfileChangeRequestLatestStatus(
        xMinusUserMinusId: String
    ): ResponseEntity<GetBusinessProfileChangeRequestLatestStatusResponse> =
        ResponseEntity.ok(
            GetBusinessProfileChangeRequestLatestStatusResponse(
                businessProfileChangeRequestService.getLatestBusinessProfileChangeRequest(xMinusUserMinusId).toApiModel()
            )
        )
}