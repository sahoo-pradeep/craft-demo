package demo.craft.user.profile.dao.access.impl.rds

import demo.craft.common.domain.enums.Product
import demo.craft.user.profile.dao.access.ChangeRequestFailureReasonAccess
import demo.craft.user.profile.dao.repository.ChangeRequestFailureReasonRepository
import demo.craft.user.profile.domain.entity.ChangeRequestFailureReason
import demo.craft.user.profile.domain.enums.FieldName
import mu.KotlinLogging
import org.springframework.stereotype.Component

@Component
internal class ChangeRequestFailureReasonRdsImpl(
    private val changeRequestFailureReasonRepository: ChangeRequestFailureReasonRepository
) : ChangeRequestFailureReasonAccess {
    private val log = KotlinLogging.logger {}
    override fun findAllByRequestId(requestId: String): List<ChangeRequestFailureReason> =
        changeRequestFailureReasonRepository.findByRequestId(requestId)

    override fun saveAllFailureReason(
        requestId: String,
        product: Product,
        failureReasons: List<Pair<FieldName, String>>
    ): List<ChangeRequestFailureReason> {
        if (changeRequestFailureReasonRepository.existsByRequestIdAndProduct(requestId, product)) {
            throw IllegalArgumentException("Failure reasons are already saved for requestId $requestId and product: $product")
        }

        return changeRequestFailureReasonRepository.saveAll(
            failureReasons.map {
                ChangeRequestFailureReason(
                    requestId = requestId,
                    product = product,
                    field = it.first,
                    reason = it.second
                )
            }
        ).also {
            log.info { "Failure reasons are saved for requestId: $requestId and product: $product. Count: ${it.size}" }
        }
    }
}
