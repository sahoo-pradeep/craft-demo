package demo.craft.user.profile.common.exception

class BusinessProfileUpdateAlreadyInProgressException(userId: String) :
    UserProfileException(
        reason = "duplicate",
        message = "Business profile update is already in progress for userId $userId. A new request is not allowed."
    )

class InvalidBusinessProfileChangeRequestException(userId: String, invalidFields: String? = null) :
    UserProfileException(
        reason = "invalidParameter",
        message = "Change request for business profile is invalid for userId $userId. Invalid Fields: $invalidFields"
    )

class BusinessProfileChangeRequestNotFoundException(userId: String, requestId: String?) :
    UserProfileException(
        reason = "notFound",
        message = "Business profile change request not found with userId: $userId and requestId: $requestId"
    )

class BusinessProfileChangeRequestIllegalStateException(
    userId: String,
    requestId: String,
    currentStatus: String,
    updateStatus: String
) : UserProfileException(
    reason = "invalidParameter",
    message = "Illegal status update of change request from $currentStatus to $updateStatus. userId: $userId, requestId: $requestId"
)
