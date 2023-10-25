package demo.craft.user.profile.service

import demo.craft.user.profile.TestConstant
import demo.craft.user.profile.common.exception.BusinessProfileChangeRequestNotFoundException
import demo.craft.user.profile.common.exception.UnauthorizedUserException
import demo.craft.user.profile.dao.access.BusinessProfileChangeRequestAccess
import demo.craft.user.profile.dao.access.ChangeRequestFailureReasonAccess
import demo.craft.user.profile.dao.access.ChangeRequestProductStatusAccess
import demo.craft.user.profile.domain.model.BusinessProfileChangeRequestWrapper
import demo.craft.user.profile.domain.model.ChangeRequestProductStatusWrapper
import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import io.mockk.verify
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.test.context.junit.jupiter.SpringExtension

@ExtendWith(SpringExtension::class)
class BusinessProfileChangeRequestTestService {
    @MockK
    private lateinit var businessProfileChangeRequestAccess: BusinessProfileChangeRequestAccess

    @MockK
    private lateinit var changeRequestProductStatusAccess: ChangeRequestProductStatusAccess

    @MockK
    private lateinit var changeRequestFailureReasonAccess: ChangeRequestFailureReasonAccess

    @InjectMockKs
    private lateinit var businessProfileChangeRequestService: BusinessProfileChangeRequestService

    @Test
    fun `get business profile change request happy case`() {
        //given
        every {
            businessProfileChangeRequestAccess.findByRequestId(TestConstant.REQUEST_ID_1)
        } returns TestConstant.BUSINESS_PROFILE_CREATE_CHANGE_REQUEST_1

        every {
            changeRequestProductStatusAccess.findAllByRequestId(TestConstant.REQUEST_ID_1)
        } returns listOf(
            TestConstant.CHANGE_REQUEST_PRODUCT_STATUS_REJECTED_1,
            TestConstant.CHANGE_REQUEST_PRODUCT_STATUS_REJECTED_2
        )

        every {
            changeRequestFailureReasonAccess.findAllByRequestId(TestConstant.REQUEST_ID_1)
        } returns listOf(
            TestConstant.CHANGE_REQUEST_FAILURE_REASON_1,
            TestConstant.CHANGE_REQUEST_FAILURE_REASON_2
        )

        //when
        val changeRequest = businessProfileChangeRequestService.getBusinessProfileChangeRequest(TestConstant.USER_1, TestConstant.REQUEST_ID_1)

        //then
        val expectedResponse = BusinessProfileChangeRequestWrapper(
            changeRequest = TestConstant.BUSINESS_PROFILE_CREATE_CHANGE_REQUEST_1,
            productStatuses = listOf(
                ChangeRequestProductStatusWrapper(
                    productStatus = TestConstant.CHANGE_REQUEST_PRODUCT_STATUS_REJECTED_1,
                    failureReasons = listOf(
                        TestConstant.CHANGE_REQUEST_FAILURE_REASON_1
                    )
                ),
                ChangeRequestProductStatusWrapper(
                    productStatus = TestConstant.CHANGE_REQUEST_PRODUCT_STATUS_REJECTED_2,
                    failureReasons = listOf(
                        TestConstant.CHANGE_REQUEST_FAILURE_REASON_2
                    )
                )
            )
        )

        Assertions.assertEquals(expectedResponse, changeRequest)

        verify(exactly = 1) { businessProfileChangeRequestAccess.findByRequestId(TestConstant.REQUEST_ID_1) }
        verify(exactly = 1) { changeRequestProductStatusAccess.findAllByRequestId(TestConstant.REQUEST_ID_1) }
        verify(exactly = 1) { changeRequestFailureReasonAccess.findAllByRequestId(TestConstant.REQUEST_ID_1) }
    }

    @Test
    fun `get business profile change request for some other user`() {
        //given
        every {
            businessProfileChangeRequestAccess.findByRequestId(TestConstant.REQUEST_ID_1)
        } returns TestConstant.BUSINESS_PROFILE_CREATE_CHANGE_REQUEST_1

        //when
        val exception = assertThrows<Exception> {
            businessProfileChangeRequestService.getBusinessProfileChangeRequest(TestConstant.USER_2, TestConstant.REQUEST_ID_1)
        }

        //then
        Assertions.assertEquals(UnauthorizedUserException::class, exception::class)

        verify(exactly = 1) { businessProfileChangeRequestAccess.findByRequestId(TestConstant.REQUEST_ID_1) }
        verify(exactly = 0) { changeRequestProductStatusAccess.findAllByRequestId(TestConstant.REQUEST_ID_1) }
        verify(exactly = 0) { changeRequestFailureReasonAccess.findAllByRequestId(TestConstant.REQUEST_ID_1) }
    }

