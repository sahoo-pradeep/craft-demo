package demo.craft.user.profile.common.config

import java.time.Duration
import org.springframework.boot.context.properties.ConfigurationProperties

@ConfigurationProperties("demo.craft.user-profile")
class UserProfileProperties {
    val kafka = KafkaProperties()
    val integration = IntegrationProperties()
    val lock = LockProperties()

    class KafkaProperties {
        var businessProfileChangeRequestTopic: String = ""
        var businessProfileValidationRequestTopic: String = ""
        var businessProfileValidationResponseTopic: String = ""
    }

    class IntegrationProperties {
        var productSubscription = ProductSubscriptionProperties()

        class ProductSubscriptionProperties {
            var url: String = ""
        }
    }

    class LockProperties {
        var timeout: Duration = Duration.ofSeconds(2)
    }
}
