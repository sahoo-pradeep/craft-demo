package demo.craft.user.profile.common.exception

class BusinessProfileNotFoundException(userId: String) :
    UserProfileException(
        reason = "notFound",
        message = "Business profile not found for userId $userId",
    )

class BusinessProfileAlreadyExistsException(userId: String) :
    UserProfileException(
        reason = "duplicate",
        message = "Business profile already exist for userId $userId",
    )

class InvalidBusinessProfileException(userId: String, invalidFields: String) :
    UserProfileException(
        reason = "invalidParameter",
        message = "Invalid business profile for userId $userId. Invalid Fields: $invalidFields",
    )