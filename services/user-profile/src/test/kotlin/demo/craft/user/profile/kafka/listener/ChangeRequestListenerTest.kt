package demo.craft.user.profile.kafka.listener

import com.fasterxml.jackson.databind.SerializationFeature
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import demo.craft.common.communication.kafka.KafkaPublisher
import demo.craft.user.profile.TestConstant
import demo.craft.user.profile.TestLockManager
import demo.craft.user.profile.common.config.UserProfileProperties
import demo.craft.user.profile.dao.access.BusinessProfileAccess
import demo.craft.user.profile.dao.access.BusinessProfileChangeRequestAccess
import demo.craft.user.profile.dao.access.ChangeRequestProductStatusAccess
import demo.craft.user.profile.integration.ProductSubscriptionIntegration
import demo.craft.user.profile.lock.UserProfileLockManager
import demo.craft.user.profile.service.BusinessProfileService
import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import io.mockk.impl.annotations.SpyK
import io.mockk.verify
import org.junit.jupiter.api.Test

import org.junit.jupiter.api.Assertions.*
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
        every { changeRequestProductStatusAccess.existsByRequestId(TestConstant.REQUEST_ID_1) } returns false
        every { businessProfileChangeRequestAccess.findByRequestId(TestConstant.REQUEST_ID_1) } returns TestConstant.BUSINESS_PROFILE_CREATE_CHANGE_REQUEST_1
        every { businessProfileAccess.findByUserId(TestConstant.USER_1) } returns TestConstant.BUSINESS_PROFILE_1
        every {
            productSubscriptionIntegration.getProductSubscriptions(TestConstant.USER_1)
        } returns listOf(TestConstant.PRODUCT_SUBSCRIPTION_1, TestConstant.PRODUCT_SUBSCRIPTION_2)
        every {
            changeRequestProductStatusAccess.createNewEntries(any())
        } returns listOf(TestConstant.CHANGE_REQUEST_PRODUCT_STATUS_IN_PROGRESS_1, TestConstant.CHANGE_REQUEST_PRODUCT_STATUS_IN_PROGRESS_1)
        every { kafkaPublisher.publish(any(), any(), any()) } returns Unit

        // when
        val kafkaPayload = objectMapper.writeValueAsString(TestConstant.BUSINESS_PROFILE_CHANGE_REQUEST_KAFKA_PAYLOAD_1)
        changeRequestListener.onMessage(kafkaPayload)

        verify(exactly = 1) { changeRequestProductStatusAccess.existsByRequestId(TestConstant.REQUEST_ID_1) }
        verify(exactly = 1) { businessProfileChangeRequestAccess.findByRequestId(TestConstant.REQUEST_ID_1) }
        verify(exactly = 1) { businessProfileAccess.findByUserId(TestConstant.USER_1) }
        verify(exactly = 1) { productSubscriptionIntegration.getProductSubscriptions(TestConstant.USER_1) }
        verify(exactly = 1) { changeRequestProductStatusAccess.createNewEntries(any()) }
        verify(exactly = 1) { kafkaPublisher.publish(any(), any(), any()) }
    }
}