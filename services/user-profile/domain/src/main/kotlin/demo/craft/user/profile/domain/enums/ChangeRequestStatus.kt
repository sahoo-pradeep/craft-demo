package demo.craft.user.profile.domain.enums

enum class ChangeRequestStatus {
    /** Change request is in progress */
    IN_PROGRESS,

    /** Change request is accepted by all product subscribed by the user */
    ACCEPTED,

    /** Change request is rejected by one or more product subscribed by the user */
    REJECTED,

    /** Failed to process change request due to non-retryable error */
    FAILED;

    companion object {
        // Initial status for update request
        fun init(): ChangeRequestStatus = IN_PROGRESS
    }

    fun isTerminal(): Boolean =
        when (this) {
            ACCEPTED, REJECTED, FAILED -> true
            else -> false
        }
}