package ml.espgm.gesem.helpers

import ml.espgm.gesem.enums.GesemRole

fun String?.toAppRoleOrThrow(): GesemRole {
    return this?.takeIf { it.isNotBlank() }
        ?.let {
            runCatching { GesemRole.valueOf(it.uppercase()) }.getOrElse {
                throw IllegalArgumentException("role invalide : $it")
            }
        }
        ?: throw IllegalArgumentException("role ne peut pas être null ou vide")
}

fun generateOtp(): String = (1000..9999).random().toString()

object DataValidatorRegex {

    val EMAIL_REGEX = Regex("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$")

    /**
     * Téléphone format : +<pays> <numéro>
     * Ex : +223 75966321
     */
    val PHONE_REGEX = Regex("""^\+\d{1,4} \d{6,15}$""")

    val PASSWORD_REGEX = Regex("""^(?=.*[A-Za-z])(?=.*\d)[A-Za-z\d\W]{6,}$""")

    /**
     * Username :
     * - Ne commence pas par un chiffre
     * - Lettres et chiffres uniquement
     * - Longueur : 6 à 12 caractères
     */
    val USERNAME_REGEX = Regex("""^[A-Za-z][A-Za-z0-9]{5,11}$""")

    val IPV4_REGEX = Regex("""^((25[0-5]|2[0-4]\d|1\d{2}|[1-9]?\d)\.){3}(25[0-5]|2[0-4]\d|1\d{2}|[1-9]?\d)$""")

    fun isValidEmail(email: String): Boolean = EMAIL_REGEX.matches(email)

    fun isValidIp(ip: String): Boolean = IPV4_REGEX.matches(ip)

    fun isValidPhone(phone: String): Boolean = PHONE_REGEX.matches(phone)

    fun isValidUsername(username: String): Boolean = USERNAME_REGEX.matches(username)

    fun isStrongPassword(
        password: String,
        username: String,
        fullName: String,
        email: String,
        phone: String
    ): Boolean {
        if (!PASSWORD_REGEX.matches(password)) {
            return false
        }

        val lowerPassword = password.lowercase()
        val lowerUsername = username.lowercase()
        val lowerFullName = fullName.lowercase()
        val emailPrefix = email.substringBefore("@").lowercase()
        val phoneDigits = phone.filter { it.isDigit() }

        // Vérifie qu’il ne contient pas le username
        if (lowerUsername.isNotBlank() && lowerPassword.contains(lowerUsername)) {
            return false
        }

        // Vérifier si une partie du nom (de > 3 lettres) est dans le mot de passe
        lowerFullName.split(" ")
            .filter { it.length >= 3 }
            .forEach {
                if (it in lowerPassword) {
                    println("Mot de passe contient le nom ou prénom.")
                    return false
                }
            }

        // Vérifier si la partie avant @ de l’email est dans le mot de passe
        if (emailPrefix.length >= 3 && emailPrefix in lowerPassword) {
            println("Mot de passe contient l’adresse email.")
            return false
        }

        // Vérifier si une séquence de 4+ chiffres du téléphone est dans le mot de passe
        for (i in 0..phoneDigits.length - 4) {
            val seq = phoneDigits.substring(i, i + 4)
            if (seq in password) {
                println("Mot de passe contient une partie du numéro de téléphone.")
                return false
            }
        }

        return true
    }

}
