package demo.craft.user.profile.common.domain.domain.entity

import demo.craft.user.profile.common.domain.domain.enums.FieldName
import demo.craft.user.profile.common.domain.domain.model.AddressType
import demo.craft.user.profile.common.domain.domain.model.InvalidField
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
    val website: String? = null,
    @CreationTimestamp
    val createdAt: LocalDateTime? = null,
    @UpdateTimestamp
    val updatedAt: LocalDateTime? = null,
) {
    fun validateFields(): List<InvalidField> {
        val invalidFields = mutableListOf<InvalidField>()

        when {
            companyName.isEmpty() -> invalidFields.add(
                InvalidField(
                    FieldName.COMPANY_NAME,
                    "company name should not be empty"
                )
            )

            legalName.isEmpty() -> invalidFields.add(
                InvalidField(
                    FieldName.COMPANY_NAME,
                    "company name should not be empty"
                )
            )

            pan.isEmpty() -> invalidFields.add(InvalidField(FieldName.PAN, "pan should not be empty"))

            !isPanValid(pan) -> invalidFields.add(InvalidField(FieldName.PAN, "invalid pan"))

            ein.isEmpty() -> invalidFields.add(InvalidField(FieldName.EIN, "ein should not be empty"))

            !isEinValid(ein) -> invalidFields.add(InvalidField(FieldName.EIN, "invalid ein"))

            email.isEmpty() -> invalidFields.add(InvalidField(FieldName.EMAIL, "email should not be empty"))
        }

        invalidFields.addAll(businessAddress.validateFields(AddressType.BUSINESS))
        invalidFields.addAll(legalAddress.validateFields(AddressType.LEGAL))

        return invalidFields
    }

    private fun isPanValid(pan: String): Boolean {
        if (pan.length != 10) {
            return false
        }

        return pan.matches(Regex("^[A-Z]{3}[TFHPC][A-Z][0-9]{4}[A-Z]$"))
    }

    // valid ein: XX-XXXXXXX where X is a digit from 0-9
    private fun isEinValid(ein: String): Boolean {
        val einParts = ein.split("-")

        return when {
            einParts.size != 2 -> false
            einParts[0].length != 2 -> false
            einParts[0].any { !it.isDigit() } -> false
            einParts[1].length != 7 -> false
            einParts[1].any { !it.isDigit() } -> false
            else -> true
        }
    }
}