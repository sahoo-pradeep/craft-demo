package demo.craft.user.profile.common.domain.exception

/** All the custom exceptions should be created extending [UserProfileException]. */
sealed class UserProfileException(
    override val message: String,
    val additionalInfo: Any? = null,
    final override val cause: Throwable? = null
) : RuntimeException(message) {
    init {
        cause?.let { initCause(cause) }
    }
}