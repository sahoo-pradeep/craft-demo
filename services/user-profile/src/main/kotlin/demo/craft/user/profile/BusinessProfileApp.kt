package demo.craft.user.profile

import demo.craft.common.communication.config.CommunicationProperties
import demo.craft.user.profile.common.config.UserProfileProperties
import org.springframework.boot.SpringApplication
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.context.properties.ConfigurationPropertiesScan
import org.springframework.data.jpa.repository.config.EnableJpaRepositories
import java.time.ZoneId
import java.util.*

@SpringBootApplication(
    scanBasePackages = [
        "demo.craft.user.profile",
        "demo.craft.common.communication" // for kafka publisher
    ]
)
@ConfigurationPropertiesScan(
    basePackageClasses = [
        UserProfileProperties::class,
        CommunicationProperties::class
    ]
)
@EnableJpaRepositories
class BusinessProfileApp {
    init {
        TimeZone.setDefault(TIME_ZONE_UTC)
    }

    companion object {
        val TIME_ZONE_UTC: TimeZone = TimeZone.getTimeZone(ZoneId.of("UTC"))

        @JvmStatic
        fun main(args: Array<String>) {
            SpringApplication.run(BusinessProfileApp::class.java, *args)
        }
    }
}
