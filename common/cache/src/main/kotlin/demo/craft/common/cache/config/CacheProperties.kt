package demo.craft.common.cache.config

import kotlin.properties.Delegates
import org.springframework.boot.context.properties.ConfigurationProperties

@ConfigurationProperties("demo.craft.cache")
class CacheProperties {
    val redis = RedisProperties()

    class RedisProperties {
        lateinit var url: String
        var port by Delegates.notNull<Int>()
        var readTimeoutInMs by Delegates.notNull<Int>()
    }
}