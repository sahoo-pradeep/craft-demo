package demo.craft.user.profile.dao.access.impl.rds

import demo.craft.user.profile.common.domain.entity.BusinessProfile
import demo.craft.user.profile.dao.access.BusinessProfileAccess
import demo.craft.user.profile.dao.repository.AddressRepository
import demo.craft.user.profile.dao.repository.BusinessProfileRepository
import javax.transaction.Transactional
import org.springframework.stereotype.Component

@Component
internal class BusinessProfileRdsImpl(
    private val businessProfileRepository: BusinessProfileRepository,
    private val addressRepository: AddressRepository,
) : BusinessProfileAccess {
    override fun findByUserId(userId: String): BusinessProfile? =
        businessProfileRepository.findByUserId(userId)

    @Transactional
    override fun createBusinessProfile(businessProfile: BusinessProfile): BusinessProfile {
        val persistedBusinessAddress = addressRepository.save(businessProfile.businessAddress)
        val persistedLegalAddress = addressRepository.save(businessProfile.legalAddress)
        return businessProfileRepository.save(
            businessProfile.copy(
                businessAddress = persistedBusinessAddress,
                legalAddress = persistedLegalAddress
            )
        )
    }
}