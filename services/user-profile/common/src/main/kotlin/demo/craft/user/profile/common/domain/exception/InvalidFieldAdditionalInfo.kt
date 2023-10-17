package demo.craft.user.profile.common.domain.exception

import demo.craft.user.profile.common.domain.domain.model.InvalidField

data class InvalidFieldAdditionalInfo(
    val invalidFields: List<InvalidField>
) : ApiErrorAdditionalInfo(
    type = "INVALID_FIELD"
)