package demo.craft.user.profile.domain.mapper

import demo.craft.user.profile.domain.entity.BusinessProfile
import demo.craft.user.profile.domain.entity.BusinessProfileChangeRequest
import demo.craft.user.profile.domain.enums.ChangeRequestOperation
import demo.craft.user.profile.domain.enums.ChangeRequestStatus
import demo.craft.user.profile.domain.kafka.BusinessProfileChangeRequestKafkaPayload

fun BusinessProfile.toChangeRequest(requestId: String, operation: ChangeRequestOperation): BusinessProfileChangeRequest =
    BusinessProfileChangeRequest(
        requestId = requestId,
        userId = this.userId,
        operation = operation,
        companyName = this.companyName,
        legalName = this.legalName,
        businessAddress = this.businessAddress,
        legalAddress = this.legalAddress,
        pan = this.pan,
        ein = this.ein,
        email = this.email,
        website = this.website,
        status = ChangeRequestStatus.init()
    )

fun BusinessProfileChangeRequest.toBusinessProfile(): BusinessProfile =
    BusinessProfile(
        userId = this.userId,
        companyName = this.companyName,
        legalName = this.legalName,
        businessAddress = this.businessAddress,
        legalAddress = this.legalAddress,
        pan = this.pan,
        ein = this.ein,
        email = this.email,
        website = this.website,
    )

fun BusinessProfileChangeRequest.toKafkaPayload(): BusinessProfileChangeRequestKafkaPayload =
    BusinessProfileChangeRequestKafkaPayload(
        userId = this.userId,
        requestId = this.requestId,
        createdAt = this.createdAt!!
    )
