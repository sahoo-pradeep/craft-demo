package demo.craft.user.profile.common.domain.entity

import java.time.LocalDateTime
import javax.persistence.Entity
import javax.persistence.GeneratedValue
import javax.persistence.GenerationType
import javax.persistence.Id
import org.hibernate.annotations.CreationTimestamp

@Entity
data class Address(
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null,
    val line1: String,
    val line2: String? = null,
    val line3: String? = null,
    val city: String,
    val state: String,
    val zip: String,
    val country: String,
    @CreationTimestamp
    val createdAt: LocalDateTime? = null,
) {
    fun validate() {
        val (valid, failureReason) = when {
            line1.isEmpty() -> Pair(false, "line1 should not be empty")
            city.isEmpty() -> Pair(false, "city should not be empty")
            state.isEmpty() -> Pair(false, "state should not be empty")
            zip.isEmpty() -> Pair(false, "zip should not be empty")
            country.isEmpty() -> Pair(false, "country should not be empty")

            else -> Pair(true, null)
        }

        if (!valid) {
            throw IllegalArgumentException(failureReason)
        }
    }
}