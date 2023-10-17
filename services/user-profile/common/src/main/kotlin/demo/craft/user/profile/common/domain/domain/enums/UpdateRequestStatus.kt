package demo.craft.user.profile.common.domain.domain.enums

enum class UpdateRequestStatus {
    IN_PROGRESS,
    ACCEPTED,
    REJECTED;

    companion object {
        // Initial status for update request
        fun init(): UpdateRequestStatus = IN_PROGRESS
    }
}