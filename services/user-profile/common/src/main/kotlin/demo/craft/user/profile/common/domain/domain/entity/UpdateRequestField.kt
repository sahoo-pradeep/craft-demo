package demo.craft.user.profile.common.domain.domain.entity

import demo.craft.user.profile.common.domain.domain.enums.FieldName
import demo.craft.user.profile.common.domain.domain.model.InvalidField
import demo.craft.user.profile.common.domain.util.ValidationUtil
import java.time.LocalDateTime
import javax.persistence.Entity
import javax.persistence.EnumType
import javax.persistence.Enumerated
import javax.persistence.GeneratedValue
import javax.persistence.GenerationType
import javax.persistence.Id
import org.hibernate.annotations.CreationTimestamp

@Entity
data class UpdateRequestField(
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null,
    val requestUuid: String,
    @Enumerated(EnumType.STRING)
    val field: FieldName,
    val oldValue: String? = null,
    val newValue: String,
    @CreationTimestamp
    val createdAt: LocalDateTime? = null
) {
    fun validate(): List<InvalidField> {
        val invalidFields = mutableListOf<InvalidField>()

        when (field) {
            FieldName.PAN, FieldName.EIN ->
                invalidFields.add(InvalidField(field, "${field.name} cannot be updated"))

            FieldName.BUSINESS_ADDRESS_ZIP, FieldName.LEGAL_ADDRESS_ZIP -> {
                ValidationUtil.validateZip(newValue)?.let {
                    invalidFields.add(InvalidField(field, it))
                }
            }

            // TODO: add more validations

            else -> {} // do nothing
        }

        return invalidFields
    }
}
