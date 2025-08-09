package ml.espgm.gesem.services.impls

import ml.espgm.gesem.configurations.OtpCodeProperties
import ml.espgm.gesem.dto.AdminDto
import ml.espgm.gesem.dto.AuthResponse
import ml.espgm.gesem.dto.RefreshTokenRequest
import ml.espgm.gesem.entities.Admin
import ml.espgm.gesem.entities.IpSecurityState
import ml.espgm.gesem.enums.OtpContext
import ml.espgm.gesem.enums.SecurityState
import ml.espgm.gesem.exceptions.AccessDeniedException
import ml.espgm.gesem.exceptions.BadCredentialsException
import ml.espgm.gesem.exceptions.LockedException
import ml.espgm.gesem.helpers.*
import ml.espgm.gesem.repositories.AdminRepo
import ml.espgm.gesem.security.JwtUtils
import ml.espgm.gesem.services.AuthService
import ml.espgm.gesem.services.IpSecurityStateService
import ml.espgm.gesem.services.LoginAttemptTrackerService
import ml.espgm.gesem.services.MailService
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDateTime

@Service
class AuthImpl(
    private val htmlSanitizer: HtmlSanitizer,
    private val passwordEncoder: PasswordEncoder,
    private val repo: AdminRepo,
    private val jwtUtils: JwtUtils,
    private val tracker: LoginAttemptTrackerService,
    private val ipSecurityStateService: IpSecurityStateService,
    private val otpCodeProperties: OtpCodeProperties,
    private val mailService: MailService,
): AuthService {

    @Transactional
    override fun createAdmin(admin: AdminDto): AdminDto {
        val cleanFullName = admin.fullName.trim()
        val cleanUsername = admin.username.trim()
        val cleanPassword = admin.password.trim()
        val cleanEmail = admin.email.trim()

        require(cleanFullName.isNotBlank()) { "fullName ne peut pas être null ou vide" }
        require(cleanUsername.isNotBlank()) { "username ne peut pas être null ou vide" }
        require(cleanPassword.isNotBlank()) { "password ne peut pas être null ou vide" }
        require(cleanEmail.isNotBlank()) { "email ne peut pas être null ou vide" }

        // Validation du username
        require(DataValidatorRegex.isValidUsername(cleanUsername)) { "Username invalide" }

        require(DataValidatorRegex.isValidEmail(cleanEmail)) { "Username invalide" }

        // Vérifier si username déjà utilisé
        require(!repo.findByUsername(cleanUsername).isPresent) { "Username déjà utilisé" }

        // Création de l'entité Admin
        val entity = Admin(
            fullName = htmlSanitizer.sanitize(cleanFullName),
            username = htmlSanitizer.sanitize(cleanUsername),
            password = passwordEncoder.encode(cleanPassword),
            email = cleanEmail,
            role = admin.role
        )

        val saved = repo.save(entity)
        return saved.toDto()
    }


    @Transactional
    override fun login(username: String, password: String, ip: String): AuthResponse {
        val cleanUsername = htmlSanitizer.sanitize(
            username.trim().takeIf { it.isNotBlank() }
                ?: throw IllegalArgumentException("username ne peut pas être null ou vide")
        )
        val cleanPassword = password.trim().takeIf { it.isNotBlank() }
            ?: throw IllegalArgumentException("password ne peut pas être null ou vide")
        val cleanIp = ip.trim().takeIf { it.isNotBlank() }
            ?: throw IllegalArgumentException("L'adresse IP ne peut pas être null ou vide")

        require(DataValidatorRegex.isValidIp(cleanIp)) { "IP invalide" }

        val ipState = ipSecurityStateService.getOrCreate(cleanIp)
        checkIpBlockStatus(ipState)

        val user = findUserOrFail(cleanUsername, ipState)

        if (user.lockedUntil?.isAfter(LocalDateTime.now()) == true) {
            throw LockedException("Compte temporairement bloqué jusqu’à ${user.lockedUntil}.", state = SecurityState.TEMPORARY_LOCKED)
        }

        if (!passwordEncoder.matches(cleanPassword, user.password)) {
            handleFailure(user.username, ipState)
        }

        if (!user.enabled) {
            throw AccessDeniedException("Compte non activé", state = SecurityState.DISABLED)
        }

        tracker.resetFailures(user.username, cleanIp)

        // Génération des tokens sans appID
        val accessToken = jwtUtils.generateAccessToken(user)
        val refreshToken = jwtUtils.generateRefreshToken(user)

        return AuthResponse(
            fullName = user.fullName,
            username = user.username,
            accessToken = accessToken,
            refreshToken = refreshToken,
            accessTokenExpiry = jwtUtils.accessTokenExpirationMs,
            refreshTokenExpiry = jwtUtils.refreshTokenExpirationMs,
            role = user.role.name
        )
    }


    override fun refreshToken(request: RefreshTokenRequest): AuthResponse {
        val username = jwtUtils.parseUsername(request.refreshToken)
        val user = repo.findByUsername(username)
            .orElseThrow { IllegalArgumentException("Utilisateur introuvable") }

        val accessToken = jwtUtils.generateAccessToken(user)
        val refreshToken = jwtUtils.generateRefreshToken(user)

        return AuthResponse(
            fullName = user.fullName,
            username = user.username,
            accessToken = accessToken,
            refreshToken = refreshToken,
            accessTokenExpiry = jwtUtils.accessTokenExpirationMs,
            refreshTokenExpiry = jwtUtils.refreshTokenExpirationMs,
            role = user.role.name
        )
    }

    override fun passwordResetRequest(username: String): String {
        val user = repo.findByUsername(username)
            .orElseThrow { IllegalArgumentException("Utilisateur introuvable") }

        val otp = generateOtp()
        val expiresAt = LocalDateTime.now().plusMinutes(otpCodeProperties.expirationMin)

        user.otp = otp
        user.otpExpiresAt = expiresAt

        repo.save(user)
        mailService.sendOtp(user.email, otp, OtpContext.PASSWORD_RESET)
        return "Un code OTP a été envoyé à votre email: ${user.email}"
    }

    @Transactional
    override fun resetPassword(username: String, newPassword: String): String {
        val user = repo.findByUsername(username)
            .orElseThrow { IllegalArgumentException("Utilisateur introuvable") }

        require(user.canResetPassword) {
            "Vous devez d'abord valider la demande de réinitialisation avec le code OTP."
        }

        user.password = passwordEncoder.encode(newPassword)
        user.canResetPassword = false
        repo.save(user)

        mailService.sendSuccessPasswordReset(user.email, user.fullName)

        return "Mot de passe réinitialisé avec succès"
    }


    /* Private methods */
    /*private fun sanitizeRequest(request: AdminDto): AdminDto {
        val cleanIp = request.ip?.trim() ?: throw IllegalArgumentException("L'adresse IP ne peut pas être null ou vide")

        require(DataValidatorRegex.isValidIp(cleanIp)) { "IP invalide" }

        return request.copy(
            username = htmlSanitizer.sanitize(
                request.username.trim().takeIf { it.isNotBlank() }
                    ?: throw IllegalArgumentException("username ne peut pas être null ou vide")
            ),
            password = request.password.trim().takeIf { it.isNotBlank() }
                ?: throw IllegalArgumentException("password ne peut pas être null ou vide"),

            ip = cleanIp
        )
    }*/

    private fun checkIpBlockStatus(ipState: IpSecurityState) {
        when {
            (ipState.state == SecurityState.REQUIRE_OTP) -> {
                throw LockedException("Trop de tentative d'échec de connexion, une validation OTP est obligatoire, faite une demande pour recevoir un code OTP.", state = SecurityState.REQUIRE_OTP)
            }
            ((ipState.state == SecurityState.TEMPORARY_LOCKED) && (ipState.blockedUntil?.isAfter(LocalDateTime.now()) == true)) -> {
                throw LockedException("IP temporairement bloquée jusqu’à ${ipState.blockedUntil}.", state = SecurityState.TEMPORARY_LOCKED)
            }
            (ipState.state == SecurityState.PERMANENTLY_BLOCKED) -> {
                throw LockedException("Votre adresse IP a été définitivement bannie.", state = SecurityState.PERMANENTLY_BLOCKED)
            }
        }
    }

    private fun findUserOrFail(username: String, ipState: IpSecurityState): Admin {
        return repo.findByUsername(username).orElseThrow {
            handleFailure(username, ipState)
        }
    }

    private fun handleFailure(username: String, ipState: IpSecurityState): Nothing {
        if(ipState.state == SecurityState.LIMITED_ATTEMPTS && tracker.countFailures(username, ipState.ip) < MAX_FAILURE_COUNT) {
            tracker.initFailure(username, ipState.ip, MAX_FAILURE_COUNT)
        }

        tracker.recordFailure(username, ipState.ip)
        val failureCount = tracker.countFailures(username, ipState.ip)

        when {
            failureCount >= TOTAL_MAX_FAILURE_COUNT -> {
                ipSecurityStateService.markAsPermanentlyBlocked(ipState)
                throw LockedException("Votre adresse IP a été définitivement bannie.", state = SecurityState.PERMANENTLY_BLOCKED)
            }
            else -> throw BadCredentialsException("username ou mot de passe incorrecte", state = ipState.state)
        }
    }
}