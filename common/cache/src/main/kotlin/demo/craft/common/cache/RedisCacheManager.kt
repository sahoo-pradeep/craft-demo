package demo.craft.common.cache

import mu.KotlinLogging
import org.springframework.stereotype.Component
import redis.clients.jedis.JedisPool

@Component
class RedisCacheManager(
    private val jedisPool: JedisPool
) : CacheManager {
    private val log = KotlinLogging.logger {}
    override fun put(key: String, value: String, ttl: Long?): Unit =
        jedisPool.resource.use { jedis ->
            if (ttl == null) {
                jedis.set(key, value).also {
                    log.info { "RedisCache: Data saved successfully. key = '$key', value = '$value'" }
                }
            } else {
                jedis.setex(key, ttl, value).also {
                    log.info { "RedisCache: Data saved successfully. key = '$key', value = '$value', ttl = '$ttl'" }
                }
            }
        }

    override fun get(key: String): String? =
        jedisPool.resource.use { jedis ->
            jedis.get(key).also {
                log.info { "RedisCache: Data fetched successfully. key = '$key', value = '$it'" }
            }
        }

    override fun evict(keys: List<String>) =
        jedisPool.resource.use { jedis ->
            keys.forEach {
                val deletedCount = jedis.del(it)
                log.info("RedisCache: Data deleted successfully. key = '$it', count = '$deletedCount'")
            }
        }

    override fun getKeys(pattern: Regex): List<String> =
        jedisPool.resource.use { jedis ->
            jedis.keys(pattern.pattern).toList()
        }.also {
            log.info { "RedisCache: Keys fetched successfully. pattern = '${pattern.pattern}', keys = '$it'" }
        }
}