package demo.craft.user.profile.common.domain.exception

class BusinessProfileUpdateAlreadyInProgressException(userId: String) :
    UserProfileException("Business profile update is already in progress for userId $userId. A new request is not allowed.")

class InvalidBusinessProfileChangeRequestException(userId: String, reason: String? = null) :
    UserProfileException("Change request for business profile is invalid for userId $userId. Reason: $reason")
