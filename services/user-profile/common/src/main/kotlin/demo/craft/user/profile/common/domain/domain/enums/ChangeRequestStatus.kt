package demo.craft.user.profile.common.domain.domain.enums

enum class ChangeRequestStatus {
    IN_PROGRESS,
    ACCEPTED,
    REJECTED;

    companion object {
        // Initial status for update request
        fun init(): ChangeRequestStatus = IN_PROGRESS
    }
}