package demo.craft.user.profile.dao.repository

import demo.craft.user.profile.common.domain.entity.BusinessProfileChangeRequest
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
internal interface BusinessProfileChangeRequestRepository : JpaRepository<BusinessProfileChangeRequest, Long> {
}