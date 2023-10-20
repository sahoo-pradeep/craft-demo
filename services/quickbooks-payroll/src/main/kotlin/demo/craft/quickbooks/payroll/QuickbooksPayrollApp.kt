package demo.craft.product.subscription

import org.springframework.boot.SpringApplication
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.context.properties.ConfigurationPropertiesScan
import java.time.ZoneId
import java.util.*

@SpringBootApplication(
    scanBasePackages = [
        "demo.craft.quickbooks.payroll",
    ]
)
@ConfigurationPropertiesScan
class QuickbooksPayrollApp {
    init {
        TimeZone.setDefault(TIME_ZONE_UTC)
    }

    companion object {
        val TIME_ZONE_UTC: TimeZone = TimeZone.getTimeZone(ZoneId.of("UTC"))

        @JvmStatic
        fun main(args: Array<String>) {
            SpringApplication.run(QuickbooksPayrollApp::class.java, *args)
        }
    }
}
