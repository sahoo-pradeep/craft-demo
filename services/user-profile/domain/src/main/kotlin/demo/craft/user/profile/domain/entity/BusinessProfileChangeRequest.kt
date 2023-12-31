package demo.craft.user.profile.domain.entity

import demo.craft.user.profile.domain.enums.ChangeRequestOperation
import demo.craft.user.profile.domain.enums.ChangeRequestStatus
import java.time.LocalDateTime
import javax.persistence.*
import org.hibernate.annotations.CreationTimestamp
import org.hibernate.annotations.UpdateTimestamp

@Entity
data class BusinessProfileChangeRequest(
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null,
    val requestId: String,
    val userId: String,
    @Enumerated(EnumType.STRING)
    val operation: ChangeRequestOperation,
    @Enumerated(EnumType.STRING)
    val status: ChangeRequestStatus,
    val companyName: String,
    val legalName: String,
    val pan: String,
    val ein: String,
    val email: String,
    val website: String? = null,
    @OneToOne
    val businessAddress: Address,
    @OneToOne
    val legalAddress: Address,
    @CreationTimestamp
    val createdAt: LocalDateTime? = null,
    @UpdateTimestamp
    val updatedAt: LocalDateTime? = null,
)