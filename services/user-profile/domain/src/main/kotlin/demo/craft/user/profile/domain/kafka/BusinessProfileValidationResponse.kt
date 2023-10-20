package demo.craft.user.profile.domain.kafka

import demo.craft.common.domain.enums.Product
import demo.craft.user.profile.domain.enums.ChangeRequestStatus
import demo.craft.user.profile.domain.enums.FieldName

data class BusinessProfileValidationResponse(
    val userId: String,
    val requestId: String,
    val product: Product,
    val status: ChangeRequestStatus,
    val failureReasons: List<Pair<FieldName, String>>
)