    @Test
    fun `get business profile change request with invalid request id`() {
        //given
        every {
            businessProfileChangeRequestAccess.findByRequestId(TestConstant.REQUEST_ID_1)
        } returns null

        //when
        val exception = assertThrows<Exception> {
            businessProfileChangeRequestService.getBusinessProfileChangeRequest(TestConstant.USER_2, TestConstant.REQUEST_ID_1)
        }

        //then
        Assertions.assertEquals(BusinessProfileChangeRequestNotFoundException::class, exception::class)

        verify(exactly = 1) { businessProfileChangeRequestAccess.findByRequestId(TestConstant.REQUEST_ID_1) }
        verify(exactly = 0) { changeRequestProductStatusAccess.findAllByRequestId(TestConstant.REQUEST_ID_1) }
        verify(exactly = 0) { changeRequestFailureReasonAccess.findAllByRequestId(TestConstant.REQUEST_ID_1) }
    }

    @Test
    fun `get latest business profile change request happy case`() {
        //given
        every {
            businessProfileChangeRequestAccess.findTopChangeRequest(TestConstant.USER_1)
        } returns TestConstant.BUSINESS_PROFILE_CREATE_CHANGE_REQUEST_1

        every {
            changeRequestProductStatusAccess.findAllByRequestId(TestConstant.REQUEST_ID_1)
        } returns listOf(
            TestConstant.CHANGE_REQUEST_PRODUCT_STATUS_REJECTED_1,
            TestConstant.CHANGE_REQUEST_PRODUCT_STATUS_REJECTED_2
        )

        every {
            changeRequestFailureReasonAccess.findAllByRequestId(TestConstant.REQUEST_ID_1)
        } returns listOf(
            TestConstant.CHANGE_REQUEST_FAILURE_REASON_1,
            TestConstant.CHANGE_REQUEST_FAILURE_REASON_2
        )

        //when
        val changeRequest = businessProfileChangeRequestService.getLatestBusinessProfileChangeRequest(TestConstant.USER_1)

        //then
        val expectedResponse = BusinessProfileChangeRequestWrapper(
            changeRequest = TestConstant.BUSINESS_PROFILE_CREATE_CHANGE_REQUEST_1,
            productStatuses = listOf(
                ChangeRequestProductStatusWrapper(
                    productStatus = TestConstant.CHANGE_REQUEST_PRODUCT_STATUS_REJECTED_1,
                    failureReasons = listOf(
                        TestConstant.CHANGE_REQUEST_FAILURE_REASON_1
                    )
                ),
                ChangeRequestProductStatusWrapper(
                    productStatus = TestConstant.CHANGE_REQUEST_PRODUCT_STATUS_REJECTED_2,
                    failureReasons = listOf(
                        TestConstant.CHANGE_REQUEST_FAILURE_REASON_2
                    )
                )
            )
        )

        Assertions.assertEquals(expectedResponse, changeRequest)

        verify(exactly = 1) { businessProfileChangeRequestAccess.findTopChangeRequest(TestConstant.USER_1) }
        verify(exactly = 1) { changeRequestProductStatusAccess.findAllByRequestId(TestConstant.REQUEST_ID_1) }
        verify(exactly = 1) { changeRequestFailureReasonAccess.findAllByRequestId(TestConstant.REQUEST_ID_1) }
    }

    @Test
    fun `get latest business profile change request when there is no existing request`() {
        //given
        every {
            businessProfileChangeRequestAccess.findTopChangeRequest(TestConstant.USER_1)
        } returns null

        //when
        val exception = assertThrows<Exception> {
            businessProfileChangeRequestService.getLatestBusinessProfileChangeRequest(TestConstant.USER_1)
        }

        //then
        Assertions.assertEquals(BusinessProfileChangeRequestNotFoundException::class, exception::class)

        verify(exactly = 1) { businessProfileChangeRequestAccess.findTopChangeRequest(TestConstant.USER_1) }
        verify(exactly = 0) { changeRequestProductStatusAccess.findAllByRequestId(TestConstant.REQUEST_ID_1) }
        verify(exactly = 0) { changeRequestFailureReasonAccess.findAllByRequestId(TestConstant.REQUEST_ID_1) }
    }
}