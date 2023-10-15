package demo.craft.user.profile.service

import demo.craft.user.profile.common.domain.entity.BusinessProfile
import demo.craft.user.profile.common.domain.exception.BusinessProfileAlreadyExistsException
import demo.craft.user.profile.common.domain.exception.BusinessProfileNotFoundException
import demo.craft.user.profile.dao.access.BusinessProfileAccess
import mu.KotlinLogging
import org.springframework.stereotype.Service

@Service
class BusinessProfileService(
    private val businessProfileAccess: BusinessProfileAccess,
) {
    private val log = KotlinLogging.logger {}

    fun getBusinessProfile(userId: String): BusinessProfile =
        businessProfileAccess.findByUserId(userId) ?: throw BusinessProfileNotFoundException(userId)

    fun createBusinessProfile(businessProfile: BusinessProfile): BusinessProfile {
        businessProfileAccess.findByUserId(businessProfile.userId)?.let {
            log.error { "Business profile already exist for user" }
            throw BusinessProfileAlreadyExistsException(businessProfile.userId)
        }

        businessProfile.validate()

        return businessProfileAccess.createBusinessProfile(businessProfile).also {
            log.info { "Business profile created successfully for userId ${businessProfile.userId}" }
        }
    }
}