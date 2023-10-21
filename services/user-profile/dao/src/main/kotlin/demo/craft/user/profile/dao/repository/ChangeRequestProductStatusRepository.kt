package demo.craft.user.profile.dao.repository

import demo.craft.common.domain.enums.Product
import demo.craft.user.profile.domain.entity.ChangeRequestProductStatus
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
internal interface ChangeRequestProductStatusRepository : JpaRepository<ChangeRequestProductStatus, Long> {
    fun findByRequestId(requestId: String): List<ChangeRequestProductStatus>

    fun findByRequestIdAndProduct(requestId: String, product: Product): ChangeRequestProductStatus?

    fun existsByRequestId(requestId: String): Boolean
}