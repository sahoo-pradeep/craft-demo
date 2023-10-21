package demo.craft.user.profile.domain.model

import demo.craft.user.profile.domain.entity.BusinessProfileChangeRequest

data class BusinessProfileChangeRequestWrapper(
    val changeRequest: BusinessProfileChangeRequest,
    val productStatuses: List<ChangeRequestProductStatusWrapper>
)