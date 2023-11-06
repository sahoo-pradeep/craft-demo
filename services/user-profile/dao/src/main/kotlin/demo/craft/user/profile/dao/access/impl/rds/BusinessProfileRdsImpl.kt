package demo.craft.user.profile.dao.access.impl.rds

import com.fasterxml.jackson.core.type.TypeReference
import demo.craft.user.profile.dao.access.BusinessProfileAccess
import demo.craft.user.profile.dao.access.cache.GenericCacheManager
import demo.craft.user.profile.dao.repository.AddressRepository
import demo.craft.user.profile.dao.repository.BusinessProfileRepository
import demo.craft.user.profile.domain.entity.Address
import demo.craft.user.profile.domain.entity.BusinessProfile
import demo.craft.user.profile.domain.entity.BusinessProfileChangeRequest
import javax.transaction.Transactional
import mu.KotlinLogging
import org.springframework.stereotype.Component

@Component
internal class BusinessProfileRdsImpl(
    private val businessProfileRepository: BusinessProfileRepository,
    private val addressRepository: AddressRepository,
    private val genericCacheManager: GenericCacheManager
) : BusinessProfileAccess {
    private val log = KotlinLogging.logger {}
    companion object {
        const val CACHE_PREFIX: String = "BUSINESS_PROFILE"
    }

    override fun findByUserId(userId: String): BusinessProfile? =
        genericCacheManager.cacheLookupForNullable(
            getCacheKey(userId),
            object : TypeReference<BusinessProfile>(){}
        ) {
            businessProfileRepository.findByUserId(userId)
        }

    @Transactional
    override fun createOrUpdateBusinessProfile(changeRequest: BusinessProfileChangeRequest): BusinessProfile {
        val businessProfile = findByUserId(changeRequest.userId)?.let {
            getUpdatedBusinessProfile(it, changeRequest)
        } ?: changeRequest.toNewBusinessProfile()

        val persistedBusinessAddress = saveNewAddress(businessProfile.businessAddress)
        val persistedLegalAddress = saveNewAddress(businessProfile.legalAddress)

        return businessProfileRepository.saveAndFlush(
            businessProfile.copy(
                businessAddress = persistedBusinessAddress,
                legalAddress = persistedLegalAddress,
            )
        ).also {
            log.info { "Business profile is created/updated successfully for userId ${changeRequest.userId}" }
            genericCacheManager.cacheUpdate(getCacheKey(it.userId), object : TypeReference<BusinessProfile>() {}, it)
        }
    }

    private fun getUpdatedBusinessProfile(
        currentBusinessProfile: BusinessProfile,
        changeRequest: BusinessProfileChangeRequest
    ): BusinessProfile =
        currentBusinessProfile.copy(
            companyName = changeRequest.companyName,
            legalName = changeRequest.legalName,
            pan = changeRequest.pan,
            ein = changeRequest.ein,
            email = changeRequest.email,
            website = changeRequest.website,
            // no need of updating address, if not changed
            businessAddress = if (currentBusinessProfile.businessAddress == changeRequest.businessAddress) {
                currentBusinessProfile.businessAddress
            } else {
                cloneNewAddress(changeRequest.businessAddress)
            },

            legalAddress = if (currentBusinessProfile.legalAddress == changeRequest.legalAddress) {
                currentBusinessProfile.legalAddress
            } else {
                cloneNewAddress(changeRequest.legalAddress)
            }
        )

    private fun BusinessProfileChangeRequest.toNewBusinessProfile(): BusinessProfile =
        BusinessProfile(
            userId = this.userId,
            companyName = this.companyName,
            legalName = this.legalName,
            businessAddress = cloneNewAddress(this.businessAddress),
            legalAddress = cloneNewAddress(this.legalAddress),
            pan = this.pan,
            ein = this.ein,
            email = this.email,
            website = this.website,
        )

    // to ensure there is no association with previously stored address
    private fun cloneNewAddress(inputAddress: Address): Address =
        inputAddress.copy(id = null)

    // id is null when the address is not saved already
    private fun saveNewAddress(address: Address): Address =
        address.id?.let {
            address
        } ?: addressRepository.saveAndFlush(address)

    private fun getCacheKey(userId: String) = "${CACHE_PREFIX}_$userId"
}