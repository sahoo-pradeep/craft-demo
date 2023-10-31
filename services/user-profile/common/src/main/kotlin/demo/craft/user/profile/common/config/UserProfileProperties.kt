package demo.craft.user.profile.common.config

import java.time.Duration
import org.springframework.boot.context.properties.ConfigurationProperties

@ConfigurationProperties("demo.craft.user-profile")
class UserProfileProperties {
    val kafka = KafkaProperties()
    val integration = IntegrationProperties()
    val lock = LockProperties()
    val cache = CacheProperties()

    class KafkaProperties {
        var businessProfileChangeRequestTopic: String = "business-profile-change-request"
        var businessProfileValidationRequestTopic: String = "business-profile-validation-request"
        var businessProfileValidationResponseTopic: String = "business-profile-validation-response"
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

    class CacheProperties {
        var defaultCacheTtl: Duration = Duration.ofDays(7)
    }
}
