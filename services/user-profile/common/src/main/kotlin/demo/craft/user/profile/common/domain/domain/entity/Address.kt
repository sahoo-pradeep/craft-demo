package demo.craft.user.profile.common.domain.domain.entity

import demo.craft.user.profile.common.domain.domain.enums.AddressFieldName
import demo.craft.user.profile.common.domain.domain.enums.FieldName
import demo.craft.user.profile.common.domain.domain.model.AddressType
import demo.craft.user.profile.common.domain.domain.model.InvalidField
import java.time.LocalDateTime
import javax.persistence.Entity
import javax.persistence.GeneratedValue
import javax.persistence.GenerationType
import javax.persistence.Id
import org.hibernate.annotations.CreationTimestamp
import org.hibernate.annotations.UpdateTimestamp

@Entity
data class Address(
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null,
    val userId: String,
    val line1: String,
    val line2: String? = null,
    val line3: String? = null,
    val city: String,
    val state: String,
    val zip: String,
    val country: String,
    @CreationTimestamp
    val createdAt: LocalDateTime? = null,
    @UpdateTimestamp
    val updatedAt: LocalDateTime? = null,
) {
    fun validateFields(addressType: AddressType): List<InvalidField> {
        val invalidFields = mutableListOf<InvalidField>()

        if (line1.isEmpty()) {
            invalidFields.add(
                InvalidField(getFieldName(addressType, AddressFieldName.LINE_1), "line1 should not be empty")
            )
        }

        if (city.isEmpty()) {
            invalidFields.add(
                InvalidField(
                    getFieldName(addressType, AddressFieldName.CITY), "city should not be empty"
                )
            )
        }

        if (state.isEmpty()) {
            invalidFields.add(
                InvalidField(
                    getFieldName(addressType, AddressFieldName.STATE), "state should not be empty"
                )
            )
        }

        if (zip.isEmpty()) {
            invalidFields.add(
                InvalidField(
                    getFieldName(addressType, AddressFieldName.ZIP), "zip should not be empty"
                )
            )
        }

        if (country.isEmpty()) {
            invalidFields.add(
                InvalidField(
                    getFieldName(addressType, AddressFieldName.COUNTRY), "country should not be empty"
                )
            )
        }

        return invalidFields
    }

    private fun getFieldName(addressType: AddressType, addressFieldName: AddressFieldName): FieldName =
        when (addressType) {
            AddressType.LEGAL -> when (addressFieldName) {
                AddressFieldName.LINE_1 -> FieldName.LEGAL_ADDRESS_LINE_1
                AddressFieldName.LINE_2 -> FieldName.LEGAL_ADDRESS_LINE_2
                AddressFieldName.LINE_3 -> FieldName.LEGAL_ADDRESS_LINE_3
                AddressFieldName.CITY -> FieldName.LEGAL_ADDRESS_CITY
                AddressFieldName.STATE -> FieldName.LEGAL_ADDRESS_STATE
                AddressFieldName.ZIP -> FieldName.LEGAL_ADDRESS_ZIP
                AddressFieldName.COUNTRY -> FieldName.LEGAL_ADDRESS_COUNTRY
            }

            AddressType.BUSINESS -> when (addressFieldName) {
                AddressFieldName.LINE_1 -> FieldName.BUSINESS_ADDRESS_LINE_1
                AddressFieldName.LINE_2 -> FieldName.BUSINESS_ADDRESS_LINE_2
                AddressFieldName.LINE_3 -> FieldName.BUSINESS_ADDRESS_LINE_3
                AddressFieldName.CITY -> FieldName.BUSINESS_ADDRESS_CITY
                AddressFieldName.STATE -> FieldName.BUSINESS_ADDRESS_STATE
                AddressFieldName.ZIP -> FieldName.BUSINESS_ADDRESS_ZIP
                AddressFieldName.COUNTRY -> FieldName.BUSINESS_ADDRESS_COUNTRY
            }
        }

    fun validateZip(zip: String): String? {
        if (zip.length !in 5..6) {
            return "zip should be 5 or 6 character long"
        }

        if (zip.any { !it.isDigit() }) {
            return "zip should be digits"
        }

        return null
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Address

        return when {
            line1 != other.line1 -> false
            line2 != other.line2 -> false
            line3 != other.line3 -> false
            city != other.city -> false
            state != other.state -> false
            zip != other.zip -> false
            country != other.country -> false
            else -> true
        }
    }

    override fun hashCode(): Int {
        var result = line1.hashCode()
        result = 31 * result + (line2?.hashCode() ?: 0)
        result = 31 * result + (line3?.hashCode() ?: 0)
        result = 31 * result + city.hashCode()
        result = 31 * result + state.hashCode()
        result = 31 * result + zip.hashCode()
        result = 31 * result + country.hashCode()
        return result
    }
}