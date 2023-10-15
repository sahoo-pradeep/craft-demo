package demo.craft.user.profile.common.domain.entity

import demo.craft.user.profile.common.domain.exception.InvalidBusinessProfileException
import java.time.LocalDateTime
import javax.persistence.Entity
import javax.persistence.GeneratedValue
import javax.persistence.GenerationType
import javax.persistence.Id
import javax.persistence.OneToOne
import org.hibernate.annotations.CreationTimestamp
import org.hibernate.annotations.UpdateTimestamp

@Entity
data class BusinessProfile(
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null,
    val userId: String,
    val companyName: String,
    val legalName: String,
    @OneToOne
    val businessAddress: Address,
    @OneToOne
    val legalAddress: Address,
    val pan: String,
    val ein: String,
    val email: String,
    val website: String,
    @CreationTimestamp
    val createdAt: LocalDateTime? = null,
    @UpdateTimestamp
    val updatedAt: LocalDateTime? = null,
) {
    fun validate() {
        val (valid, failureReason) = when {
            companyName.isEmpty() -> Pair(false, "company name should not be empty")
            legalName.isEmpty() -> Pair(false, "legal name should not be empty")
            pan.isEmpty() -> Pair(false, "pan should not be empty")
            ein.isEmpty() -> Pair(false, "ein should not be empty")
            email.isEmpty() -> Pair(false, "email should not be empty")
            website.isEmpty() -> Pair(false, "website should not be empty")

            else -> Pair(true, null)
        }

        if (!valid) {
            throw InvalidBusinessProfileException(userId, failureReason)
        }

        try {
            businessAddress.validate()
            legalAddress.validate()
        } catch (e: IllegalArgumentException) {
            throw InvalidBusinessProfileException(userId, e.message)
        }
    }
}