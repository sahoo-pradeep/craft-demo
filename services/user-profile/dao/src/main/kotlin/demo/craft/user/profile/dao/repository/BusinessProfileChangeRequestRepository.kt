package demo.craft.user.profile.dao.repository

import demo.craft.user.profile.domain.entity.BusinessProfileChangeRequest
import demo.craft.user.profile.domain.enums.ChangeRequestStatus
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
internal interface BusinessProfileChangeRequestRepository : JpaRepository<BusinessProfileChangeRequest, Long> {
    fun findAllByUserId(userId: String): List<BusinessProfileChangeRequest>
    fun findAllByUserIdAndStatus(userId: String, status: ChangeRequestStatus): List<BusinessProfileChangeRequest>
}