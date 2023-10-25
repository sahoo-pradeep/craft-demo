package demo.craft.user.profile

import demo.craft.common.lock.LockManager
import java.time.Duration

class TestLockManager : LockManager {
    override fun <T> tryWithLock(namespace: String, key: String, timeout: Duration, function: () -> T): T =
        function()
}