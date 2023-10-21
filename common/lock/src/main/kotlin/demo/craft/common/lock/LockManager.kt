package demo.craft.common.lock

import java.time.Duration

interface LockManager {
    fun <T> tryWithLock(namespace: String, key: String, timeout: Duration, function: () -> T): T
}