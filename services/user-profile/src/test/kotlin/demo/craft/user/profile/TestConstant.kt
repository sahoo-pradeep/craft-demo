package demo.craft.user.profile

import demo.craft.common.domain.enums.Product
import demo.craft.product.subscription.client.model.ProductSubscription
import demo.craft.user.profile.domain.entity.Address
import demo.craft.user.profile.domain.entity.BusinessProfile
import demo.craft.user.profile.domain.entity.BusinessProfileChangeRequest
import demo.craft.user.profile.domain.entity.ChangeRequestFailureReason
import demo.craft.user.profile.domain.entity.ChangeRequestProductStatus
import demo.craft.user.profile.domain.enums.ChangeRequestOperation
import demo.craft.user.profile.domain.enums.ChangeRequestStatus
import demo.craft.user.profile.domain.enums.FieldName
import demo.craft.user.profile.domain.kafka.BusinessProfileChangeRequestKafkaPayload
import demo.craft.user.profile.domain.kafka.BusinessProfileValidationResponse
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

object TestConstant {
    const val REQUEST_ID_1 = "00000000-0000-0000-0000-000000000001"
    const val REQUEST_ID_2 = "00000000-0000-0000-0000-000000000002"
    const val REQUEST_ID_3 = "00000000-0000-0000-0000-000000000003"
    const val USER_1 = "USER001"
    const val USER_2 = "USER002"
    const val COMPANY_NAME_1 = "COMPANY 1"
    const val LEGAL_NAME_1 = "COMPANY LEGAL 1"
    const val PAN_1 = "AAACA1111A"
    const val EIN_1 = "11-1111111"
    const val EMAIL_1 = "user001@craft.demo"
    const val WEBSITE_1 = "www.craft.demo"
    const val PAN_FAILURE_REASON = "invalid pan"
    const val EIN_FAILURE_REASON = "invalid ein"
    val DATE_TIME_1 = LocalDateTime.parse("2023-10-01T00:00:00", DateTimeFormatter.ISO_DATE_TIME)
    val DATE_TIME_2 = LocalDateTime.parse("2023-10-02T00:00:00", DateTimeFormatter.ISO_DATE_TIME)
    val DATE_TIME_3 = LocalDateTime.parse("2023-10-03T00:00:00", DateTimeFormatter.ISO_DATE_TIME)
    val PRODUCT_1 = Product.QUICKBOOKS_ACCOUNTING
    val PRODUCT_2 = Product.QUICKBOOKS_PAYROLL

    val ADDRESS_1 = Address(
        id = 1,
        userId = USER_1,
        line1 = "line 1",
        line2 = "line 2",
        line3 = "line 3",
        city = "city",
        state = "state",
        country = "country",
        zip = "111111",
        createdAt = DATE_TIME_1,
        updatedAt = DATE_TIME_1
    )

    val BUSINESS_PROFILE_1 = BusinessProfile(
        id = 1,
        userId = USER_1,
        companyName = COMPANY_NAME_1,
        legalName = LEGAL_NAME_1,
        pan = PAN_1,
        ein = EIN_1,
        email = EMAIL_1,
        website = WEBSITE_1,
        businessAddress = ADDRESS_1,
        legalAddress = ADDRESS_1,
        createdAt = DATE_TIME_1,
        updatedAt = DATE_TIME_1
    )

    val BUSINESS_PROFILE_CREATE_CHANGE_REQUEST_IN_PROGRESS_1 = BusinessProfileChangeRequest(
        id = 1,
        requestId = REQUEST_ID_1,
        userId = USER_1,
        operation = ChangeRequestOperation.CREATE,
        status = ChangeRequestStatus.IN_PROGRESS,
        companyName = COMPANY_NAME_1,
        legalName = LEGAL_NAME_1,
        pan = PAN_1,
        ein = EIN_1,
        email = EMAIL_1,
        website = WEBSITE_1,
        businessAddress = ADDRESS_1,
        legalAddress = ADDRESS_1,
        createdAt = DATE_TIME_1,
        updatedAt = DATE_TIME_1
    )

    val BUSINESS_PROFILE_CREATE_CHANGE_REQUEST_ACCEPTED_1 = BusinessProfileChangeRequest(
        id = 1,
        requestId = REQUEST_ID_1,
        userId = USER_1,
        operation = ChangeRequestOperation.CREATE,
        status = ChangeRequestStatus.ACCEPTED,
        companyName = COMPANY_NAME_1,
        legalName = LEGAL_NAME_1,
        pan = PAN_1,
        ein = EIN_1,
        email = EMAIL_1,
        website = WEBSITE_1,
        businessAddress = ADDRESS_1,
        legalAddress = ADDRESS_1,
        createdAt = DATE_TIME_1,
        updatedAt = DATE_TIME_1
    )

