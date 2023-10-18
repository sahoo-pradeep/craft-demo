package demo.craft.user.profile.common.config

import org.springframework.boot.context.properties.ConfigurationProperties

@ConfigurationProperties("demo.craft.user-profile")
class UserProfileProperties {
    val businessProfile = BusinessProfileProperties()

    class BusinessProfileProperties {
        val kafka = Kafka()

        class Kafka {
            lateinit var changeRequestTopicName: String
            lateinit var changeRequestProductTopicName: String
        }
    }
}
