package demo.craft.user.profile.dao.access

import demo.craft.user.profile.domain.entity.BusinessProfile
import demo.craft.user.profile.domain.entity.BusinessProfileChangeRequest

interface BusinessProfileAccess {
    /**
     * Get business profile for the given user.
     * If business profile is not created yet, it will return null
     */
    fun findByUserId(userId: String): BusinessProfile?

    /** Create a new business profile or updates it if it already exists  */
    fun createOrUpdateBusinessProfile(changeRequest: BusinessProfileChangeRequest): BusinessProfile
}