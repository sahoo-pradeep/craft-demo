package demo.craft.common.lock

import demo.craft.common.lock.config.LockManagerProperties
import mu.KotlinLogging
import org.springframework.jdbc.core.JdbcTemplate
import java.math.BigInteger
import java.security.MessageDigest
import java.time.Duration
import java.util.concurrent.TimeoutException

class PostgresLockManager(
    private val jdbcTemplate: JdbcTemplate,
    properties: LockManagerProperties
) : LockManager {
    private val log = KotlinLogging.logger {}
    private val retryDelayInMs = properties.retryDelay.toMillis()

    override fun <T> tryWithLock(namespace: String, key: String, timeout: Duration, function: () -> T): T {
        lock(namespace, key, timeout)
        return function()
    }

    private fun lock(namespace: String, key2String: String, timeout: Duration) {
        val timeoutMs = timeout.toMillis()
        val startMs = System.currentTimeMillis()
        val key1: Int = hashToInt(namespace)
        val key2: Int = hashToInt(key2String)

        val logSuffix = "pg_try_advisory_xact_lock($key1, $key2) for namespace=$namespace key=$key2String"
        log.debug("Acquiring $logSuffix")

        while (!jdbcTemplate.queryForObject("select pg_try_advisory_xact_lock(?, ?)", Boolean::class.java, key1, key2)) {
            if (System.currentTimeMillis() < startMs + timeoutMs) {
                log.debug { "Waiting for $retryDelayInMs ms to acquire $logSuffix" }
                Thread.sleep(retryDelayInMs)
            } else {
                throw TimeoutException("Timed out waiting for $logSuffix")
            }
        }
        log.info { "Acquired $logSuffix in ${System.currentTimeMillis() - startMs} ms" }
    }

    // Consistent hashing of a string to Int
    private fun hashToInt(str: String): Int {
        val md = MessageDigest.getInstance("MD5")
        return BigInteger(1, md.digest(str.toByteArray())).toInt()
    }
}