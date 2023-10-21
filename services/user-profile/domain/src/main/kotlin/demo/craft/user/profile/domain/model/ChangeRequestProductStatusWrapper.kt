package demo.craft.user.profile.domain.model

import demo.craft.user.profile.domain.entity.ChangeRequestFailureReason
import demo.craft.user.profile.domain.entity.ChangeRequestProductStatus

data class ChangeRequestProductStatusWrapper(
    val productStatus: ChangeRequestProductStatus,
    val failureReasons: List<ChangeRequestFailureReason>
)