    val BUSINESS_PROFILE_CREATE_CHANGE_REQUEST_REJECTED_1 = BusinessProfileChangeRequest(
        id = 1,
        requestId = REQUEST_ID_1,
        userId = USER_1,
        operation = ChangeRequestOperation.CREATE,
        status = ChangeRequestStatus.REJECTED,
        companyName = COMPANY_NAME_1,
        legalName = LEGAL_NAME_1,
        pan = PAN_1,
        ein = EIN_1,
        email = EMAIL_1,
        website = WEBSITE_1,
        businessAddress = ADDRESS_1,
        legalAddress = ADDRESS_1,
        createdAt = DATE_TIME_1,
        updatedAt = DATE_TIME_1
    )

    val BUSINESS_PROFILE_CREATE_CHANGE_REQUEST_ACCEPTED_2 = BusinessProfileChangeRequest(
        id = 2,
        requestId = REQUEST_ID_2,
        userId = USER_1,
        operation = ChangeRequestOperation.UPDATE,
        status = ChangeRequestStatus.ACCEPTED,
        companyName = COMPANY_NAME_1,
        legalName = LEGAL_NAME_1,
        pan = PAN_1,
        ein = EIN_1,
        email = EMAIL_1,
        website = WEBSITE_1,
        businessAddress = ADDRESS_1,
        legalAddress = ADDRESS_1,
        createdAt = DATE_TIME_2,
        updatedAt = DATE_TIME_2
    )

    val BUSINESS_PROFILE_CREATE_CHANGE_REQUEST_REJECTED_2 = BusinessProfileChangeRequest(
        id = 2,
        requestId = REQUEST_ID_2,
        userId = USER_1,
        operation = ChangeRequestOperation.UPDATE,
        status = ChangeRequestStatus.REJECTED,
        companyName = COMPANY_NAME_1,
        legalName = LEGAL_NAME_1,
        pan = PAN_1,
        ein = EIN_1,
        email = EMAIL_1,
        website = WEBSITE_1,
        businessAddress = ADDRESS_1,
        legalAddress = ADDRESS_1,
        createdAt = DATE_TIME_2,
        updatedAt = DATE_TIME_2
    )

    val BUSINESS_PROFILE_CREATE_CHANGE_REQUEST_IN_PROGRESS_2 = BusinessProfileChangeRequest(
        id = 2,
        requestId = REQUEST_ID_2,
        userId = USER_1,
        operation = ChangeRequestOperation.UPDATE,
        status = ChangeRequestStatus.IN_PROGRESS,
        companyName = COMPANY_NAME_1,
        legalName = LEGAL_NAME_1,
        pan = PAN_1,
        ein = EIN_1,
        email = EMAIL_1,
        website = WEBSITE_1,
        businessAddress = ADDRESS_1,
        legalAddress = ADDRESS_1,
        createdAt = DATE_TIME_2,
        updatedAt = DATE_TIME_2
    )

    val BUSINESS_PROFILE_CREATE_CHANGE_REQUEST_ACCEPTED_3 = BusinessProfileChangeRequest(
        id = 3,
        requestId = REQUEST_ID_3,
        userId = USER_1,
        operation = ChangeRequestOperation.UPDATE,
        status = ChangeRequestStatus.ACCEPTED,
        companyName = COMPANY_NAME_1,
        legalName = LEGAL_NAME_1,
        pan = PAN_1,
        ein = EIN_1,
        email = EMAIL_1,
        website = WEBSITE_1,
        businessAddress = ADDRESS_1,
        legalAddress = ADDRESS_1,
        createdAt = DATE_TIME_3,
        updatedAt = DATE_TIME_3
    )

    val CHANGE_REQUEST_PRODUCT_STATUS_IN_PROGRESS_1 = ChangeRequestProductStatus(
        id = 1,
        requestId = REQUEST_ID_1,
        product = PRODUCT_1,
        status = ChangeRequestStatus.IN_PROGRESS,
        createdAt = DATE_TIME_1,
        updatedAt = DATE_TIME_1
    )

    val CHANGE_REQUEST_PRODUCT_STATUS_IN_PROGRESS_2 = ChangeRequestProductStatus(
        id = 2,
        requestId = REQUEST_ID_1,
        product = PRODUCT_2,
        status = ChangeRequestStatus.IN_PROGRESS,
        createdAt = DATE_TIME_1,
        updatedAt = DATE_TIME_1
    )

    val CHANGE_REQUEST_PRODUCT_STATUS_ACCEPTED_1 = ChangeRequestProductStatus(
        id = 1,
        requestId = REQUEST_ID_1,
        product = PRODUCT_1,
        status = ChangeRequestStatus.ACCEPTED,
        createdAt = DATE_TIME_1,
        updatedAt = DATE_TIME_1
    )

