package demo.craft.user.profile.mapper

import demo.craft.user.profile.domain.entity.BusinessProfile
import demo.craft.user.profile.domain.entity.BusinessProfileChangeRequest
import demo.craft.user.profile.domain.enums.ChangeRequestOperation
import demo.craft.user.profile.domain.enums.ChangeRequestStatus
import demo.craft.user.profile.domain.kafka.BusinessProfileChangeRequestKafkaPayload
import java.util.UUID

fun BusinessProfile.toChangeRequest(operation: ChangeRequestOperation): BusinessProfileChangeRequest =
    BusinessProfileChangeRequest(
        requestId = UUID.randomUUID().toString(),
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
        status = ChangeRequestStatus.init()
    )

fun BusinessProfileChangeRequest.toKafkaPayload(): BusinessProfileChangeRequestKafkaPayload =
    BusinessProfileChangeRequestKafkaPayload(
        userId = this.userId,
        requestId = this.requestId,
        createdAt = this.createdAt!!
    )