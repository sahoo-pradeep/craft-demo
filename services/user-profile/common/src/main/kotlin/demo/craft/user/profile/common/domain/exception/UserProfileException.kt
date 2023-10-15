package demo.craft.user.profile.common.domain.exception

/** All the custom exceptions should be created extending [UserProfileException]. */
sealed class UserProfileException(
    message: String,
    cause: Throwable? = null
) : RuntimeException(message) {
    init {
        cause?.let { initCause(cause) }
    }
}