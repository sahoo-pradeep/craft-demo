package demo.craft.user.profile.common.config

import org.springframework.boot.context.properties.ConfigurationProperties
import java.time.Duration

@ConfigurationProperties("demo.craft.user-profile")
class UserProfileProperties {
    val kafka = Kafka()
    val integration = IntegrationProperties()
    val lock = LockProperties()

    class Kafka {
        lateinit var businessProfileChangeRequestTopic: String
        lateinit var businessProfileValidationRequestTopic: String
        lateinit var businessProfileValidationResponseTopic: String
    }

    class IntegrationProperties {
        var productSubscription = ProductSubscriptionProperties()

        class ProductSubscriptionProperties {
            lateinit var url: String
        }
    }

    class LockProperties {
        var timeout: Duration = Duration.ofSeconds(2)
    }
}
