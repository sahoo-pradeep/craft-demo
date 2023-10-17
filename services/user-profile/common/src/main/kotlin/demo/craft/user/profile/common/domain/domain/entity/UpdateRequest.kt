package demo.craft.user.profile.common.domain.domain.entity

import demo.craft.user.profile.common.domain.domain.enums.UpdateRequestOperation
import demo.craft.user.profile.common.domain.domain.enums.UpdateRequestStatus
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
data class UpdateRequest(
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null,
    val requestUuid: String,
    val userId: String,
    @Enumerated(EnumType.STRING)
    val operation: UpdateRequestOperation,
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
    val status: UpdateRequestStatus,
    @CreationTimestamp
    val createdAt: LocalDateTime? = null,
    @UpdateTimestamp
    val updatedAt: LocalDateTime? = null,
)