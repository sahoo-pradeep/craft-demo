package demo.craft.user.profile.dao.access.impl.rds

import demo.craft.user.profile.common.domain.domain.entity.BusinessProfileChangeRequest
import demo.craft.user.profile.common.domain.domain.enums.ChangeRequestStatus
import demo.craft.user.profile.common.domain.exception.BusinessProfileUpdateAlreadyInProgressException
import demo.craft.user.profile.dao.access.BusinessProfileChangeRequestAccess
import demo.craft.user.profile.dao.repository.AddressRepository
import demo.craft.user.profile.dao.repository.BusinessProfileChangeRequestRepository
import javax.transaction.Transactional
import org.springframework.stereotype.Component

@Component
internal class BusinessProfileChangeRequestRdsImpl(
    private val businessProfileChangeRequestRepository: BusinessProfileChangeRequestRepository,
    private val addressRepository: AddressRepository,
) : BusinessProfileChangeRequestAccess {
    override fun getChangeRequests(
        userId: String, status: ChangeRequestStatus?
    ): List<BusinessProfileChangeRequest> =
        status?.let { businessProfileChangeRequestRepository.findAllByUserIdAndStatus(userId, it) }
            ?: businessProfileChangeRequestRepository.findAllByUserId(userId)

    @Transactional
    override fun createChangeRequest(
        businessProfileChangeRequest: BusinessProfileChangeRequest
    ): BusinessProfileChangeRequest {
        if (getChangeRequests(businessProfileChangeRequest.userId, ChangeRequestStatus.IN_PROGRESS).isNotEmpty()) {
            throw BusinessProfileUpdateAlreadyInProgressException(businessProfileChangeRequest.userId)
        }

        val persistedBusinessAddress = addressRepository.save(businessProfileChangeRequest.businessAddress)
        val persistedLegalAddress = addressRepository.save(businessProfileChangeRequest.legalAddress)

        return businessProfileChangeRequestRepository.save(
            businessProfileChangeRequest.copy(
                businessAddress = persistedBusinessAddress,
                legalAddress = persistedLegalAddress
            )
        )
    }
}