package demo.craft.user.profile.mapper

import demo.craft.user.profile.domain.entity.Address
import demo.craft.user.profile.domain.entity.BusinessProfile

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