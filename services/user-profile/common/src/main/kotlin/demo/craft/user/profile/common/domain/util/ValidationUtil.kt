package demo.craft.user.profile.common.domain.util

object ValidationUtil {
    fun isPanValid(pan: String): Boolean {
        if (pan.length != 10) {
            return false
        }

        return pan.matches(Regex("^[A-Z]{3}[TFHPC][0-9]{4}[A-Z]$"))
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
}