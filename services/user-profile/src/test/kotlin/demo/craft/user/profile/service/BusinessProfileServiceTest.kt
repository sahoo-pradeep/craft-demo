package demo.craft.user.profile.service

import demo.craft.common.communication.kafka.KafkaPublisher
import demo.craft.user.profile.TestConstant.BUSINESS_PROFILE_1
import demo.craft.user.profile.TestConstant.BUSINESS_PROFILE_CREATE_CHANGE_REQUEST_1
import demo.craft.user.profile.TestConstant.USER_1
import demo.craft.user.profile.TestLockManager
import demo.craft.user.profile.common.config.UserProfileProperties
import demo.craft.user.profile.common.exception.BusinessProfileAlreadyExistsException
import demo.craft.user.profile.common.exception.BusinessProfileNotFoundException
import demo.craft.user.profile.common.exception.InvalidBusinessProfileException
import demo.craft.user.profile.dao.access.BusinessProfileAccess
import demo.craft.user.profile.dao.access.BusinessProfileChangeRequestAccess
import demo.craft.user.profile.lock.UserProfileLockManager
import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import io.mockk.impl.annotations.SpyK
import io.mockk.verify
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.test.context.junit.jupiter.SpringExtension

@ExtendWith(SpringExtension::class)
class BusinessProfileServiceTest {
    @MockK
    private lateinit var businessProfileAccess: BusinessProfileAccess

    @MockK
    private lateinit var businessProfileChangeRequestAccess: BusinessProfileChangeRequestAccess

    @MockK
    private lateinit var kafkaPublisher: KafkaPublisher

    @SpyK
    private var userProfileProperties: UserProfileProperties = UserProfileProperties()

    @SpyK
    private var lockManager: UserProfileLockManager =
        object : UserProfileLockManager(TestLockManager(), UserProfileProperties()) {}

    @InjectMockKs
    private lateinit var businessProfileService: BusinessProfileService

    @Test
    fun `get business profile happy case`() {
        //given
        every { businessProfileAccess.findByUserId(USER_1) } returns BUSINESS_PROFILE_1

        //when
        val businessProfile = businessProfileService.getBusinessProfile(USER_1)

        //then
        assertEquals(businessProfile, BUSINESS_PROFILE_1)

        verify(exactly = 1) { businessProfileAccess.findByUserId(USER_1) }
    }

    @Test
    fun `get business profile when it doesn't exist`() {
        //given
        every { businessProfileAccess.findByUserId(USER_1) } returns null

        //when
        val exception = assertThrows<Exception> { businessProfileService.getBusinessProfile(USER_1) }

        //then
        assertEquals(BusinessProfileNotFoundException::class, exception::class)
        verify(exactly = 1) { businessProfileAccess.findByUserId(USER_1) }
    }

    @Test
    fun `create business profile happy case`() {
        //given
        every { businessProfileAccess.findByUserId(USER_1) } returns null
        every { kafkaPublisher.publish(any(), any(), any()) } returns Unit
        every { businessProfileChangeRequestAccess.createChangeRequest(any()) } returns BUSINESS_PROFILE_CREATE_CHANGE_REQUEST_1

        // when
        val changeRequest = businessProfileService.createBusinessProfile(BUSINESS_PROFILE_1)

        // then
        assertEquals(BUSINESS_PROFILE_CREATE_CHANGE_REQUEST_1, changeRequest)

        verify(exactly = 1) { businessProfileAccess.findByUserId(USER_1) }
        verify(exactly = 1) { businessProfileChangeRequestAccess.createChangeRequest(any()) }
        verify(exactly = 1) { kafkaPublisher.publish(any(), any(), any()) }
    }

    @Test
    fun `create business profile with invalid request`() {
        //given
        val invalidRequest = BUSINESS_PROFILE_1.copy(
            companyName = "",
            pan = "1234567890"
        )
        // when
        val exception = assertThrows<Exception> { businessProfileService.createBusinessProfile(invalidRequest) }

        // then
        assertEquals(InvalidBusinessProfileException::class, exception::class)

        verify(exactly = 0) { businessProfileAccess.findByUserId(USER_1) }
        verify(exactly = 0) { businessProfileChangeRequestAccess.createChangeRequest(any()) }
        verify(exactly = 0) { kafkaPublisher.publish(any(), any(), any()) }
    }

