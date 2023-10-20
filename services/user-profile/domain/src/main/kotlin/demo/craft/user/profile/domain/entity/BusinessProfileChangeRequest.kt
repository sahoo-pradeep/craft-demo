package demo.craft.user.profile.domain.entity

import demo.craft.user.profile.domain.enums.ChangeRequestOperation
import demo.craft.user.profile.domain.enums.ChangeRequestStatus
import org.hibernate.annotations.CreationTimestamp
import org.hibernate.annotations.UpdateTimestamp
import java.time.LocalDateTime
import javax.persistence.*

@Entity
data class BusinessProfileChangeRequest(
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null,
    val requestId: String,
    val userId: String,
    @Enumerated(EnumType.STRING)
    val operation: ChangeRequestOperation,
    val companyName: String,
    val legalName: String,
    @OneToOne
    val businessAddress: Address,
    @OneToOne
    val legalAddress: Address,
    val pan: String,
    val ein: String,
    val email: String,
    val website: String? = null,
    @Enumerated(EnumType.STRING)
    val status: ChangeRequestStatus,
    @CreationTimestamp
    val createdAt: LocalDateTime? = null,
    @UpdateTimestamp
    val updatedAt: LocalDateTime? = null,
)