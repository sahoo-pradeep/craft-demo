package demo.craft.quickbooks.accounting

import demo.craft.common.communication.config.CommunicationProperties
import demo.craft.quickbooks.accounting.config.QuickbooksAccountingProperties
import org.springframework.boot.SpringApplication
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration
import org.springframework.boot.context.properties.ConfigurationPropertiesScan
import java.time.ZoneId
import java.util.*

@SpringBootApplication(
    exclude = [DataSourceAutoConfiguration::class],
    scanBasePackages = [
        "demo.craft.quickbooks.accounting",
        "demo.craft.common.communication" // for kafka publisher
    ]
)
@ConfigurationPropertiesScan(
    basePackageClasses = [
        QuickbooksAccountingProperties::class,
        CommunicationProperties::class
    ]
)
class QuickbooksAccountingApp {
    init {
        TimeZone.setDefault(TIME_ZONE_UTC)
    }

    companion object {
        val TIME_ZONE_UTC: TimeZone = TimeZone.getTimeZone(ZoneId.of("UTC"))

        @JvmStatic
        fun main(args: Array<String>) {
            SpringApplication.run(QuickbooksAccountingApp::class.java, *args)
        }
    }
}
