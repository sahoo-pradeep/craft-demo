package demo.craft.user.profile.common.domain.exception

class BusinessProfileNotFoundException(userId: String) :
    UserProfileException("Business profile not found for userId $userId")

class BusinessProfileAlreadyExistsException(userId: String) :
    UserProfileException("Business profile already exist for userId $userId")

class InvalidBusinessProfileException(userId: String, reason: String? = null) :
    UserProfileException("Invalid business profile for userId $userId. Reason: $reason")