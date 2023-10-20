package demo.craft.user.profile.dao.access.impl.rds

import demo.craft.user.profile.common.exception.BusinessProfileAlreadyExistsException
import demo.craft.user.profile.dao.access.BusinessProfileAccess
import demo.craft.user.profile.dao.repository.AddressRepository
import demo.craft.user.profile.dao.repository.BusinessProfileRepository
import demo.craft.user.profile.domain.entity.BusinessProfile
import org.springframework.stereotype.Component
import javax.transaction.Transactional

@Component
internal class BusinessProfileRdsImpl(
    private val businessProfileRepository: BusinessProfileRepository,
    private val addressRepository: AddressRepository,
) : BusinessProfileAccess {
    override fun findByUserId(userId: String): BusinessProfile? =
        businessProfileRepository.findByUserId(userId)

    @Transactional
    override fun createBusinessProfile(businessProfile: BusinessProfile): BusinessProfile {
        findByUserId(businessProfile.userId)?.let {
            throw BusinessProfileAlreadyExistsException(businessProfile.userId)
        }

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