    @Test
    fun `create business profile when it already exists`() {
        //given
        every { businessProfileAccess.findByUserId(USER_1) } returns BUSINESS_PROFILE_1

        // when
        val exception = assertThrows<Exception> { businessProfileService.createBusinessProfile(BUSINESS_PROFILE_1) }

        // then
        assertEquals(BusinessProfileAlreadyExistsException::class, exception::class)

        verify(exactly = 1) { businessProfileAccess.findByUserId(USER_1) }
        verify(exactly = 0) { businessProfileChangeRequestAccess.createChangeRequest(any()) }
        verify(exactly = 0) { kafkaPublisher.publish(any(), any(), any()) }
    }

    @Test
    fun `create business profile when kafka publish fails`() {
        //given
        every { businessProfileAccess.findByUserId(USER_1) } returns null
        every { kafkaPublisher.publish(any(), any(), any()) } throws RuntimeException()
        every { businessProfileChangeRequestAccess.createChangeRequest(any()) } returns BUSINESS_PROFILE_CREATE_CHANGE_REQUEST_1

        // when
        val changeRequest = businessProfileService.createBusinessProfile(BUSINESS_PROFILE_1)

        // then
        assertEquals(BUSINESS_PROFILE_CREATE_CHANGE_REQUEST_1, changeRequest)

        verify(exactly = 1) { businessProfileAccess.findByUserId(USER_1) }
        verify(exactly = 1) { businessProfileChangeRequestAccess.createChangeRequest(any()) }
        verify(exactly = 1) { kafkaPublisher.publish(any(), any(), any()) }
    }

    @Test
    fun `update business profile happy case`() {
        //given
        every { businessProfileAccess.findByUserId(USER_1) } returns BUSINESS_PROFILE_1
        every { kafkaPublisher.publish(any(), any(), any()) } returns Unit
        every { businessProfileChangeRequestAccess.createChangeRequest(any()) } returns BUSINESS_PROFILE_CREATE_CHANGE_REQUEST_1

        // when
        val changeRequest = businessProfileService.updateBusinessProfile(BUSINESS_PROFILE_1)

        // then
        assertEquals(BUSINESS_PROFILE_CREATE_CHANGE_REQUEST_1, changeRequest)

        verify(exactly = 1) { businessProfileAccess.findByUserId(USER_1) }
        verify(exactly = 1) { businessProfileChangeRequestAccess.createChangeRequest(any()) }
        verify(exactly = 1) { kafkaPublisher.publish(any(), any(), any()) }
    }

    @Test
    fun `update business profile with invalid request`() {
        //given
        val invalidRequest = BUSINESS_PROFILE_1.copy(
            companyName = "",
            pan = "1234567890"
        )
        // when
        val exception = assertThrows<Exception> { businessProfileService.updateBusinessProfile(invalidRequest) }

        // then
        assertEquals(InvalidBusinessProfileException::class, exception::class)

        verify(exactly = 0) { businessProfileAccess.findByUserId(USER_1) }
        verify(exactly = 0) { businessProfileChangeRequestAccess.createChangeRequest(any()) }
        verify(exactly = 0) { kafkaPublisher.publish(any(), any(), any()) }
    }

    @Test
    fun `update business profile when business profile doesn't exist`() {
        //given
        every { businessProfileAccess.findByUserId(USER_1) } returns null

        // when
        val exception = assertThrows<Exception> { businessProfileService.updateBusinessProfile(BUSINESS_PROFILE_1) }

        // then
        assertEquals(BusinessProfileNotFoundException::class, exception::class)

        verify(exactly = 1) { businessProfileAccess.findByUserId(USER_1) }
        verify(exactly = 0) { businessProfileChangeRequestAccess.createChangeRequest(any()) }
        verify(exactly = 0) { kafkaPublisher.publish(any(), any(), any()) }
    }
}