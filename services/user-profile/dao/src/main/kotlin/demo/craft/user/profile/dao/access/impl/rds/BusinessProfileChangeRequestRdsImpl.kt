package demo.craft.user.profile.dao.access.impl.rds

import com.fasterxml.jackson.core.type.TypeReference
import demo.craft.user.profile.common.exception.BusinessProfileChangeRequestIllegalStateException
import demo.craft.user.profile.common.exception.BusinessProfileChangeRequestNotFoundException
import demo.craft.user.profile.common.exception.BusinessProfileUpdateAlreadyInProgressException
import demo.craft.user.profile.dao.access.BusinessProfileChangeRequestAccess
import demo.craft.user.profile.dao.access.cache.GenericCacheManager
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
    private val genericCacheManager: GenericCacheManager
) : BusinessProfileChangeRequestAccess {
    private val log = KotlinLogging.logger {}

    override fun findAllByUserId(userId: String): List<BusinessProfileChangeRequest> =
        genericCacheManager.cacheLookup(
            getCacheKey(userId),
            object : TypeReference<List<BusinessProfileChangeRequest>>() {}
        ) {
            businessProfileChangeRequestRepository.findAllByUserIdOrderByIdAsc(userId)
        }

    override fun findByUserIdAndRequestId(userId: String, requestId: String): BusinessProfileChangeRequest? =
        findAllByUserId(userId).find { it.requestId == requestId }

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

        return businessProfileChangeRequestRepository.saveAndFlush(changeRequest.copy(status = updatedStatus)).also {
            log.info { "Status of change request with requestId $requestId is updated from ${changeRequest.status} to ${it.status}" }
            val allChangeRequests = businessProfileChangeRequestRepository.findAllByUserIdOrderByIdAsc(userId)
            genericCacheManager.cacheUpdate(
                getCacheKey(userId),
                object : TypeReference<List<BusinessProfileChangeRequest>>() {},
                allChangeRequests
            )
        }
    }

    private fun getCacheKey(userId: String) = "BusinessProfileChangeRequest_$userId"
}