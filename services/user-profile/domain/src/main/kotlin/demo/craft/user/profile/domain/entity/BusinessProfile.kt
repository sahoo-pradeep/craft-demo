package demo.craft.user.profile.domain.entity

import demo.craft.user.profile.domain.enums.FieldName
import demo.craft.user.profile.domain.model.AddressType
import org.hibernate.annotations.CreationTimestamp
import org.hibernate.annotations.UpdateTimestamp
import java.time.LocalDateTime
import javax.persistence.*

@Entity
data class BusinessProfile(
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null,
    val userId: String,
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
) {
    fun validateFields(): List<Pair<FieldName, String>> {
        val invalidFields = mutableListOf<Pair<FieldName, String>>()

        when {
            companyName.isEmpty() -> invalidFields.add(
                Pair(
                    FieldName.COMPANY_NAME,
                    "company name should not be empty"
                )
            )

            legalName.isEmpty() -> invalidFields.add(
                Pair(
                    FieldName.COMPANY_NAME,
                    "company name should not be empty"
                )
            )

            pan.isEmpty() -> invalidFields.add(Pair(FieldName.PAN, "pan should not be empty"))

            !isPanValid(pan) -> invalidFields.add(Pair(FieldName.PAN, "invalid pan"))

            ein.isEmpty() -> invalidFields.add(Pair(FieldName.EIN, "ein should not be empty"))

            !isEinValid(ein) -> invalidFields.add(Pair(FieldName.EIN, "invalid ein"))

            email.isEmpty() -> invalidFields.add(Pair(FieldName.EMAIL, "email should not be empty"))
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