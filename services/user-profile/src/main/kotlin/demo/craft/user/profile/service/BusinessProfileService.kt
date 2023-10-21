package demo.craft.user.profile.service

import com.fasterxml.jackson.databind.SerializationFeature
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import demo.craft.common.communication.kafka.KafkaPublisher
import demo.craft.user.profile.common.config.UserProfileProperties
import demo.craft.user.profile.common.exception.BusinessProfileAlreadyExistsException
import demo.craft.user.profile.common.exception.BusinessProfileNotFoundException
import demo.craft.user.profile.common.exception.InvalidBusinessProfileException
import demo.craft.user.profile.dao.access.BusinessProfileAccess
import demo.craft.user.profile.dao.access.BusinessProfileChangeRequestAccess
import demo.craft.user.profile.domain.entity.BusinessProfile
import demo.craft.user.profile.domain.entity.BusinessProfileChangeRequest
import demo.craft.user.profile.domain.enums.ChangeRequestOperation
import demo.craft.user.profile.domain.mapper.toChangeRequest
import demo.craft.user.profile.domain.mapper.toKafkaPayload
import demo.craft.user.profile.lock.UserProfileLockManager
import demo.craft.user.profile.mapper.toKeyValueString
import mu.KotlinLogging
import org.springframework.stereotype.Service

@Service
class BusinessProfileService(
    private val businessProfileAccess: BusinessProfileAccess,
    private val businessProfileChangeRequestAccess: BusinessProfileChangeRequestAccess,
    private val kafkaPublisher: KafkaPublisher,
    private val lockManager: UserProfileLockManager,
    userProfileProperties: UserProfileProperties,
) {
    private val log = KotlinLogging.logger {}
    private val kafkaProperties = userProfileProperties.kafka
    private val objectMapper = jacksonObjectMapper().apply {
        registerModule(JavaTimeModule()) // Register JavaTimeModule to handle Java 8 date/time types
        configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false)
    }

    fun getBusinessProfile(userId: String): BusinessProfile =
        businessProfileAccess.findByUserId(userId) ?: throw BusinessProfileNotFoundException(userId)

    // TODO: logging context
    fun createBusinessProfile(businessProfile: BusinessProfile): BusinessProfileChangeRequest {
        val invalidFields = businessProfile.validateFields()
        if (invalidFields.isNotEmpty()) {
            throw InvalidBusinessProfileException(businessProfile.userId, invalidFields.toKeyValueString())
        }

        businessProfileAccess.findByUserId(businessProfile.userId)?.let {
            throw BusinessProfileAlreadyExistsException(businessProfile.userId)
        }

        return lockManager.doExclusively(businessProfile.userId) {
            val changeRequest =
                businessProfileChangeRequestAccess.createChangeRequest(
                    businessProfile.toChangeRequest(
                        ChangeRequestOperation.CREATE
                    )
                )
            log.info { "Request to create business profile is created successfully with requestUuid: ${changeRequest.requestId} " }

            kafkaPublisher.publish(
                kafkaProperties.businessProfileChangeRequestTopic,
                businessProfile.userId.hashCode(),
                objectMapper.writeValueAsString(changeRequest.toKafkaPayload())
            )

            changeRequest
        }
    }

    fun updateBusinessProfile(businessProfile: BusinessProfile): BusinessProfileChangeRequest {
        val invalidFields = businessProfile.validateFields()
        if (invalidFields.isNotEmpty()) {
            throw InvalidBusinessProfileException(businessProfile.userId, invalidFields.toKeyValueString())
        }

        // ensures that business profile is already created
        getBusinessProfile(businessProfile.userId)

        return lockManager.doExclusively(businessProfile.userId) {
            val changeRequest =
                businessProfileChangeRequestAccess.createChangeRequest(
                    businessProfile.toChangeRequest(
                        ChangeRequestOperation.UPDATE
                    )
                )
            log.info { "Request to update business profile is created successfully with requestUuid: ${changeRequest.requestId} " }

            kafkaPublisher.publish(
                kafkaProperties.businessProfileChangeRequestTopic,
                businessProfile.userId.hashCode(),
                objectMapper.writeValueAsString(changeRequest.toKafkaPayload())
            )

            changeRequest
        }
    }
}