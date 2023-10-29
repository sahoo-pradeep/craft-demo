package demo.craft.user.profile.mapper

import demo.craft.common.domain.enums.Product
import demo.craft.user.profile.domain.entity.Address
import demo.craft.user.profile.domain.entity.BusinessProfile
import demo.craft.user.profile.domain.entity.BusinessProfileChangeRequest
import demo.craft.user.profile.domain.entity.ChangeRequestFailureReason
import demo.craft.user.profile.domain.enums.ChangeRequestOperation
import demo.craft.user.profile.domain.enums.ChangeRequestStatus
import demo.craft.user.profile.domain.enums.FieldName
import demo.craft.user.profile.domain.enums.SortOrder
import demo.craft.user.profile.domain.model.ChangeRequestProductStatusWrapper

fun BusinessProfile.toApiModel(): demo.craft.user.profile.model.BusinessProfile =
    demo.craft.user.profile.model.BusinessProfile(
        companyName = this.companyName,
        legalName = this.legalName,
        businessAddress = this.businessAddress.toApiModel(),
        legalAddress = this.legalAddress.toApiModel(),
        pan = this.pan,
        ein = this.ein,
        email = this.email,
        website = this.website,
    )

fun demo.craft.user.profile.model.BusinessProfile.toDomainModel(userId: String): BusinessProfile =
    BusinessProfile(
        userId = userId,
        companyName = this.companyName,
        legalName = this.legalName,
        businessAddress = this.businessAddress.toDomainModel(userId),
        legalAddress = this.legalAddress.toDomainModel(userId),
        pan = this.pan,
        ein = this.ein,
        email = this.email,
        website = this.website,
    )

fun Address.toApiModel(): demo.craft.user.profile.model.Address =
    demo.craft.user.profile.model.Address(
        line1 = this.line1,
        line2 = this.line2,
        line3 = this.line3,
        city = this.city,
        state = this.state,
        zip = this.zip,
        country = this.country
    )

fun demo.craft.user.profile.model.Address.toDomainModel(userId: String): Address =
    Address(
        userId = userId,
        line1 = this.line1,
        line2 = this.line2,
        line3 = this.line3,
        city = this.city,
        state = this.state,
        zip = this.zip,
        country = this.country
    )

fun List<Pair<FieldName, String>>.toKeyValueString() =
    this.joinToString(",", "[", "]") { "{field = ${it.first}, reason = '${it.second}'}" }

fun BusinessProfileChangeRequest.toApiModel(): demo.craft.user.profile.model.BusinessProfileChangeRequestStatus =
    demo.craft.user.profile.model.BusinessProfileChangeRequestStatus(
        requestId = this.requestId,
        operation = this.operation.toApiModel(),
        status = this.status.toApiModel()
    )

fun ChangeRequestProductStatusWrapper.toApiModel(): demo.craft.user.profile.model.ChangeRequestProductStatus =
    demo.craft.user.profile.model.ChangeRequestProductStatus(
        product = this.productStatus.product.toApiModel(),
        status = this.productStatus.status.toApiModel(),
        failureReasons = this.failureReasons.map { it.toApiModel() }
    )

fun ChangeRequestOperation.toApiModel(): demo.craft.user.profile.model.ChangeRequestOperation =
    demo.craft.user.profile.model.ChangeRequestOperation.valueOf(this.name)

private fun ChangeRequestStatus.toApiModel(): demo.craft.user.profile.model.ChangeRequestStatus =
    demo.craft.user.profile.model.ChangeRequestStatus.valueOf(this.name)

fun demo.craft.user.profile.model.ChangeRequestStatus.toDomainModel(): ChangeRequestStatus =
    ChangeRequestStatus.valueOf(this.name)

private fun Product.toApiModel(): demo.craft.user.profile.model.Product =
    demo.craft.user.profile.model.Product.valueOf(this.name)

fun demo.craft.user.profile.model.SortOrder.toDomain(): SortOrder =
    SortOrder.valueOf(this.name)

private fun ChangeRequestFailureReason.toApiModel(): demo.craft.user.profile.model.ProductFailureReason =
    demo.craft.user.profile.model.ProductFailureReason(
        field = this.field.toApiModel(),
        reason = this.reason,
    )

private fun FieldName.toApiModel(): demo.craft.user.profile.model.BusinessProfileFieldName =
    demo.craft.user.profile.model.BusinessProfileFieldName.valueOf(this.name)
