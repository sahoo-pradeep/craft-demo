package demo.craft.user.profile.controller

import demo.craft.user.profile.api.BusinessProfileApi
import demo.craft.user.profile.mapper.toApiModel
import demo.craft.user.profile.mapper.toDomainModel
import demo.craft.user.profile.model.CreateBusinessProfileRequest
import demo.craft.user.profile.model.CreateBusinessProfileResponse
import demo.craft.user.profile.model.GetBusinessProfileResponse
import demo.craft.user.profile.model.UpdateBusinessProfileRequest
import demo.craft.user.profile.model.UpdateBusinessProfileResponse
import demo.craft.user.profile.service.BusinessProfileService
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.RestController

@RestController
class BusinessProfileController(
    private val businessProfileService: BusinessProfileService
) : BusinessProfileApi {
    override fun getBusinessProfile(xMinusUserMinusId: String): ResponseEntity<GetBusinessProfileResponse> =
        ResponseEntity.ok(
            GetBusinessProfileResponse(
                businessProfile = businessProfileService.getBusinessProfile(xMinusUserMinusId).toApiModel()
            )
        )

    override fun createBusinessProfile(
        xMinusUserMinusId: String,
        createBusinessProfileRequest: CreateBusinessProfileRequest
    ): ResponseEntity<CreateBusinessProfileResponse> =
        ResponseEntity.ok(
            CreateBusinessProfileResponse(
                requestUuid = businessProfileService.createBusinessProfile(
                    createBusinessProfileRequest.businessProfile.toDomainModel(xMinusUserMinusId)
                ).requestUuid
            )
        )

    override fun updateBusinessProfile(
        xMinusUserMinusId: String,
        updateBusinessProfileRequest: UpdateBusinessProfileRequest
    ): ResponseEntity<UpdateBusinessProfileResponse> =
        ResponseEntity.ok(
            UpdateBusinessProfileResponse(
                requestUuid = businessProfileService.updateBusinessProfile(
                    updateBusinessProfileRequest.businessProfile.toDomainModel(xMinusUserMinusId)
                ).requestUuid
            )
        )
}