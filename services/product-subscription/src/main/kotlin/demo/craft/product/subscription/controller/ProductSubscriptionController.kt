package demo.craft.product.subscription.controller

import demo.craft.product.subscription.api.ProductSubscriptionApi
import demo.craft.product.subscription.model.GetAllProductSubscriptionsResponse
import demo.craft.product.subscription.model.SaveProductSubscriptionRequest
import demo.craft.product.subscription.model.SaveProductSubscriptionResponse
import demo.craft.product.subscription.model.UpdateProductSubscriptionRequest
import demo.craft.product.subscription.model.UpdateProductSubscriptionResponse
import demo.craft.product.subscription.mapper.toApiModel
import demo.craft.product.subscription.mapper.toDomainModel
import demo.craft.product.subscription.service.ProductSubscriptionService
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.RestController

@RestController
class ProductSubscriptionController(
    private val productSubscriptionService: ProductSubscriptionService
) : ProductSubscriptionApi {

    override fun getAllProductSubscriptions(xMinusUserMinusId: String): ResponseEntity<GetAllProductSubscriptionsResponse> =
        ResponseEntity.ok(
            GetAllProductSubscriptionsResponse(
                productSubscriptionService.getAllProductSubscriptions(xMinusUserMinusId).toApiModel()
            )
        )

    override fun saveProductSubscription(
        xMinusUserMinusId: String,
        saveProductSubscriptionRequest: SaveProductSubscriptionRequest
    ): ResponseEntity<SaveProductSubscriptionResponse> =
        ResponseEntity.ok(
            SaveProductSubscriptionResponse(
                productSubscriptionService.saveProductSubscription(
                    saveProductSubscriptionRequest.productSubscription.toDomainModel(xMinusUserMinusId)
                ).toApiModel()
            )
        )

    override fun updateProductSubscription(
        xMinusUserMinusId: String,
        updateProductSubscriptionRequest: UpdateProductSubscriptionRequest
    ): ResponseEntity<UpdateProductSubscriptionResponse> =
        ResponseEntity.ok(
            UpdateProductSubscriptionResponse(
                productSubscriptionService.updateProductSubscription(
                    updateProductSubscriptionRequest.productSubscription.toDomainModel(xMinusUserMinusId)
                ).toApiModel()
            )
        )
}
