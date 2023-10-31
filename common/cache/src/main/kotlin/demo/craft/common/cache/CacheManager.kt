package demo.craft.common.cache

interface CacheManager {
    fun put(key: String, value: String, ttl: Long? = null): Unit
    fun get(key: String): String?
    fun evict(keys: List<String>): Unit
    fun getKeys(pattern: Regex): List<String>
}