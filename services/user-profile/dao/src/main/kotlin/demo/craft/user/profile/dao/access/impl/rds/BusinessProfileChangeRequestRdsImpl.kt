package demo.craft.user.profile.dao.access.impl.rds

import demo.craft.user.profile.common.exception.BusinessProfileChangeRequestIllegalStateException
import demo.craft.user.profile.common.exception.BusinessProfileChangeRequestNotFoundException
import demo.craft.user.profile.common.exception.BusinessProfileUpdateAlreadyInProgressException
import demo.craft.user.profile.dao.access.BusinessProfileChangeRequestAccess
import demo.craft.user.profile.dao.repository.AddressRepository
import demo.craft.user.profile.dao.repository.BusinessProfileChangeRequestRepository
import demo.craft.user.profile.domain.entity.BusinessProfileChangeRequest
import demo.craft.user.profile.domain.enums.ChangeRequestStatus
import javax.transaction.Transactional
import mu.KotlinLogging
import org.springframework.stereotype.Component

@Component
internal class BusinessProfileChangeRequestRdsImpl(
    private val businessProfileChangeRequestRepository: BusinessProfileChangeRequestRepository,
    private val addressRepository: AddressRepository,
) : BusinessProfileChangeRequestAccess {
    private val log = KotlinLogging.logger {}

    override fun findAllByUserId(userId: String): List<BusinessProfileChangeRequest> =
        businessProfileChangeRequestRepository.findAllByUserIdOrderByIdAsc(userId)

    override fun findByUserIdAndRequestId(userId: String, requestId: String): BusinessProfileChangeRequest? =
        businessProfileChangeRequestRepository.findByUserIdAndRequestId(userId, requestId)

    @Transactional
    override fun createChangeRequest(
        businessProfileChangeRequest: BusinessProfileChangeRequest
    ): BusinessProfileChangeRequest {
        if (findAllByUserId(businessProfileChangeRequest.userId).any { it.status == ChangeRequestStatus.IN_PROGRESS }) {
            throw BusinessProfileUpdateAlreadyInProgressException(businessProfileChangeRequest.userId)
        }

        val persistedBusinessAddress = addressRepository.saveAndFlush(businessProfileChangeRequest.businessAddress)
        val persistedLegalAddress = addressRepository.saveAndFlush(businessProfileChangeRequest.legalAddress)

        return businessProfileChangeRequestRepository.saveAndFlush(
            businessProfileChangeRequest.copy(
                businessAddress = persistedBusinessAddress,
                legalAddress = persistedLegalAddress
            )
        )
    }

    override fun updateStatus(userId: String, requestId: String, updatedStatus: ChangeRequestStatus): BusinessProfileChangeRequest {
        val changeRequest = findByUserIdAndRequestId(userId, requestId)
            ?: throw BusinessProfileChangeRequestNotFoundException(userId, requestId)
        if (changeRequest.status.isTerminal()) {
            throw BusinessProfileChangeRequestIllegalStateException(userId, requestId, changeRequest.status.name, updatedStatus.name)
        }

        val updatedChangeRequest = changeRequest.copy(status = updatedStatus)
        return businessProfileChangeRequestRepository.saveAndFlush(updatedChangeRequest).also {
            log.info { "Status of change request with requestId $requestId is updated to ${it.status}" }
        }
    }
}