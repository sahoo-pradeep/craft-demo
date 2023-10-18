package demo.craft.common.communication.config

import java.time.Duration
import org.springframework.boot.context.properties.ConfigurationProperties

@ConfigurationProperties("demo.craft.communication")
class CommunicationProperties {
    val kafka = KafkaProperties()

    class KafkaProperties {
        var bootstrapServers: String = ""
        val consumer = KafkaConsumerProperties()

        class KafkaConsumerProperties {
            lateinit var groupId: String

            var enabled: Boolean = true
            var enableAutoCommitConfig: Boolean = true
            var autoCommitInterval: Duration = Duration.ofMillis(100)
            var sessionTimeout: Duration = Duration.ofSeconds(10)
            var concurrency: Int = 3
        }
    }
}