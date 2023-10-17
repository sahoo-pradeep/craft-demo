package demo.craft.user.profile.dao.access

import demo.craft.user.profile.common.domain.domain.entity.BusinessProfile
import demo.craft.user.profile.common.domain.exception.BusinessProfileAlreadyExistsException

interface BusinessProfileAccess {
    /**
     * Get business profile for the given user.
     * If business profile is not created yet, it will return null
     */
    fun findByUserId(userId: String): BusinessProfile?

    /**
     * Creates a new business profile for the given user.
     * If the business profile is already created, then it throws [BusinessProfileAlreadyExistsException]
     */
    fun createBusinessProfile(businessProfile: BusinessProfile): BusinessProfile
}