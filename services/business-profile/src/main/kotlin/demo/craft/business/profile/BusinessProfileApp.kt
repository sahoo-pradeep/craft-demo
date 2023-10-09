package demo.craft.business.profile

import demo.craft.business.profile.config.BusinessProfileProperties
import java.time.ZoneId
import java.util.TimeZone
import org.springframework.boot.SpringApplication
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan
import org.springframework.data.jpa.repository.config.EnableJpaRepositories

@SpringBootApplication(
    scanBasePackages = [
        "demo.craft.business.profile"
    ]
)
@ConfigurationPropertiesScan(
    basePackageClasses = [
        BusinessProfileProperties::class,
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