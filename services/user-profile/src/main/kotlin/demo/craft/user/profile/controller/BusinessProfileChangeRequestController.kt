package demo.craft.user.profile.controller

import demo.craft.user.profile.api.BusinessProfileChangeRequestApi
import demo.craft.user.profile.mapper.toApiModel
import demo.craft.user.profile.mapper.toDomain
import demo.craft.user.profile.mapper.toDomainModel
import demo.craft.user.profile.model.ChangeRequestStatus
import demo.craft.user.profile.model.GetAllBusinessProfileChangeRequestWithFiltersResponse
import demo.craft.user.profile.model.GetBusinessProfileChangeRequestDetailsResponse
import demo.craft.user.profile.model.SortOrder
import demo.craft.user.profile.service.BusinessProfileChangeRequestService
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.RestController

@RestController
class BusinessProfileChangeRequestController(
    private val businessProfileChangeRequestService: BusinessProfileChangeRequestService
) : BusinessProfileChangeRequestApi {

    override fun getBusinessProfileChangeRequestDetails(
        xMinusUserMinusId: String,
        requestId: String
    ): ResponseEntity<GetBusinessProfileChangeRequestDetailsResponse> =
        businessProfileChangeRequestService.getBusinessProfileChangeRequest(xMinusUserMinusId, requestId).let {
            ResponseEntity.ok(
                GetBusinessProfileChangeRequestDetailsResponse(
                    it.changeRequest.toApiModel(),
                    it.productStatuses.map { productStatus -> productStatus.toApiModel() }
                )
            )
        }

    override fun getAllBusinessProfileChangeRequest(
        xMinusUserMinusId: String,
        page: Int,
        size: Int,
        sort: SortOrder,
        status: ChangeRequestStatus?
    ): ResponseEntity<GetAllBusinessProfileChangeRequestWithFiltersResponse> =
        ResponseEntity.ok(
            GetAllBusinessProfileChangeRequestWithFiltersResponse(
                businessProfileChangeRequestService.getAllBusinessProfileChangeRequestWithFilters(
                    xMinusUserMinusId, status?.toDomainModel(), page, size, sort.toDomain()
                ).map { it.toApiModel() }
            )
        )
}