package demo.craft.user.profile.common.domain.entity

import demo.craft.user.profile.common.domain.enums.ChangeRequestStatus
import java.time.LocalDateTime
import javax.persistence.Entity
import javax.persistence.EnumType
import javax.persistence.Enumerated
import javax.persistence.GeneratedValue
import javax.persistence.GenerationType
import javax.persistence.Id
import javax.persistence.OneToOne
import org.hibernate.annotations.CreationTimestamp
import org.hibernate.annotations.UpdateTimestamp

@Entity
data class BusinessProfileChangeRequest(
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null,
    val requestUuid: String,
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
    @Enumerated(EnumType.STRING)
    val status: ChangeRequestStatus,
    val failureReason: String? = null,
    @CreationTimestamp
    val createdAt: LocalDateTime? = null,
    @UpdateTimestamp
    val updatedAt: LocalDateTime? = null,
)