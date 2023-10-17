package demo.craft.user.profile.service

import demo.craft.user.profile.common.domain.domain.entity.BusinessProfile
import demo.craft.user.profile.common.domain.domain.entity.UpdateRequest
import demo.craft.user.profile.common.domain.domain.enums.UpdateRequestOperation
import demo.craft.user.profile.common.domain.domain.enums.UpdateRequestStatus.IN_PROGRESS
import demo.craft.user.profile.common.domain.exception.BusinessProfileAlreadyExistsException
import demo.craft.user.profile.common.domain.exception.BusinessProfileNotFoundException
import demo.craft.user.profile.common.domain.exception.BusinessProfileUpdateAlreadyInProgressException
import demo.craft.user.profile.common.domain.exception.InvalidBusinessProfileException
import demo.craft.user.profile.common.domain.exception.InvalidFieldAdditionalInfo
import demo.craft.user.profile.dao.access.BusinessProfileAccess
import demo.craft.user.profile.dao.access.UpdateRequestAccess
import demo.craft.user.profile.mapper.toUpdateRequest
import mu.KotlinLogging
import org.springframework.stereotype.Service

@Service
class BusinessProfileService(
    private val businessProfileAccess: BusinessProfileAccess,
    private val updateRequestAccess: UpdateRequestAccess
) {
    private val log = KotlinLogging.logger {}

    fun getBusinessProfile(userId: String): BusinessProfile =
        businessProfileAccess.findByUserId(userId) ?: throw BusinessProfileNotFoundException(userId)

    // TODO: customer lock
    // TODO: logging context
    fun createBusinessProfile(businessProfile: BusinessProfile): UpdateRequest {
        val invalidFields = businessProfile.validateFields()
        if (invalidFields.isNotEmpty()) {
            throw InvalidBusinessProfileException(businessProfile.userId, InvalidFieldAdditionalInfo(invalidFields))
        }

        businessProfileAccess.findByUserId(businessProfile.userId)?.let {
            throw BusinessProfileAlreadyExistsException(businessProfile.userId)
        }

        val updateRequest =
            updateRequestAccess.createUpdateRequest(businessProfile.toUpdateRequest(UpdateRequestOperation.CREATE))
        log.info { "Request to create business profile is created successfully with requestUuid: ${updateRequest.requestUuid} " }

        // TODO: publish to kafka

        return updateRequest
    }

    fun updateBusinessProfile(businessProfile: BusinessProfile): UpdateRequest {
        val invalidFields = businessProfile.validateFields()
        if (invalidFields.isNotEmpty()) {
            throw InvalidBusinessProfileException(businessProfile.userId, InvalidFieldAdditionalInfo(invalidFields))
        }

        // ensures that business profile is already created
        getBusinessProfile(businessProfile.userId)

        val updateRequest =
            updateRequestAccess.createUpdateRequest(businessProfile.toUpdateRequest(UpdateRequestOperation.UPDATE))
        log.info { "Request to update business profile is created successfully with requestUuid: ${updateRequest.requestUuid} " }

        // TODO: publish to kafka

        return updateRequest
    }
}