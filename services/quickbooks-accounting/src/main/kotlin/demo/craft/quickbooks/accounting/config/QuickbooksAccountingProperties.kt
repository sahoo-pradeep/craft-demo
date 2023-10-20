package demo.craft.quickbooks.accounting.config

import org.springframework.boot.context.properties.ConfigurationProperties

@ConfigurationProperties("demo.craft.quickbooks-accounting")
class QuickbooksAccountingProperties {
    val kafka = Kafka()

    class Kafka {
        lateinit var businessProfileValidationRequestTopic: String
        lateinit var businessProfileValidationResponseTopic: String
    }
}
