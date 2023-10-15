package demo.craft.user.profile.mapper

import demo.craft.user.profile.common.domain.entity.Address
import demo.craft.user.profile.common.domain.entity.BusinessProfile

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
        businessAddress = this.businessAddress.toDomainModel(),
        legalAddress = this.legalAddress.toDomainModel(),
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

fun demo.craft.user.profile.model.Address.toDomainModel(): Address =
    Address(
        line1 = this.line1,
        line2 = this.line2,
        line3 = this.line3,
        city = this.city,
        state = this.state,
        zip = this.zip,
        country = this.country
    )