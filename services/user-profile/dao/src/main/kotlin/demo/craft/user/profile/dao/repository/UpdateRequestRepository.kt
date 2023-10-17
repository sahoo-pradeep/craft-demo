package demo.craft.user.profile.dao.repository

import demo.craft.user.profile.common.domain.domain.entity.UpdateRequest
import demo.craft.user.profile.common.domain.domain.enums.UpdateRequestStatus
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
internal interface UpdateRequestRepository : JpaRepository<UpdateRequest, Long> {
    fun existsByUserIdAndStatus(userId: String, status: UpdateRequestStatus): Boolean
    fun findAllByUserId(userId: String): List<UpdateRequest>
    fun findAllByUserIdAndStatus(userId: String, status: UpdateRequestStatus): List<UpdateRequest>
}