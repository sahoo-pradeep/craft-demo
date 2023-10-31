package demo.craft.common.cache.config

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import redis.clients.jedis.JedisPool
import redis.clients.jedis.JedisPoolConfig

@Configuration
class CacheConfig(
    cacheProperties: CacheProperties
) {
    private val redisProperties = cacheProperties.redis

    @Bean
    fun cachingJedisPool() = JedisPool(
        JedisPoolConfig().apply { maxTotal = 128 },
        redisProperties.url,
        redisProperties.port,
        redisProperties.readTimeoutInMs
    )
}