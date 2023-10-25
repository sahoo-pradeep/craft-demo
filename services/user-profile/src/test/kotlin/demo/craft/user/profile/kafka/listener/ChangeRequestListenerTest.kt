package demo.craft.user.profile.kafka.listener

import com.fasterxml.jackson.databind.SerializationFeature
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import demo.craft.common.communication.kafka.KafkaPublisher
import demo.craft.user.profile.TestConstant.BUSINESS_PROFILE_1
import demo.craft.user.profile.TestConstant.BUSINESS_PROFILE_CHANGE_REQUEST_KAFKA_PAYLOAD_1
import demo.craft.user.profile.TestConstant.BUSINESS_PROFILE_CREATE_CHANGE_REQUEST_1
import demo.craft.user.profile.TestConstant.CHANGE_REQUEST_PRODUCT_STATUS_IN_PROGRESS_1
import demo.craft.user.profile.TestConstant.PRODUCT_SUBSCRIPTION_1
import demo.craft.user.profile.TestConstant.PRODUCT_SUBSCRIPTION_2
import demo.craft.user.profile.TestConstant.REQUEST_ID_1
import demo.craft.user.profile.TestConstant.USER_1
import demo.craft.user.profile.TestLockManager
import demo.craft.user.profile.common.config.UserProfileProperties
import demo.craft.user.profile.common.exception.BusinessProfileChangeRequestNotFoundException
import demo.craft.user.profile.dao.access.BusinessProfileAccess
import demo.craft.user.profile.dao.access.BusinessProfileChangeRequestAccess
import demo.craft.user.profile.dao.access.ChangeRequestProductStatusAccess
import demo.craft.user.profile.domain.enums.ChangeRequestStatus.FAILED
import demo.craft.user.profile.integration.ProductSubscriptionIntegration
import demo.craft.user.profile.lock.UserProfileLockManager
import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import io.mockk.impl.annotations.SpyK
import io.mockk.verify
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.test.context.junit.jupiter.SpringExtension

@ExtendWith(SpringExtension::class)
class ChangeRequestListenerTest {
    @MockK
    private lateinit var businessProfileAccess: BusinessProfileAccess

    @MockK
    private lateinit var businessProfileChangeRequestAccess: BusinessProfileChangeRequestAccess

    @MockK
    private lateinit var changeRequestProductStatusAccess: ChangeRequestProductStatusAccess

    @MockK
    private lateinit var productSubscriptionIntegration: ProductSubscriptionIntegration

    @MockK
    private lateinit var kafkaPublisher: KafkaPublisher

    @SpyK
    private var userProfileProperties: UserProfileProperties = UserProfileProperties()

    @SpyK
    private var lockManager: UserProfileLockManager =
        object : UserProfileLockManager(TestLockManager(), UserProfileProperties()) {}

    @InjectMockKs
    private lateinit var changeRequestListener: ChangeRequestListener

