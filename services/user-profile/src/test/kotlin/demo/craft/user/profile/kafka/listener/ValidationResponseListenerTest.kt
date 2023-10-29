package demo.craft.user.profile.kafka.listener

import com.fasterxml.jackson.databind.SerializationFeature
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import demo.craft.user.profile.TestConstant.BUSINESS_PROFILE_1
import demo.craft.user.profile.TestConstant.BUSINESS_PROFILE_CREATE_CHANGE_REQUEST_ACCEPTED_1
import demo.craft.user.profile.TestConstant.BUSINESS_PROFILE_CREATE_CHANGE_REQUEST_IN_PROGRESS_1
import demo.craft.user.profile.TestConstant.BUSINESS_PROFILE_CREATE_CHANGE_REQUEST_REJECTED_1
import demo.craft.user.profile.TestConstant.BUSINESS_PROFILE_VALIDATION_RESPONSE_ACCEPTED_1
import demo.craft.user.profile.TestConstant.BUSINESS_PROFILE_VALIDATION_RESPONSE_REJECTED_1
import demo.craft.user.profile.TestConstant.BUSINESS_PROFILE_VALIDATION_RESPONSE_REJECTED_FAILURE_REASON_1
import demo.craft.user.profile.TestConstant.CHANGE_REQUEST_FAILURE_REASON_1
import demo.craft.user.profile.TestConstant.CHANGE_REQUEST_PRODUCT_STATUS_ACCEPTED_1
import demo.craft.user.profile.TestConstant.CHANGE_REQUEST_PRODUCT_STATUS_ACCEPTED_2
import demo.craft.user.profile.TestConstant.CHANGE_REQUEST_PRODUCT_STATUS_IN_PROGRESS_1
import demo.craft.user.profile.TestConstant.CHANGE_REQUEST_PRODUCT_STATUS_IN_PROGRESS_2
import demo.craft.user.profile.TestConstant.CHANGE_REQUEST_PRODUCT_STATUS_REJECTED_1
import demo.craft.user.profile.TestConstant.PRODUCT_1
import demo.craft.user.profile.TestConstant.REQUEST_ID_1
import demo.craft.user.profile.TestConstant.USER_1
import demo.craft.user.profile.TestLockManager
import demo.craft.user.profile.common.config.UserProfileProperties
import demo.craft.user.profile.dao.access.BusinessProfileAccess
import demo.craft.user.profile.dao.access.BusinessProfileChangeRequestAccess
import demo.craft.user.profile.dao.access.ChangeRequestFailureReasonAccess
import demo.craft.user.profile.dao.access.ChangeRequestProductStatusAccess
import demo.craft.user.profile.domain.enums.ChangeRequestStatus
import demo.craft.user.profile.lock.UserProfileLockManager
import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import io.mockk.impl.annotations.SpyK
import io.mockk.verify
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.test.context.junit.jupiter.SpringExtension

@ExtendWith(SpringExtension::class)
class ValidationResponseListenerTest {
    @MockK
    private lateinit var businessProfileAccess: BusinessProfileAccess

    @MockK
    private lateinit var businessProfileChangeRequestAccess: BusinessProfileChangeRequestAccess

    @MockK
    private lateinit var changeRequestProductStatusAccess: ChangeRequestProductStatusAccess

    @MockK
    private lateinit var changeRequestFailureReasonAccess: ChangeRequestFailureReasonAccess

    @SpyK
    private var userProfileProperties: UserProfileProperties = UserProfileProperties()

    @SpyK
    private var lockManager: UserProfileLockManager =
        object : UserProfileLockManager(TestLockManager(), UserProfileProperties()) {}

    @InjectMockKs
    private lateinit var validationResponseListener: ValidationResponseListener

