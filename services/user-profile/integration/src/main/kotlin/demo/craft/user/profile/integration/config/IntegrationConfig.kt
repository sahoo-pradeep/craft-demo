package demo.craft.user.profile.integration.config

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import demo.craft.product.subscription.client.api.ProductSubscriptionApi
import demo.craft.user.profile.common.config.UserProfileProperties
import feign.Contract
import feign.Feign
import feign.Logger
import feign.Retryer
import feign.jackson.JacksonDecoder
import feign.jackson.JacksonEncoder
import org.springframework.cloud.openfeign.support.ResponseEntityDecoder
import org.springframework.cloud.openfeign.support.SpringMvcContract
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class IntegrationConfig(
    userProfileProperties: UserProfileProperties
) {
    private val objectMapper = jacksonObjectMapper()
    private val integrationProperties = userProfileProperties.integration

    @Bean
    fun productSubscriptionApi(): ProductSubscriptionApi =
        getFeignBuilder(SpringMvcContract()).target(
            ProductSubscriptionApi::class.java,
            integrationProperties.productSubscription.url
        )

    private fun getFeignBuilder(contract: Contract): Feign.Builder =
        Feign.builder()
            .logLevel(Logger.Level.FULL)
            .decoder(ResponseEntityDecoder(JacksonDecoder(objectMapper)))
            .encoder(JacksonEncoder(objectMapper))
            .retryer(Retryer.NEVER_RETRY)
            .requestInterceptor {
                it.header("Content-Type", "application/json")
                it.header("Accept", "application/json")
            }
            .contract(contract)
}