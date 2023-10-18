package demo.craft.user.profile.common.config

import org.springframework.boot.context.properties.ConfigurationProperties

@ConfigurationProperties("demo.craft.user-profile")
class UserProfileProperties {
    val businessProfile = BusinessProfileProperties()
    val integration = IntegrationProperties()

    class BusinessProfileProperties {
        val kafka = Kafka()

        class Kafka {
            lateinit var changeRequestTopicName: String
            lateinit var changeRequestProductTopicName: String
        }
    }

    class IntegrationProperties {
        var productSubscription = ProductSubscriptionProperties()

        class ProductSubscriptionProperties {
            lateinit var url: String
        }
    }
}
