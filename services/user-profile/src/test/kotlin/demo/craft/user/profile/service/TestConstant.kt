package demo.craft.user.profile.service

import demo.craft.user.profile.domain.entity.Address
import demo.craft.user.profile.domain.entity.BusinessProfile
import demo.craft.user.profile.domain.entity.BusinessProfileChangeRequest
import demo.craft.user.profile.domain.enums.ChangeRequestOperation
import demo.craft.user.profile.domain.enums.ChangeRequestStatus
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

object TestConstant {
    const val USER_1 = "USER001"
    const val COMPANY_NAME_1 = "COMPANY 1"
    const val LEGAL_NAME_1 = "COMPANY LEGAL 1"
    const val PAN_1 = "AAACA1111A"
    const val EIN_1 = "11-1111111"
    const val EMAIL_1 = "user001@craft.demo"
    const val WEBSITE_1 = "www.craft.demo"
    val DATE_TIME_1 = LocalDateTime.parse("2023-10-20T00:00:00", DateTimeFormatter.ISO_DATE_TIME)
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

    val BUSINESS_PROFILE_CREATE_CHANGE_REQUEST_1 = BusinessProfileChangeRequest(
        id = 1,
        requestId = "00000000-0000-0000-0000-000000000001",
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
}