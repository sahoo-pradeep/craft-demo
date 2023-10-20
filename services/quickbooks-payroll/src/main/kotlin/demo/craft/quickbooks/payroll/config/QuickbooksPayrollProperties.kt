package demo.craft.quickbooks.payroll.config

import org.springframework.boot.context.properties.ConfigurationProperties

@ConfigurationProperties("demo.craft.quickbooks-payroll")
class QuickbooksPayrollProperties {
    val kafka = Kafka()

    class Kafka {
        lateinit var businessProfileValidationRequestTopic: String
        lateinit var businessProfileValidationResponseTopic: String
    }
}
