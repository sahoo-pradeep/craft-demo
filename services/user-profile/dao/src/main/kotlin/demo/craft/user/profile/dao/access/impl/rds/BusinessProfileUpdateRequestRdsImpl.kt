package demo.craft.user.profile.dao.access.impl.rds

import demo.craft.user.profile.common.domain.domain.entity.UpdateRequest
import demo.craft.user.profile.common.domain.domain.enums.UpdateRequestStatus
import demo.craft.user.profile.common.domain.exception.BusinessProfileUpdateAlreadyInProgressException
import demo.craft.user.profile.dao.access.UpdateRequestAccess
import demo.craft.user.profile.dao.repository.AddressRepository
import demo.craft.user.profile.dao.repository.UpdateRequestRepository
import javax.transaction.Transactional
import org.springframework.stereotype.Component

@Component
internal class BusinessProfileUpdateRequestRdsImpl(
    private val updateRequestRepository: UpdateRequestRepository,
    private val addressRepository: AddressRepository,
) : UpdateRequestAccess {
    override fun getUpdateRequests(
        userId: String, status: UpdateRequestStatus?
    ): List<UpdateRequest> =
        status?.let { updateRequestRepository.findAllByUserIdAndStatus(userId, it) }
            ?: updateRequestRepository.findAllByUserId(userId)

    @Transactional
    override fun createUpdateRequest(
        updateRequest: UpdateRequest
    ): UpdateRequest {
        if (getUpdateRequests(updateRequest.userId, UpdateRequestStatus.IN_PROGRESS).isNotEmpty()) {
            throw BusinessProfileUpdateAlreadyInProgressException(updateRequest.userId)
        }

        val persistedBusinessAddress = addressRepository.save(updateRequest.businessAddress)
        val persistedLegalAddress = addressRepository.save(updateRequest.legalAddress)

        return updateRequestRepository.save(
            updateRequest.copy(
                businessAddress = persistedBusinessAddress,
                legalAddress = persistedLegalAddress
            )
        )
    }
}