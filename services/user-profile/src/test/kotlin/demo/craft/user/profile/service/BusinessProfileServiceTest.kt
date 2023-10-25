package demo.craft.user.profile.service

import demo.craft.common.communication.kafka.KafkaPublisher
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
import java.util.concurrent.TimeoutException

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
        every { businessProfileAccess.findByUserId(TestConstant.USER_1) } returns TestConstant.BUSINESS_PROFILE_1

        //when
        val businessProfile = businessProfileService.getBusinessProfile(TestConstant.USER_1)

        //then
        assertEquals(businessProfile, TestConstant.BUSINESS_PROFILE_1)

        verify(exactly = 1) { businessProfileAccess.findByUserId(TestConstant.USER_1) }
    }

    @Test
    fun `get business profile when it doesn't exist`() {
        //given
        every { businessProfileAccess.findByUserId(TestConstant.USER_1) } returns null

        //when
        val exception = assertThrows<Exception> { businessProfileService.getBusinessProfile(TestConstant.USER_1) }

        //then
        assertEquals(BusinessProfileNotFoundException::class, exception::class)
        verify(exactly = 1) { businessProfileAccess.findByUserId(TestConstant.USER_1) }
    }

    @Test
    fun `create business profile happy case`() {
        //given
        every { businessProfileAccess.findByUserId(TestConstant.USER_1) } returns null
        every { kafkaPublisher.publish(any(), any(), any()) } returns Unit
        every {
            businessProfileChangeRequestAccess.createChangeRequest(any())
        } returns TestConstant.BUSINESS_PROFILE_CREATE_CHANGE_REQUEST_1

        // when
        val changeRequest = businessProfileService.createBusinessProfile(TestConstant.BUSINESS_PROFILE_1)

        // then
        assertEquals(TestConstant.BUSINESS_PROFILE_CREATE_CHANGE_REQUEST_1, changeRequest)

        verify(exactly = 1) { businessProfileAccess.findByUserId(TestConstant.USER_1) }
        verify(exactly = 1) { businessProfileChangeRequestAccess.createChangeRequest(any()) }
        verify(exactly = 1) { kafkaPublisher.publish(any(), any(), any()) }
    }

    @Test
    fun `create business profile with invalid request`() {
        //given
        val invalidRequest = TestConstant.BUSINESS_PROFILE_1.copy(
            companyName = "",
            pan = "1234567890"
        )
        // when
        val exception = assertThrows<Exception> { businessProfileService.createBusinessProfile(invalidRequest) }

        // then
        assertEquals(InvalidBusinessProfileException::class, exception::class)

        verify(exactly = 0) { businessProfileAccess.findByUserId(TestConstant.USER_1) }
        verify(exactly = 0) { businessProfileChangeRequestAccess.createChangeRequest(any()) }
        verify(exactly = 0) { kafkaPublisher.publish(any(), any(), any()) }
    }

    @Test
    fun `create business profile when it already exists`() {
        //given
        every { businessProfileAccess.findByUserId(TestConstant.USER_1) } returns TestConstant.BUSINESS_PROFILE_1

        // when
        val exception = assertThrows<Exception> { businessProfileService.createBusinessProfile(TestConstant.BUSINESS_PROFILE_1) }

        // then
        assertEquals(BusinessProfileAlreadyExistsException::class, exception::class)

        verify(exactly = 1) { businessProfileAccess.findByUserId(TestConstant.USER_1) }
        verify(exactly = 0) { businessProfileChangeRequestAccess.createChangeRequest(any()) }
        verify(exactly = 0) { kafkaPublisher.publish(any(), any(), any()) }
    }
}