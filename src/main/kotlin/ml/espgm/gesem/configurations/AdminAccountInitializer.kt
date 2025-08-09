package ml.espgm.gesem.configurations

import ml.espgm.gesem.entities.Admin
import ml.espgm.gesem.enums.GesemRole
import ml.espgm.gesem.repositories.AdminRepo
import mu.KotlinLogging
import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.CommandLineRunner
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Component

private val logger = KotlinLogging.logger {}

@Component
class AdminAccountInitializer(
    private val adminRepo: AdminRepo,
    private val passwordEncoder: PasswordEncoder,
    @Value("\${espgm.gesem.default.username}") private val defaultUsername: String,
    @Value("\${espgm.gesem.default.password}") private val defaultPassword: String,
    @Value("\${espgm.gesem.default.email}") private val defaultEmail: String,
): CommandLineRunner {
    override fun run(vararg args: String?) {
        if (adminRepo.count() == 0L) {
            val defaultAdmin = Admin(
                fullName = "Drissa Sidiki Traore",
                username = defaultUsername,
                email = defaultEmail,
                password = passwordEncoder.encode(defaultPassword),
                role = GesemRole.ADMIN,
                enabled = true,
                otpValidated = true,
                canResetPassword = false
            )

            adminRepo.save(defaultAdmin)
            logger.info { "Compte admin par défaut créé" }
        } else {
            logger.info { "Des comptes admin existent déjà, aucun compte par défaut créé." }
        }
    }
}