    private val objectMapper = jacksonObjectMapper().apply {
        registerModule(JavaTimeModule()) // Register JavaTimeModule to handle Java 8 date/time types
        configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false)
    }

    @Test
    fun `validation response partially approved`() {
        // given
        every { changeRequestProductStatusAccess.findByRequestIdAndProduct(REQUEST_ID_1, PRODUCT_1) } returns CHANGE_REQUEST_PRODUCT_STATUS_IN_PROGRESS_1

        every {
            changeRequestProductStatusAccess.updateStatus(REQUEST_ID_1, PRODUCT_1, ChangeRequestStatus.ACCEPTED)
        } returns CHANGE_REQUEST_PRODUCT_STATUS_ACCEPTED_1

        every { businessProfileChangeRequestAccess.findByUserIdAndRequestId(USER_1, REQUEST_ID_1) } returns BUSINESS_PROFILE_CREATE_CHANGE_REQUEST_IN_PROGRESS_1

        every {
            changeRequestProductStatusAccess.findAllByRequestId(REQUEST_ID_1)
        } returns listOf(CHANGE_REQUEST_PRODUCT_STATUS_IN_PROGRESS_1, CHANGE_REQUEST_PRODUCT_STATUS_IN_PROGRESS_2)

        // when
        val kafkaPayload = objectMapper.writeValueAsString(BUSINESS_PROFILE_VALIDATION_RESPONSE_ACCEPTED_1)
        validationResponseListener.onMessage(kafkaPayload)

        // then
        verify(exactly = 1) { changeRequestProductStatusAccess.findByRequestIdAndProduct(REQUEST_ID_1, PRODUCT_1) }
        verify(exactly = 1) { changeRequestProductStatusAccess.findAllByRequestId(REQUEST_ID_1) }
        verify(exactly = 1) { changeRequestProductStatusAccess.updateStatus(REQUEST_ID_1, PRODUCT_1, ChangeRequestStatus.ACCEPTED) }
        verify(exactly = 1) { businessProfileChangeRequestAccess.findByUserIdAndRequestId(USER_1, REQUEST_ID_1) }
        verify(exactly = 0) { changeRequestFailureReasonAccess.saveAllFailureReason(any(), any(), any()) }
        verify(exactly = 0) { businessProfileChangeRequestAccess.updateStatus(any(), any(), any()) }
        verify(exactly = 0) { businessProfileAccess.createOrUpdateBusinessProfile(any()) }
    }

    @Test
    fun `validation response fully approved`() {
        // given
        every { changeRequestProductStatusAccess.findByRequestIdAndProduct(REQUEST_ID_1, PRODUCT_1) } returns CHANGE_REQUEST_PRODUCT_STATUS_IN_PROGRESS_1

        every {
            changeRequestProductStatusAccess.updateStatus(REQUEST_ID_1, PRODUCT_1, ChangeRequestStatus.ACCEPTED)
        } returns CHANGE_REQUEST_PRODUCT_STATUS_ACCEPTED_1

        every { businessProfileChangeRequestAccess.findByUserIdAndRequestId(USER_1, REQUEST_ID_1) } returns BUSINESS_PROFILE_CREATE_CHANGE_REQUEST_IN_PROGRESS_1

        every {
            changeRequestProductStatusAccess.findAllByRequestId(REQUEST_ID_1)
        } returns listOf(CHANGE_REQUEST_PRODUCT_STATUS_ACCEPTED_1, CHANGE_REQUEST_PRODUCT_STATUS_ACCEPTED_2)

        every {
            businessProfileChangeRequestAccess.updateStatus(USER_1, REQUEST_ID_1, ChangeRequestStatus.ACCEPTED)
        } returns BUSINESS_PROFILE_CREATE_CHANGE_REQUEST_ACCEPTED_1

        every { businessProfileAccess.createOrUpdateBusinessProfile(BUSINESS_PROFILE_CREATE_CHANGE_REQUEST_IN_PROGRESS_1) } returns BUSINESS_PROFILE_1

        // when
        val kafkaPayload = objectMapper.writeValueAsString(BUSINESS_PROFILE_VALIDATION_RESPONSE_ACCEPTED_1)
        validationResponseListener.onMessage(kafkaPayload)

        // then
        verify(exactly = 1) { changeRequestProductStatusAccess.findByRequestIdAndProduct(REQUEST_ID_1, PRODUCT_1) }
        verify(exactly = 1) { changeRequestProductStatusAccess.updateStatus(REQUEST_ID_1, PRODUCT_1, ChangeRequestStatus.ACCEPTED) }
        verify(exactly = 1) { businessProfileChangeRequestAccess.findByUserIdAndRequestId(USER_1, REQUEST_ID_1) }
        verify(exactly = 1) { changeRequestProductStatusAccess.findAllByRequestId(REQUEST_ID_1) }
        verify(exactly = 1) { businessProfileChangeRequestAccess.updateStatus(any(), any(), any()) }
        verify(exactly = 1) { businessProfileAccess.createOrUpdateBusinessProfile(any()) }
        verify(exactly = 0) { changeRequestFailureReasonAccess.saveAllFailureReason(any(), any(), any()) }
    }

    @Test
    fun `validation response partially rejected`() {
        // given
        every { changeRequestProductStatusAccess.findByRequestIdAndProduct(REQUEST_ID_1, PRODUCT_1) } returns CHANGE_REQUEST_PRODUCT_STATUS_IN_PROGRESS_1

        every {
            changeRequestProductStatusAccess.updateStatus(REQUEST_ID_1, PRODUCT_1, ChangeRequestStatus.REJECTED)
        } returns CHANGE_REQUEST_PRODUCT_STATUS_REJECTED_1

        every {
            changeRequestFailureReasonAccess.saveAllFailureReason(REQUEST_ID_1, PRODUCT_1, BUSINESS_PROFILE_VALIDATION_RESPONSE_REJECTED_FAILURE_REASON_1)
        } returns listOf(CHANGE_REQUEST_FAILURE_REASON_1)

        every { businessProfileChangeRequestAccess.findByUserIdAndRequestId(USER_1, REQUEST_ID_1) } returns BUSINESS_PROFILE_CREATE_CHANGE_REQUEST_IN_PROGRESS_1

        every {
            changeRequestProductStatusAccess.findAllByRequestId(REQUEST_ID_1)
        } returns listOf(CHANGE_REQUEST_PRODUCT_STATUS_REJECTED_1, CHANGE_REQUEST_PRODUCT_STATUS_IN_PROGRESS_2)

        every { businessProfileChangeRequestAccess.updateStatus(USER_1, REQUEST_ID_1, ChangeRequestStatus.REJECTED) } returns BUSINESS_PROFILE_CREATE_CHANGE_REQUEST_REJECTED_1

        // when
        val kafkaPayload = objectMapper.writeValueAsString(BUSINESS_PROFILE_VALIDATION_RESPONSE_REJECTED_1)
        validationResponseListener.onMessage(kafkaPayload)

        // then
        verify(exactly = 1) { changeRequestProductStatusAccess.findByRequestIdAndProduct(REQUEST_ID_1, PRODUCT_1) }
        verify(exactly = 1) { changeRequestProductStatusAccess.updateStatus(REQUEST_ID_1, PRODUCT_1, ChangeRequestStatus.REJECTED) }
        verify(exactly = 1) { changeRequestFailureReasonAccess.saveAllFailureReason(any(), any(), any()) }
        verify(exactly = 1) { businessProfileChangeRequestAccess.findByUserIdAndRequestId(USER_1, REQUEST_ID_1) }
        verify(exactly = 1) { changeRequestProductStatusAccess.findAllByRequestId(REQUEST_ID_1) }
        verify(exactly = 1) { businessProfileChangeRequestAccess.updateStatus(any(), any(), any()) }
        verify(exactly = 0) { businessProfileAccess.createOrUpdateBusinessProfile(any()) }
    }
}