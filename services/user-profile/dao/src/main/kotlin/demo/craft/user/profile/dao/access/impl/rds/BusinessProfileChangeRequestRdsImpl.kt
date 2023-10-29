package demo.craft.user.profile.dao.access.impl.rds

import demo.craft.user.profile.common.exception.BusinessProfileChangeRequestNotFoundException
import demo.craft.user.profile.common.exception.BusinessProfileUpdateAlreadyInProgressException
import demo.craft.user.profile.dao.access.BusinessProfileChangeRequestAccess
import demo.craft.user.profile.dao.repository.AddressRepository
import demo.craft.user.profile.dao.repository.BusinessProfileChangeRequestRepository
import demo.craft.user.profile.domain.entity.BusinessProfileChangeRequest
import demo.craft.user.profile.domain.enums.ChangeRequestStatus
import javax.transaction.Transactional
import org.springframework.stereotype.Component

@Component
internal class BusinessProfileChangeRequestRdsImpl(
    private val businessProfileChangeRequestRepository: BusinessProfileChangeRequestRepository,
    private val addressRepository: AddressRepository,
) : BusinessProfileChangeRequestAccess {
    override fun findAllByUserId(userId: String): List<BusinessProfileChangeRequest> =
        businessProfileChangeRequestRepository.findAllByUserIdOrderByIdAsc(userId)

    override fun findByRequestId(requestId: String): BusinessProfileChangeRequest? =
        businessProfileChangeRequestRepository.findByRequestId(requestId)

    override fun findTopChangeRequest(userId: String): BusinessProfileChangeRequest? =
        businessProfileChangeRequestRepository.findTopByUserIdOrderByCreatedAtDesc(userId)

    @Transactional
    override fun createChangeRequest(
        businessProfileChangeRequest: BusinessProfileChangeRequest
    ): BusinessProfileChangeRequest {
        if (findAllByUserId(businessProfileChangeRequest.userId).any { it.status == ChangeRequestStatus.IN_PROGRESS }) {
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

    override fun updateStatus(userId: String, requestId: String, updatedStatus: ChangeRequestStatus): BusinessProfileChangeRequest {
        val changeRequest = findByRequestId(requestId)
            ?: throw BusinessProfileChangeRequestNotFoundException(userId, requestId)
        if (changeRequest.status.isTerminal()) {
            throw IllegalArgumentException("State update for change request with requestId $requestId is not allowed as current status is in terminal state")
        }

        return businessProfileChangeRequestRepository.save(changeRequest.copy(status = updatedStatus))
    }
}