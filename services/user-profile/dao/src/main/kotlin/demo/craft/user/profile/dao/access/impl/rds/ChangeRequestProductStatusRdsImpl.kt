package demo.craft.user.profile.dao.access.impl.rds

import com.fasterxml.jackson.core.type.TypeReference
import demo.craft.common.domain.enums.Product
import demo.craft.user.profile.common.exception.InvalidProductStatusException
import demo.craft.user.profile.common.exception.ProductStatusAlreadyExistsException
import demo.craft.user.profile.common.exception.ProductStatusIllegalStateException
import demo.craft.user.profile.common.exception.ProductStatusNotFoundException
import demo.craft.user.profile.dao.access.ChangeRequestProductStatusAccess
import demo.craft.user.profile.dao.access.cache.GenericCacheManager
import demo.craft.user.profile.dao.repository.ChangeRequestProductStatusRepository
import demo.craft.user.profile.domain.entity.ChangeRequestProductStatus
import demo.craft.user.profile.domain.enums.ChangeRequestStatus
import javax.transaction.Transactional
import mu.KotlinLogging
import org.springframework.stereotype.Component

@Component
internal class ChangeRequestProductStatusRdsImpl(
    private val changeRequestProductStatusRepository: ChangeRequestProductStatusRepository,
    private val genericCacheManager: GenericCacheManager,
) : ChangeRequestProductStatusAccess {
    private val log = KotlinLogging.logger {}

    override fun findAllByRequestId(requestId: String): List<ChangeRequestProductStatus> =
        genericCacheManager.cacheLookup(
            getCacheKey(requestId),
            object : TypeReference<List<ChangeRequestProductStatus>>() {}
        ) {
            changeRequestProductStatusRepository.findByRequestId(requestId)
        }

    override fun findByRequestIdAndProduct(requestId: String, product: Product): ChangeRequestProductStatus? =
        findAllByRequestId(requestId).first { it.product == product }

    @Transactional
    override fun createNewProductStatuses(productStatuses: List<ChangeRequestProductStatus>): List<ChangeRequestProductStatus> {
        if (productStatuses.isEmpty()) {
            log.warn { "input list is empty for creating new product statuses" }
            return emptyList()
        }

        val requestIds = productStatuses.map { it.requestId }.distinct()
        if (requestIds.count() > 1) {
            throw InvalidProductStatusException("entries have different requestIds: $requestIds in create ChangeRequestProductStatus request")
        }

        val requestId = requestIds.first()
        if (changeRequestProductStatusRepository.existsByRequestId(requestId)) {
            throw ProductStatusAlreadyExistsException(requestId)
        }

        return changeRequestProductStatusRepository.saveAll(
            productStatuses.map { it.copy(id = null) }
        ).also {
            log.info { "ChangeRequestProductStatus created with requestId: $requestId. Count: ${it.size}" }
            genericCacheManager.cacheUpdate(
                getCacheKey(requestId),
                object : TypeReference<List<ChangeRequestProductStatus>>() {},
                it
            )
        }
    }

    override fun updateStatus(requestId: String, product: Product, status: ChangeRequestStatus): ChangeRequestProductStatus {
        val productStatus = findByRequestIdAndProduct(requestId, product)
            ?: throw ProductStatusNotFoundException(requestId, product.name)

        if (productStatus.status.isTerminal()) {
            throw ProductStatusIllegalStateException(requestId, productStatus.status.name, status.name)
        }

        return changeRequestProductStatusRepository.save(productStatus.copy(status = status)).also {
            log.info { "ChangeRequestProductStatus updated with requestId: $requestId, product: $product and status: $status" }
            val allProductStatuses = changeRequestProductStatusRepository.findByRequestId(requestId)
            genericCacheManager.cacheUpdate(
                getCacheKey(requestId),
                object : TypeReference<List<ChangeRequestProductStatus>>() {},
                allProductStatuses
            )
        }
    }

    private fun getCacheKey(requestId: String) = "ChangeRequestProductStatus_$requestId"
}
