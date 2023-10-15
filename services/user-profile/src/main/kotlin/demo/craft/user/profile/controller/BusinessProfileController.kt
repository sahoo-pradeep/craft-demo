package demo.craft.user.profile.controller

import demo.craft.user.profile.api.BusinessProfileApi
import demo.craft.user.profile.mapper.toApiModel
import demo.craft.user.profile.mapper.toDomainModel
import demo.craft.user.profile.model.BusinessProfileResponse
import demo.craft.user.profile.model.CreateBusinessProfileRequest
import demo.craft.user.profile.service.BusinessProfileService
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.RestController

@RestController
class BusinessProfileController(
    private val businessProfileService: BusinessProfileService
) : BusinessProfileApi {
    override fun getBusinessProfile(xMinusUserMinusId: String): ResponseEntity<BusinessProfileResponse> =
        ResponseEntity.ok(
            BusinessProfileResponse(
                businessProfile = businessProfileService.getBusinessProfile(xMinusUserMinusId).toApiModel()
            )
        )

    override fun createBusinessProfile(
        xMinusUserMinusId: String, createBusinessProfileRequest: CreateBusinessProfileRequest
    ): ResponseEntity<BusinessProfileResponse> =
        ResponseEntity.ok(
            BusinessProfileResponse(
                businessProfile = businessProfileService.createBusinessProfile(
                    createBusinessProfileRequest.businessProfile.toDomainModel(xMinusUserMinusId)
                ).toApiModel()
            )
        )
}