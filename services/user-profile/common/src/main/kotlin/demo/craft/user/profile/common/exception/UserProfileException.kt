package demo.craft.user.profile.common.exception

/** All the custom exceptions should be created extending [UserProfileException]. */
sealed class UserProfileException(
    val reason: String = "internalError",
    override val message: String = "The request failed due to an internal error",
    val additionalInfo: String? = null,
    final override val cause: Throwable? = null
) : RuntimeException(message) {
    init {
        cause?.let { initCause(cause) }
    }
}

class UnauthorizedUserException(userId: String) :
    UserProfileException(
        reason = "unauthorized",
        message = "The userId $userId is not authorized to make the request.",
    )