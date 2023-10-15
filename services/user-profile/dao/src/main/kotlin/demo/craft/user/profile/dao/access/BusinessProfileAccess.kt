package demo.craft.user.profile.dao.access

import demo.craft.user.profile.common.domain.entity.BusinessProfile

interface BusinessProfileAccess {
    fun findByUserId(userId: String): BusinessProfile?

    fun createBusinessProfile(businessProfile: BusinessProfile): BusinessProfile
}