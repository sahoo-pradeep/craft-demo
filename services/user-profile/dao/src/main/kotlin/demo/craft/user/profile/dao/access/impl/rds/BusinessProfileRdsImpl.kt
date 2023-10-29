package demo.craft.user.profile.dao.access.impl.rds

import demo.craft.user.profile.common.exception.BusinessProfileAlreadyExistsException
import demo.craft.user.profile.dao.access.BusinessProfileAccess
import demo.craft.user.profile.dao.repository.AddressRepository
import demo.craft.user.profile.dao.repository.BusinessProfileRepository
import demo.craft.user.profile.domain.entity.Address
import demo.craft.user.profile.domain.entity.BusinessProfile
import demo.craft.user.profile.domain.entity.BusinessProfileChangeRequest
import demo.craft.user.profile.domain.mapper.toBusinessProfile
import javax.transaction.Transactional
import mu.KotlinLogging
import org.springframework.stereotype.Component

@Component
internal class BusinessProfileRdsImpl(
    private val businessProfileRepository: BusinessProfileRepository,
    private val addressRepository: AddressRepository,
) : BusinessProfileAccess {
    private val log = KotlinLogging.logger {}

    override fun findByUserId(userId: String): BusinessProfile? =
        businessProfileRepository.findByUserId(userId)

    @Transactional
    override fun createOrUpdateBusinessProfile(changeRequest: BusinessProfileChangeRequest): BusinessProfile =
        findByUserId(changeRequest.userId)?.let {
            val updatedBusinessProfile = getUpdatedBusinessProfile(it, changeRequest)
            val persistedBusinessAddress = saveNewAddress(updatedBusinessProfile.businessAddress)
            val persistedLegalAddress = saveNewAddress(updatedBusinessProfile.legalAddress)

            businessProfileRepository.saveAndFlush(
                updatedBusinessProfile.copy(
                    businessAddress = persistedBusinessAddress,
                    legalAddress = persistedLegalAddress,
                )
            ).also {
                log.info { "Business profile is updated successfully for userId ${changeRequest.userId}" }
            }
        } ?: createBusinessProfile(changeRequest.toBusinessProfile())

    private fun createBusinessProfile(businessProfile: BusinessProfile): BusinessProfile {
        findByUserId(businessProfile.userId)?.let {
            throw BusinessProfileAlreadyExistsException(businessProfile.userId)
        }

        val persistedBusinessAddress = addressRepository.saveAndFlush(cloneNewAddress(businessProfile.businessAddress))
        val persistedLegalAddress = addressRepository.saveAndFlush(cloneNewAddress(businessProfile.legalAddress))
        return businessProfileRepository.saveAndFlush(
            businessProfile.copy(
                id = null, // to create a new entry
                businessAddress = persistedBusinessAddress,
                legalAddress = persistedLegalAddress,
            )
        ).also {
            log.info { "Business profile is created successfully for userId ${businessProfile.userId}" }
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

    // to ensure there is no association with previously stored address
    private fun cloneNewAddress(inputAddress: Address): Address =
        inputAddress.copy(id = null)

    // id is null when the address is not saved already
    private fun saveNewAddress(businessAddress: Address): Address =
        businessAddress.id?.let {
            addressRepository.saveAndFlush(businessAddress)
        } ?: businessAddress
}