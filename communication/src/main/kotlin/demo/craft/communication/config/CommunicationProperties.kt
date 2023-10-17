package demo.craft.communication.config

import org.springframework.boot.context.properties.ConfigurationProperties

@ConfigurationProperties("demo.craft.communication")
class CommunicationProperties {
    val kafka = KafkaProperties()

    class KafkaProperties {
        var bootstrapServers: String = ""
    }
}