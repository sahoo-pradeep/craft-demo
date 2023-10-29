package demo.craft.quickbooks.payroll

import demo.craft.common.communication.config.CommunicationProperties
import demo.craft.quickbooks.payroll.config.QuickbooksPayrollProperties
import java.time.ZoneId
import java.util.TimeZone
import org.springframework.boot.SpringApplication
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration
import org.springframework.boot.context.properties.ConfigurationPropertiesScan

@SpringBootApplication(
    exclude = [DataSourceAutoConfiguration::class],
    scanBasePackages = [
        "demo.craft.quickbooks.payroll",
        "demo.craft.common.communication" // for kafka publisher
    ]
)
@ConfigurationPropertiesScan(
    basePackageClasses = [
        QuickbooksPayrollProperties::class,
        CommunicationProperties::class
    ]
)
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
