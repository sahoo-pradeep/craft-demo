package demo.craft.product.subscription

import demo.craft.product.subscription.common.config.ProductSubscriptionProperties
import java.time.ZoneId
import java.util.TimeZone
import org.springframework.boot.SpringApplication
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.context.properties.ConfigurationPropertiesScan
import org.springframework.data.jpa.repository.config.EnableJpaRepositories

@SpringBootApplication(
    scanBasePackages = [
        "demo.craft.product.subscription",
    ]
)
@ConfigurationPropertiesScan(
    basePackageClasses = [
        ProductSubscriptionProperties::class,
    ]
)
@EnableJpaRepositories
class ProductSubscriptionApp {
    init {
        TimeZone.setDefault(TIME_ZONE_UTC)
    }

    companion object {
        val TIME_ZONE_UTC: TimeZone = TimeZone.getTimeZone(ZoneId.of("UTC"))

        @JvmStatic
        fun main(args: Array<String>) {
            SpringApplication.run(ProductSubscriptionApp::class.java, *args)
        }
    }
}
