package demo.craft.common.lock.config

import org.springframework.boot.context.properties.ConfigurationProperties
import java.time.Duration

@ConfigurationProperties("demo.craft.lock")
class LockManagerProperties {
    var retryDelay: Duration = Duration.ofMillis(100)
}