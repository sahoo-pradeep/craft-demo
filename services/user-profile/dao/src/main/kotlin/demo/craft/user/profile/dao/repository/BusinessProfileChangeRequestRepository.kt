package demo.craft.user.profile.dao.repository

import demo.craft.user.profile.domain.entity.BusinessProfileChangeRequest
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
internal interface BusinessProfileChangeRequestRepository : JpaRepository<BusinessProfileChangeRequest, Long> {
    fun findAllByUserIdOrderByIdAsc(userId: String): List<BusinessProfileChangeRequest>
    fun findByUserIdAndRequestId(userId: String, requestId: String): BusinessProfileChangeRequest?
}
