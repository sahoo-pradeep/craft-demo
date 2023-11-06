package demo.craft.user.profile.domain.model

import demo.craft.user.profile.domain.enums.FieldName

data class InvalidField(
    val fieldName: FieldName,
    val description: String
)
