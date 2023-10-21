package demo.craft.common.lock.config

import demo.craft.common.lock.LockManager
import demo.craft.common.lock.PostgresLockManager
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.jdbc.core.JdbcTemplate

@Configuration
class LockManagerConfig(
    private val jdbcTemplate: JdbcTemplate,
    private val properties: LockManagerProperties
) {
    @Bean
    fun lockManager(
    ): LockManager =
        PostgresLockManager(jdbcTemplate, properties)
}