package demo.craft.user.profile.service

import demo.craft.user.profile.TestConstant.BUSINESS_PROFILE_CREATE_CHANGE_REQUEST_ACCEPTED_1
import demo.craft.user.profile.TestConstant.BUSINESS_PROFILE_CREATE_CHANGE_REQUEST_ACCEPTED_2
import demo.craft.user.profile.TestConstant.BUSINESS_PROFILE_CREATE_CHANGE_REQUEST_ACCEPTED_3
import demo.craft.user.profile.TestConstant.BUSINESS_PROFILE_CREATE_CHANGE_REQUEST_IN_PROGRESS_1
import demo.craft.user.profile.TestConstant.BUSINESS_PROFILE_CREATE_CHANGE_REQUEST_IN_PROGRESS_2
import demo.craft.user.profile.TestConstant.BUSINESS_PROFILE_CREATE_CHANGE_REQUEST_REJECTED_2
import demo.craft.user.profile.TestConstant.CHANGE_REQUEST_FAILURE_REASON_3
import demo.craft.user.profile.TestConstant.CHANGE_REQUEST_FAILURE_REASON_4
import demo.craft.user.profile.TestConstant.CHANGE_REQUEST_PRODUCT_STATUS_REJECTED_3
import demo.craft.user.profile.TestConstant.CHANGE_REQUEST_PRODUCT_STATUS_REJECTED_4
import demo.craft.user.profile.TestConstant.REQUEST_ID_1
import demo.craft.user.profile.TestConstant.REQUEST_ID_2
import demo.craft.user.profile.TestConstant.USER_1
import demo.craft.user.profile.TestConstant.USER_2
import demo.craft.user.profile.common.exception.BusinessProfileChangeRequestNotFoundException
import demo.craft.user.profile.common.exception.UnauthorizedUserException
import demo.craft.user.profile.dao.access.BusinessProfileChangeRequestAccess
import demo.craft.user.profile.dao.access.ChangeRequestFailureReasonAccess
import demo.craft.user.profile.dao.access.ChangeRequestProductStatusAccess
import demo.craft.user.profile.domain.enums.ChangeRequestStatus
import demo.craft.user.profile.domain.enums.SortOrder
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
    fun `get business profile change request details with all product status rejected`() {
        //given
        every {
            businessProfileChangeRequestAccess.findByRequestId(REQUEST_ID_2)
        } returns BUSINESS_PROFILE_CREATE_CHANGE_REQUEST_REJECTED_2

        every {
            changeRequestProductStatusAccess.findAllByRequestId(REQUEST_ID_2)
        } returns listOf(
            CHANGE_REQUEST_PRODUCT_STATUS_REJECTED_3,
            CHANGE_REQUEST_PRODUCT_STATUS_REJECTED_4
        )

        every {
            changeRequestFailureReasonAccess.findAllByRequestId(REQUEST_ID_2)
        } returns listOf(
            CHANGE_REQUEST_FAILURE_REASON_3,
            CHANGE_REQUEST_FAILURE_REASON_4
        )

        //when
        val changeRequestWrapper = businessProfileChangeRequestService.getBusinessProfileChangeRequest(USER_1, REQUEST_ID_2)

        //then
        val expectedResponse = BusinessProfileChangeRequestWrapper(
            changeRequest = BUSINESS_PROFILE_CREATE_CHANGE_REQUEST_REJECTED_2,
            productStatuses = listOf(
                ChangeRequestProductStatusWrapper(
                    productStatus = CHANGE_REQUEST_PRODUCT_STATUS_REJECTED_3,
                    failureReasons = listOf(
                        CHANGE_REQUEST_FAILURE_REASON_3
                    )
                ),
                ChangeRequestProductStatusWrapper(
                    productStatus = CHANGE_REQUEST_PRODUCT_STATUS_REJECTED_4,
                    failureReasons = listOf(
                        CHANGE_REQUEST_FAILURE_REASON_4
                    )
                )
            )
        )

        Assertions.assertEquals(expectedResponse, changeRequestWrapper)
        if (changeRequestWrapper.productStatuses.any { it.productStatus.status == ChangeRequestStatus.REJECTED }) {
            Assertions.assertEquals(ChangeRequestStatus.REJECTED, changeRequestWrapper.changeRequest.status)
        }

        verify(exactly = 1) { businessProfileChangeRequestAccess.findByRequestId(REQUEST_ID_2) }
        verify(exactly = 1) { changeRequestProductStatusAccess.findAllByRequestId(REQUEST_ID_2) }
        verify(exactly = 1) { changeRequestFailureReasonAccess.findAllByRequestId(REQUEST_ID_2) }
    }

    @Test
    fun `get business profile change request details for some other user`() {
        //given
        every {
            businessProfileChangeRequestAccess.findByRequestId(REQUEST_ID_1)
        } returns BUSINESS_PROFILE_CREATE_CHANGE_REQUEST_IN_PROGRESS_1

        //when
        val exception = assertThrows<Exception> {
            businessProfileChangeRequestService.getBusinessProfileChangeRequest(USER_2, REQUEST_ID_1)
        }

        //then
        Assertions.assertEquals(UnauthorizedUserException::class, exception::class)

        verify(exactly = 1) { businessProfileChangeRequestAccess.findByRequestId(REQUEST_ID_1) }
        verify(exactly = 0) { changeRequestProductStatusAccess.findAllByRequestId(REQUEST_ID_1) }
        verify(exactly = 0) { changeRequestFailureReasonAccess.findAllByRequestId(REQUEST_ID_1) }
    }

    @Test
    fun `get business profile change request details with invalid request id`() {
        //given
        every {
            businessProfileChangeRequestAccess.findByRequestId(REQUEST_ID_1)
        } returns null

        //when
        val exception = assertThrows<Exception> {
            businessProfileChangeRequestService.getBusinessProfileChangeRequest(USER_2, REQUEST_ID_1)
        }

        //then
        Assertions.assertEquals(BusinessProfileChangeRequestNotFoundException::class, exception::class)

        verify(exactly = 1) { businessProfileChangeRequestAccess.findByRequestId(REQUEST_ID_1) }
        verify(exactly = 0) { changeRequestProductStatusAccess.findAllByRequestId(REQUEST_ID_1) }
        verify(exactly = 0) { changeRequestFailureReasonAccess.findAllByRequestId(REQUEST_ID_1) }
    }

    @Test
    fun `get all business profile change request with no filters`() {
        //given
        every {
            businessProfileChangeRequestAccess.findAllByUserId(USER_1)
        } returns listOf(BUSINESS_PROFILE_CREATE_CHANGE_REQUEST_ACCEPTED_1, BUSINESS_PROFILE_CREATE_CHANGE_REQUEST_ACCEPTED_2)

        //when
        val actualChangeRequests =
            businessProfileChangeRequestService.getAllBusinessProfileChangeRequestWithFilters(
                USER_1, null, 0, 10, SortOrder.ASC
            )

        //then
        val expectedResponse = listOf(BUSINESS_PROFILE_CREATE_CHANGE_REQUEST_ACCEPTED_1, BUSINESS_PROFILE_CREATE_CHANGE_REQUEST_ACCEPTED_2)

        Assertions.assertEquals(expectedResponse, actualChangeRequests)

        verify(exactly = 1) { businessProfileChangeRequestAccess.findAllByUserId(USER_1) }
    }

    @Test
    fun `get all business profile change requests in processing state`() {
        //given
        every {
            businessProfileChangeRequestAccess.findAllByUserId(USER_1)
        } returns listOf(BUSINESS_PROFILE_CREATE_CHANGE_REQUEST_ACCEPTED_1, BUSINESS_PROFILE_CREATE_CHANGE_REQUEST_IN_PROGRESS_2)

        //when
        val actualChangeRequests =
            businessProfileChangeRequestService.getAllBusinessProfileChangeRequestWithFilters(
                USER_1, ChangeRequestStatus.IN_PROGRESS, 0, 10, SortOrder.ASC
            )

        //then
        val expectedResponse = listOf(BUSINESS_PROFILE_CREATE_CHANGE_REQUEST_IN_PROGRESS_2)
        Assertions.assertEquals(expectedResponse, actualChangeRequests)
        verify(exactly = 1) { businessProfileChangeRequestAccess.findAllByUserId(USER_1) }
    }

    @Test
    fun `get all recent business profile change requests with page size as 2`() {
        //given
        every {
            businessProfileChangeRequestAccess.findAllByUserId(USER_1)
        } returns listOf(
            BUSINESS_PROFILE_CREATE_CHANGE_REQUEST_ACCEPTED_1,
            BUSINESS_PROFILE_CREATE_CHANGE_REQUEST_ACCEPTED_2,
            BUSINESS_PROFILE_CREATE_CHANGE_REQUEST_ACCEPTED_3
        )

        //when
        val actualChangeRequests =
            businessProfileChangeRequestService.getAllBusinessProfileChangeRequestWithFilters(
                USER_1, ChangeRequestStatus.ACCEPTED, 0, 2, SortOrder.DESC
            )

        //then
        val expectedResponse = listOf(BUSINESS_PROFILE_CREATE_CHANGE_REQUEST_ACCEPTED_3, BUSINESS_PROFILE_CREATE_CHANGE_REQUEST_ACCEPTED_2)
        Assertions.assertEquals(expectedResponse, actualChangeRequests)
        verify(exactly = 1) { businessProfileChangeRequestAccess.findAllByUserId(USER_1) }
    }
}