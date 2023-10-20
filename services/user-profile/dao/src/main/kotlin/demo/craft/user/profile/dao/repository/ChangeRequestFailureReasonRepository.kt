package demo.craft.user.profile.dao.repository

import demo.craft.user.profile.domain.entity.ChangeRequestFailureReason
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
internal interface ChangeRequestFailureReasonRepository : JpaRepository<ChangeRequestFailureReason, Long> {
    fun findByRequestId(requestId: String): ChangeRequestFailureReason?
}