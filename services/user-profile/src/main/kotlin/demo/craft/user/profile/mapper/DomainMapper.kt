package demo.craft.user.profile.mapper

import demo.craft.user.profile.common.domain.domain.entity.BusinessProfile
import demo.craft.user.profile.common.domain.domain.entity.UpdateRequest
import demo.craft.user.profile.common.domain.domain.enums.UpdateRequestOperation
import demo.craft.user.profile.common.domain.domain.enums.UpdateRequestStatus
import java.util.UUID

fun BusinessProfile.toUpdateRequest(operation: UpdateRequestOperation): UpdateRequest =
    UpdateRequest(
        requestUuid = UUID.randomUUID().toString(),
        operation = operation,
        companyName = this.companyName,
        legalName = this.legalName,
        businessAddress = this.businessAddress,
        legalAddress = this.legalAddress,
        pan = this.pan,
        ein = this.ein,
        email = this.email,
        website = this.website,
        userId = this.userId,
        status = UpdateRequestStatus.init()
    )