package demo.craft.user.profile.dao.access.impl.rds

import demo.craft.common.domain.enums.Product
import demo.craft.user.profile.dao.access.ChangeRequestProductStatusAccess
import demo.craft.user.profile.dao.repository.ChangeRequestProductStatusRepository
import demo.craft.user.profile.domain.entity.ChangeRequestProductStatus
import demo.craft.user.profile.domain.enums.ChangeRequestStatus
import mu.KotlinLogging
import org.springframework.stereotype.Component
import javax.transaction.Transactional

@Component
internal class ChangeRequestProductStatusRdsImpl(
    private val changeRequestProductStatusRepository: ChangeRequestProductStatusRepository
) : ChangeRequestProductStatusAccess {
    private val log = KotlinLogging.logger {}
    override fun findByRequestId(requestId: String): List<ChangeRequestProductStatus> =
        changeRequestProductStatusRepository.findByRequestId(requestId)

    override fun existsByRequestId(requestId: String): Boolean =
        changeRequestProductStatusRepository.existsByRequestId(requestId)

    override fun findByRequestIdAndProduct(requestId: String, product: Product): ChangeRequestProductStatus? =
        changeRequestProductStatusRepository.findByRequestIdAndProduct(requestId, product)

    @Transactional
    override fun createNewEntries(entities: List<ChangeRequestProductStatus>): List<ChangeRequestProductStatus> {
        val requestIds = entities.map { it.requestId }.distinct()
        if (requestIds.count() > 1) {
            throw IllegalArgumentException("Investigate! entries have different requestIds: $requestIds in create ChangeRequestProductStatus request")
        }

        val requestId = requestIds.first()
        if (findByRequestId(requestId).isNotEmpty()) {
            throw IllegalArgumentException("ChangeRequestProductStatus is already created with requestId $requestId")
        }

        return changeRequestProductStatusRepository.saveAll(
            entities.map { it.copy(id = null) }
        ).also {
            log.info { "ChangeRequestProductStatus created with requestId: $requestId. Count: ${it.size}" }
        }
    }

    // TODO: Custom Exception can be used in place of IllegalArgumentException
    override fun updateStatus(requestId: String, product: Product, status: ChangeRequestStatus): ChangeRequestProductStatus {
        val productStatus = findByRequestIdAndProduct(requestId, product)
            ?: throw IllegalArgumentException("ChangeRequestProductStatus with requestId: $requestId and product: $product doesn't exist")

        if (productStatus.status.isTerminal()) {
            throw IllegalArgumentException("State update for product status with requestId $requestId is not allowed as current status is terminal")
        }

        return changeRequestProductStatusRepository.save(productStatus.copy(status = status)).also {
            log.info { "ChangeRequestProductStatus updated with requestId: $requestId, product: $product and status: $status" }
        }
    }
}
