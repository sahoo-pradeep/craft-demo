package demo.craft.user.profile.domain.kafka

import demo.craft.user.profile.domain.entity.BusinessProfile
import demo.craft.user.profile.domain.entity.BusinessProfileChangeRequest

data class BusinessProfileChangeRequestProductValidationRequest(
    val currentProfile: BusinessProfile,
    val profileChangeRequest: BusinessProfileChangeRequest
)