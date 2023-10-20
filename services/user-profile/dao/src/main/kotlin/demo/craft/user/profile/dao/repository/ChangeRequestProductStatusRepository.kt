package demo.craft.user.profile.dao.repository

import demo.craft.user.profile.domain.entity.ChangeRequestProductStatus
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
internal interface ChangeRequestProductStatusRepository : JpaRepository<ChangeRequestProductStatus, Long> {
    fun findByRequestId(requestId: String): ChangeRequestProductStatus?
}