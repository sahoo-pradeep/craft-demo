package demo.craft.user.profile.common.exception

class BusinessProfileNotFoundException(userId: String) :
    UserProfileException("Business profile not found for userId $userId")

class BusinessProfileAlreadyExistsException(userId: String) :
    UserProfileException("Business profile already exist for userId $userId")

class InvalidBusinessProfileException(userId: String, additionalInfo: String) :
    UserProfileException(
        message = "Invalid business profile for userId $userId",
        additionalInfo = additionalInfo
    )