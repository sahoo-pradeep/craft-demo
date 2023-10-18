package demo.craft.user.profile.dao.repository

import demo.craft.user.profile.domain.entity.Address
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
internal interface AddressRepository : JpaRepository<Address, Long> {
}