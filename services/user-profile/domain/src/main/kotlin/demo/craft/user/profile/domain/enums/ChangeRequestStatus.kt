package demo.craft.user.profile.domain.enums

enum class ChangeRequestStatus {
    IN_PROGRESS,
    ACCEPTED,
    REJECTED;

    companion object {
        // Initial status for update request
        fun init(): ChangeRequestStatus = IN_PROGRESS
    }

    fun isTerminal(): Boolean =
        when (this) {
            ACCEPTED, REJECTED -> true
            else -> false
        }
}