    val CHANGE_REQUEST_PRODUCT_STATUS_ACCEPTED_2 = ChangeRequestProductStatus(
        id = 2,
        requestId = REQUEST_ID_1,
        product = PRODUCT_2,
        status = ChangeRequestStatus.ACCEPTED,
        createdAt = DATE_TIME_1,
        updatedAt = DATE_TIME_1
    )

    val CHANGE_REQUEST_PRODUCT_STATUS_REJECTED_1 = ChangeRequestProductStatus(
        id = 1,
        requestId = REQUEST_ID_1,
        product = PRODUCT_1,
        status = ChangeRequestStatus.REJECTED,
        createdAt = DATE_TIME_1,
        updatedAt = DATE_TIME_1
    )

    val CHANGE_REQUEST_PRODUCT_STATUS_REJECTED_2 = ChangeRequestProductStatus(
        id = 2,
        requestId = REQUEST_ID_1,
        product = PRODUCT_2,
        status = ChangeRequestStatus.REJECTED,
        createdAt = DATE_TIME_1,
        updatedAt = DATE_TIME_1
    )

    val CHANGE_REQUEST_PRODUCT_STATUS_REJECTED_3 = ChangeRequestProductStatus(
        id = 3,
        requestId = REQUEST_ID_2,
        product = PRODUCT_1,
        status = ChangeRequestStatus.REJECTED,
        createdAt = DATE_TIME_1,
        updatedAt = DATE_TIME_1
    )

    val CHANGE_REQUEST_PRODUCT_STATUS_REJECTED_4 = ChangeRequestProductStatus(
        id = 4,
        requestId = REQUEST_ID_2,
        product = PRODUCT_2,
        status = ChangeRequestStatus.REJECTED,
        createdAt = DATE_TIME_1,
        updatedAt = DATE_TIME_1
    )

    val CHANGE_REQUEST_FAILURE_REASON_1 = ChangeRequestFailureReason(
        id = 3,
        requestId = REQUEST_ID_1,
        product = PRODUCT_1,
        field = FieldName.PAN,
        reason = PAN_FAILURE_REASON,
        createdAt = DATE_TIME_1
    )

    val CHANGE_REQUEST_FAILURE_REASON_3 = ChangeRequestFailureReason(
        id = 3,
        requestId = REQUEST_ID_2,
        product = PRODUCT_1,
        field = FieldName.PAN,
        reason = PAN_FAILURE_REASON,
        createdAt = DATE_TIME_1
    )

    val CHANGE_REQUEST_FAILURE_REASON_4 = ChangeRequestFailureReason(
        id = 4,
        requestId = REQUEST_ID_2,
        product = PRODUCT_2,
        field = FieldName.EIN,
        reason = EIN_FAILURE_REASON,
        createdAt = DATE_TIME_1
    )

    val PRODUCT_SUBSCRIPTION_1 =
        ProductSubscription()
            .product(demo.craft.product.subscription.client.model.Product.QUICKBOOKS_ACCOUNTING)
            .status(demo.craft.product.subscription.client.model.ProductSubscriptionStatus.ACTIVE)

    val PRODUCT_SUBSCRIPTION_2 =
        ProductSubscription()
            .product(demo.craft.product.subscription.client.model.Product.QUICKBOOKS_PAYROLL)
            .status(demo.craft.product.subscription.client.model.ProductSubscriptionStatus.ACTIVE)

    val BUSINESS_PROFILE_CHANGE_REQUEST_KAFKA_PAYLOAD_1 = BusinessProfileChangeRequestKafkaPayload(
        userId = USER_1,
        requestId = REQUEST_ID_1,
        createdAt = DATE_TIME_1
    )

    val BUSINESS_PROFILE_VALIDATION_RESPONSE_ACCEPTED_1 = BusinessProfileValidationResponse(
        userId = USER_1,
        requestId = REQUEST_ID_1,
        product = PRODUCT_1,
        status = ChangeRequestStatus.ACCEPTED,
        failureReasons = listOf()
    )

    val BUSINESS_PROFILE_VALIDATION_RESPONSE_REJECTED_FAILURE_REASON_1: List<Pair<FieldName, String>> = listOf(
        Pair(FieldName.PAN, PAN_FAILURE_REASON)
    )

    val BUSINESS_PROFILE_VALIDATION_RESPONSE_REJECTED_1 = BusinessProfileValidationResponse(
        userId = USER_1,
        requestId = REQUEST_ID_1,
        product = PRODUCT_1,
        status = ChangeRequestStatus.REJECTED,
        failureReasons = BUSINESS_PROFILE_VALIDATION_RESPONSE_REJECTED_FAILURE_REASON_1
    )
}