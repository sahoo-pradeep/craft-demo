package demo.craft.user.profile.dao.access.cache

import com.fasterxml.jackson.core.type.TypeReference
import com.fasterxml.jackson.databind.SerializationFeature
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import demo.craft.common.cache.CacheManager
import demo.craft.user.profile.common.config.UserProfileProperties
import java.time.Duration
import org.springframework.stereotype.Component

@Component
class GenericCacheManager(
    private val cacheManager: CacheManager,
    userProfileProperties: UserProfileProperties
) {
    private val cacheProperties = userProfileProperties.cache

    private val objectMapper = jacksonObjectMapper().apply {
        registerModule(JavaTimeModule()) // Register JavaTimeModule to handle Java 8 date/time types
        configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false)
    }

    fun <T : Any> cacheLookup(
        key: String,
        valueTypeRef: TypeReference<T>,
        ttl: Duration = cacheProperties.defaultCacheTtl,
        onCacheMiss: () -> T
    ): T =
        cacheManager.get(key)?.let { valueString ->
            objectMapper.readValue(valueString, valueTypeRef)
        } ?: onCacheMiss().also { value ->
            cacheUpdate(key, valueTypeRef, value)
        }

    fun <T : Any> cacheLookupForNullable(
        key: String,
        valueTypeRef: TypeReference<T>,
        ttl: Duration = cacheProperties.defaultCacheTtl,
        onCacheMiss: () -> T?
    ): T? =
        cacheManager.get(key)?.let { valueString ->
            objectMapper.readValue(valueString, valueTypeRef)
        } ?: onCacheMiss()?.also { value ->
            cacheUpdate(key, valueTypeRef, value)
        }

    fun <T> cacheUpdate(
        key: String,
        valueTypeRef: TypeReference<T>,
        cacheValue: T,
        ttl: Duration = cacheProperties.defaultCacheTtl
    ) =
        cacheManager.put(key, objectMapper.writeValueAsString(cacheValue), ttl.seconds)
}