    private val objectMapper = jacksonObjectMapper().apply {
        registerModule(JavaTimeModule()) // Register JavaTimeModule to handle Java 8 date/time types
        configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false)
    }

    @Test
    fun `process change request happy case`() {
        // given
        every { changeRequestProductStatusAccess.existsByRequestId(REQUEST_ID_1) } returns false
        every { businessProfileChangeRequestAccess.findByRequestId(REQUEST_ID_1) } returns BUSINESS_PROFILE_CREATE_CHANGE_REQUEST_1
        every { businessProfileAccess.findByUserId(USER_1) } returns BUSINESS_PROFILE_1
        every {
            productSubscriptionIntegration.getProductSubscriptions(USER_1)
        } returns listOf(PRODUCT_SUBSCRIPTION_1, PRODUCT_SUBSCRIPTION_2)
        every {
            changeRequestProductStatusAccess.createNewEntries(any())
        } returns listOf(CHANGE_REQUEST_PRODUCT_STATUS_IN_PROGRESS_1, CHANGE_REQUEST_PRODUCT_STATUS_IN_PROGRESS_1)
        every { kafkaPublisher.publish(any(), any(), any()) } returns Unit

        // when
        val kafkaPayload = objectMapper.writeValueAsString(BUSINESS_PROFILE_CHANGE_REQUEST_KAFKA_PAYLOAD_1)
        changeRequestListener.onMessage(kafkaPayload)

        // then
        verify(exactly = 1) { changeRequestProductStatusAccess.existsByRequestId(REQUEST_ID_1) }
        verify(exactly = 1) { businessProfileChangeRequestAccess.findByRequestId(REQUEST_ID_1) }
        verify(exactly = 1) { businessProfileAccess.findByUserId(USER_1) }
        verify(exactly = 1) { productSubscriptionIntegration.getProductSubscriptions(USER_1) }
        verify(exactly = 1) { changeRequestProductStatusAccess.createNewEntries(any()) }
        verify(exactly = 1) { kafkaPublisher.publish(any(), any(), any()) }
    }

    @Test
    fun `process change request with request id already persisted`() {
        // given
        every { changeRequestProductStatusAccess.existsByRequestId(REQUEST_ID_1) } returns true

        // when
        val kafkaPayload = objectMapper.writeValueAsString(BUSINESS_PROFILE_CHANGE_REQUEST_KAFKA_PAYLOAD_1)
        changeRequestListener.onMessage(kafkaPayload)

        // then
        verify(exactly = 1) { changeRequestProductStatusAccess.existsByRequestId(REQUEST_ID_1) }
        verify(exactly = 0) { businessProfileChangeRequestAccess.findByRequestId(REQUEST_ID_1) }
        verify(exactly = 0) { businessProfileAccess.findByUserId(USER_1) }
        verify(exactly = 0) { productSubscriptionIntegration.getProductSubscriptions(USER_1) }
        verify(exactly = 0) { changeRequestProductStatusAccess.createNewEntries(any()) }
        verify(exactly = 0) { kafkaPublisher.publish(any(), any(), any()) }
    }

    @Test
    fun `process change request with invalid request id`() {
        // given
        every { changeRequestProductStatusAccess.existsByRequestId(REQUEST_ID_1) } returns false
        every { businessProfileChangeRequestAccess.findByRequestId(REQUEST_ID_1) } returns null

        // when
        val kafkaPayload = objectMapper.writeValueAsString(BUSINESS_PROFILE_CHANGE_REQUEST_KAFKA_PAYLOAD_1)
        val exception = assertThrows<Exception> { changeRequestListener.onMessage(kafkaPayload) }

        // then
        assertEquals(BusinessProfileChangeRequestNotFoundException::class, exception::class)

        verify(exactly = 1) { changeRequestProductStatusAccess.existsByRequestId(REQUEST_ID_1) }
        verify(exactly = 1) { businessProfileChangeRequestAccess.findByRequestId(REQUEST_ID_1) }
        verify(exactly = 0) { businessProfileAccess.findByUserId(USER_1) }
        verify(exactly = 0) { productSubscriptionIntegration.getProductSubscriptions(USER_1) }
        verify(exactly = 0) { changeRequestProductStatusAccess.createNewEntries(any()) }
        verify(exactly = 0) { kafkaPublisher.publish(any(), any(), any()) }
    }

    @Test
    fun `process change request with no product subscribed by user`() {
        // given
        every { changeRequestProductStatusAccess.existsByRequestId(REQUEST_ID_1) } returns false
        every { businessProfileChangeRequestAccess.findByRequestId(REQUEST_ID_1) } returns BUSINESS_PROFILE_CREATE_CHANGE_REQUEST_1
        every { businessProfileAccess.findByUserId(USER_1) } returns BUSINESS_PROFILE_1
        every {
            productSubscriptionIntegration.getProductSubscriptions(USER_1)
        } returns listOf()
        every {
            businessProfileChangeRequestAccess.updateStatus(USER_1, REQUEST_ID_1, FAILED)
        } returns BUSINESS_PROFILE_CREATE_CHANGE_REQUEST_1.copy(status = FAILED)

        // when
        val kafkaPayload = objectMapper.writeValueAsString(BUSINESS_PROFILE_CHANGE_REQUEST_KAFKA_PAYLOAD_1)
        changeRequestListener.onMessage(kafkaPayload)

        // then
        verify(exactly = 1) { changeRequestProductStatusAccess.existsByRequestId(REQUEST_ID_1) }
        verify(exactly = 1) { businessProfileChangeRequestAccess.findByRequestId(REQUEST_ID_1) }
        verify(exactly = 1) { businessProfileChangeRequestAccess.updateStatus(USER_1, REQUEST_ID_1, FAILED) }
        verify(exactly = 1) { businessProfileAccess.findByUserId(USER_1) }
        verify(exactly = 1) { productSubscriptionIntegration.getProductSubscriptions(USER_1) }
        verify(exactly = 0) { changeRequestProductStatusAccess.createNewEntries(any()) }
        verify(exactly = 0) { kafkaPublisher.publish(any(), any(), any()) }
    }

}