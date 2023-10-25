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
    override fun createBusinessProfile(businessProfile: BusinessProfile): BusinessProfile {
        findByUserId(businessProfile.userId)?.let {
            throw BusinessProfileAlreadyExistsException(businessProfile.userId)
        }

        val persistedBusinessAddress = addressRepository.save(createNewAddress(businessProfile.businessAddress))
        val persistedLegalAddress = addressRepository.save(createNewAddress(businessProfile.legalAddress))
        return businessProfileRepository.save(
            businessProfile.copy(
                id = null, // to create a new entry
                businessAddress = persistedBusinessAddress,
                legalAddress = persistedLegalAddress,
            )
        ).also {
            log.info { "Business profile is created successfully for userId ${businessProfile.userId}" }
        }
    }

    @Transactional
    override fun updateBusinessProfile(changeRequest: BusinessProfileChangeRequest): BusinessProfile =
        findByUserId(changeRequest.userId)?.let {
            val updatedBusinessProfile = updateFields(it, changeRequest)
            val persistedBusinessAddress = if (updatedBusinessProfile.businessAddress.id == null) {
                addressRepository.save(updatedBusinessProfile.businessAddress)
            } else {
                updatedBusinessProfile.businessAddress
            }

            val persistedLegalAddress = if (updatedBusinessProfile.legalAddress.id == null) {
                addressRepository.save(updatedBusinessProfile.legalAddress)
            } else {
                updatedBusinessProfile.legalAddress
            }

            businessProfileRepository.save(
                updatedBusinessProfile.copy(
                    businessAddress = persistedBusinessAddress,
                    legalAddress = persistedLegalAddress,
                )
            ).also {
                log.info { "Business profile is updated successfully for userId ${changeRequest.userId}" }
            }
        } ?: createBusinessProfile(changeRequest.toBusinessProfile())

    private fun updateFields(
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
                createNewAddress(changeRequest.businessAddress)
            },

            legalAddress = if (currentBusinessProfile.legalAddress == changeRequest.legalAddress) {
                currentBusinessProfile.legalAddress
            } else {
                createNewAddress(changeRequest.legalAddress)
            }
        )

    // to ensure there is no association with previously stored address
    private fun createNewAddress(inputAddress: Address): Address =
        inputAddress.copy(id = null)
}