package demo.craft.user.profile.service

import com.fasterxml.jackson.databind.SerializationFeature
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import demo.craft.communication.kafka.KafkaPublisher
import demo.craft.user.profile.common.domain.config.UserProfileProperties
import demo.craft.user.profile.common.domain.domain.entity.BusinessProfile
import demo.craft.user.profile.common.domain.domain.entity.BusinessProfileChangeRequest
import demo.craft.user.profile.common.domain.domain.enums.ChangeRequestOperation
import demo.craft.user.profile.common.domain.exception.BusinessProfileAlreadyExistsException
import demo.craft.user.profile.common.domain.exception.BusinessProfileNotFoundException
import demo.craft.user.profile.common.domain.exception.InvalidBusinessProfileException
import demo.craft.user.profile.common.domain.exception.InvalidFieldAdditionalInfo
import demo.craft.user.profile.dao.access.BusinessProfileAccess
import demo.craft.user.profile.dao.access.BusinessProfileChangeRequestAccess
import demo.craft.user.profile.mapper.toChangeRequest
import demo.craft.user.profile.mapper.toKafkaPayload
import mu.KotlinLogging
import org.springframework.stereotype.Service

@Service
class BusinessProfileService(
    private val businessProfileAccess: BusinessProfileAccess,
    private val businessProfileChangeRequestAccess: BusinessProfileChangeRequestAccess,
    private val kafkaPublisher: KafkaPublisher,
    userProfileProperties: UserProfileProperties,
) {
    private val log = KotlinLogging.logger {}
    private val businessProfileProperties = userProfileProperties.businessProfile
    private val objectMapper = jacksonObjectMapper().apply {
        registerModule(JavaTimeModule()) // Register JavaTimeModule to handle Java 8 date/time types
        configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false)
    }

    fun getBusinessProfile(userId: String): BusinessProfile =
        businessProfileAccess.findByUserId(userId) ?: throw BusinessProfileNotFoundException(userId)

    // TODO: customer lock
    // TODO: logging context
    fun createBusinessProfile(businessProfile: BusinessProfile): BusinessProfileChangeRequest {
        val invalidFields = businessProfile.validateFields()
        if (invalidFields.isNotEmpty()) {
            throw InvalidBusinessProfileException(businessProfile.userId, InvalidFieldAdditionalInfo(invalidFields))
        }

        businessProfileAccess.findByUserId(businessProfile.userId)?.let {
            throw BusinessProfileAlreadyExistsException(businessProfile.userId)
        }

        val changeRequest =
            businessProfileChangeRequestAccess.createChangeRequest(businessProfile.toChangeRequest(ChangeRequestOperation.CREATE))
        log.info { "Request to create business profile is created successfully with requestUuid: ${changeRequest.requestId} " }

        kafkaPublisher.publish(
            businessProfileProperties.kafka.changeRequestTopicName,
            businessProfile.userId.hashCode(),
            objectMapper.writeValueAsString(changeRequest.toKafkaPayload())
        )

        return changeRequest
    }

    fun updateBusinessProfile(businessProfile: BusinessProfile): BusinessProfileChangeRequest {
        val invalidFields = businessProfile.validateFields()
        if (invalidFields.isNotEmpty()) {
            throw InvalidBusinessProfileException(businessProfile.userId, InvalidFieldAdditionalInfo(invalidFields))
        }

        // ensures that business profile is already created
        getBusinessProfile(businessProfile.userId)

        val changeRequest =
            businessProfileChangeRequestAccess.createChangeRequest(businessProfile.toChangeRequest(ChangeRequestOperation.UPDATE))
        log.info { "Request to update business profile is created successfully with requestUuid: ${changeRequest.requestId} " }

        kafkaPublisher.publish(
            businessProfileProperties.kafka.changeRequestTopicName,
            businessProfile.userId.hashCode(),
            objectMapper.writeValueAsString(changeRequest.toKafkaPayload())
        )

        return changeRequest
    }
}