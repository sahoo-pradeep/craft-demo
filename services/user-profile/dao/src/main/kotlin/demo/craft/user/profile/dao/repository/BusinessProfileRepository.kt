package demo.craft.user.profile.dao.repository

import demo.craft.user.profile.common.domain.entity.BusinessProfile
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
internal interface BusinessProfileRepository : JpaRepository<BusinessProfile, Long> {
    fun findByUserId(userId: String): BusinessProfile?
}