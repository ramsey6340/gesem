package ml.espgm.gesem.services.impls

import ml.espgm.gesem.configurations.OtpCodeProperties
import ml.espgm.gesem.enums.OtpContext
import ml.espgm.gesem.enums.SecurityState
import ml.espgm.gesem.helpers.generateOtp
import ml.espgm.gesem.repositories.AdminRepo
import ml.espgm.gesem.repositories.IpSecurityStateRepo
import ml.espgm.gesem.services.IpSecurityStateService
import ml.espgm.gesem.services.MailService
import ml.espgm.gesem.services.OtpService
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDateTime

@Service
class OtpImpl(
    private val repo: AdminRepo,
    private val mailService: MailService,
    private val ipSecurityStateService: IpSecurityStateService,
    private val ipSecurityStateRepo: IpSecurityStateRepo,
    private val otpCodeProperties: OtpCodeProperties,
): OtpService {
    @Transactional
    override fun verifyOtp(email: String, otp: String, ip: String?, context: OtpContext): String {
        val user = repo.findByEmail(email)
            .orElseThrow { IllegalArgumentException("Admin introuvable") }

        if (ip != null && context == OtpContext.LOGIN_ATTEMPT_FAILED) {
            val ipState = ipSecurityStateService.get(ip)
            require(ipState.otp == otp) { "OTP invalide" }
            require(!(ipState.otpExpiresAt!!.isBefore(LocalDateTime.now()))) { "OTP expiré" }

            ipState.otp = null
            ipState.otpExpiresAt = null
            if (ipState.state == SecurityState.REQUIRE_OTP) {
                ipState.state = SecurityState.LIMITED_ATTEMPTS
            }
            ipSecurityStateRepo.save(ipState)
        }
        else {
            require(user.otp == otp) { "OTP invalide" }
            require(!(user.otpExpiresAt!!.isBefore(LocalDateTime.now()))) { "OTP expiré" }

            user.otp = null
            user.otpExpiresAt = null
            repo.save(user)
        }

        when (context) {
            OtpContext.REGISTRATION -> {
                user.enabled = true
                mailService.sendSuccessOTP(user.email, user.fullName)
            }
            OtpContext.PASSWORD_RESET -> {
                user.canResetPassword = true
                mailService.sendSuccessOTP(user.email, user.fullName, OtpContext.PASSWORD_RESET)
            }

            OtpContext.LOGIN_ATTEMPT_FAILED -> {
                user.otpValidated = true
                mailService.sendSuccessOTP(user.email, user.fullName, OtpContext.LOGIN_ATTEMPT_FAILED)
            }
        }

        return "Vérification OTP réussie"
    }

    override fun requestOtp(email: String, ip: String?, context: OtpContext): String {
        val user = repo.findByEmail(email)
            .orElseThrow { IllegalArgumentException("Utilisateur introuvable") }

        val otp = generateOtp()
        val expiresAt = LocalDateTime.now().plusMinutes(otpCodeProperties.expirationMin)

        if (ip != null) {
            val ipState = ipSecurityStateService.get(ip)
            ipState.otp = otp
            ipState.otpExpiresAt = expiresAt

            ipSecurityStateRepo.save(ipState)
        }
        else {
            user.otp = otp
            user.otpExpiresAt = expiresAt

            repo.save(user)
        }

        mailService.sendOtp(email, otp, context)

        return "Code OTP envoyer avec succès"
    }
}