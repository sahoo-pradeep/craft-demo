package demo.craft.user.profile.common.domain.domain.model

import demo.craft.user.profile.common.domain.domain.enums.FieldName

data class InvalidField(
    val fieldName: FieldName,
    val reason: String
)