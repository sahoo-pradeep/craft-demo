package demo.craft.user.profile.common.domain.config

import org.springframework.boot.context.properties.ConfigurationProperties

@ConfigurationProperties("demo.craft.user-profile")
class UserProfileProperties {
    val businessProfile = BusinessProfile()

    class BusinessProfile {
        val kafka = Kafka()

        class Kafka {
            lateinit var changeRequestTopicName: String
        }
    }
}
