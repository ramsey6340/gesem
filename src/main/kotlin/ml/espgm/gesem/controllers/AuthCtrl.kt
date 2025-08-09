package ml.espgm.gesem.controllers

import com.fasterxml.jackson.annotation.JsonView
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.Parameter
import io.swagger.v3.oas.annotations.tags.Tag
import io.swagger.v3.oas.annotations.media.Content
import io.swagger.v3.oas.annotations.media.Schema
import io.swagger.v3.oas.annotations.responses.ApiResponse
import jakarta.servlet.http.HttpServletRequest
import ml.espgm.gesem.annotations.HandleException
import ml.espgm.gesem.dto.AdminDto
import ml.espgm.gesem.dto.LoginRequest
import ml.espgm.gesem.dto.OtpRequest
import ml.espgm.gesem.dto.RefreshTokenRequest
import ml.espgm.gesem.enums.OtpContext
import ml.espgm.gesem.services.AuthService
import ml.espgm.gesem.services.OtpService
import ml.espgm.gesem.views.CreationViews
import ml.espgm.gesem.views.OtpViews
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@Tag(name = "Authentification", description = "Endpoints pour la gestion de l'authentification et des comptes")
@RestController
@RequestMapping("/api/v1/auth")
class AuthCtrl(
    private val authService: AuthService,
    private val otpService: OtpService,
) {

    @HandleException
    @Operation(
        summary = "Créer un administrateur",
        description = "Permet de créer un compte administrateur.",
        responses = [
            ApiResponse(responseCode = "201", description = "Administrateur créé avec succès"),
            ApiResponse(responseCode = "400", description = "Requête invalide")
        ]
    )
    @PostMapping("/admin")
    @JsonView(CreationViews.Creation::class)
    fun createAdmin(
        @io.swagger.v3.oas.annotations.parameters.RequestBody(
            description = "Données de l'administrateur à créer",
            required = true,
            content = [Content(schema = Schema(implementation = AdminDto::class))]
        )
        @RequestBody @JsonView(CreationViews.Creation::class) adminDto: AdminDto
    ): Any {
        val createdAdmin = authService.createAdmin(adminDto)
        return ResponseEntity.status(HttpStatus.CREATED).body(createdAdmin)
    }

    @HandleException
    @Operation(
        summary = "Connexion utilisateur",
        description = "Authentifie un utilisateur avec son nom d'utilisateur et mot de passe.",
        responses = [
            ApiResponse(responseCode = "200", description = "Authentification réussie"),
            ApiResponse(responseCode = "401", description = "Nom d'utilisateur ou mot de passe incorrect"),
            ApiResponse(responseCode = "423", description = "Compte verrouillé ou IP bloquée")
        ]
    )
    @PostMapping("/login")
    fun login(
        @io.swagger.v3.oas.annotations.parameters.RequestBody(
            description = "Informations de connexion",
            required = true,
            content = [Content(schema = Schema(implementation = LoginRequest::class))]
        )
        @RequestBody loginRequest: LoginRequest,
        @Parameter(
            name = "X-Forwarded-For",
            description = "Adresse IP du client (si transmise via proxy ou gateway)",
            example = "192.168.1.10"
        )
        @RequestHeader("X-Forwarded-For", required = false) xForwardedFor: String?,
        request: HttpServletRequest
    ): Any {
        val ip = xForwardedFor?.split(",")?.first()?.trim() ?: request.remoteAddr
        val authResponse = authService.login(loginRequest.username, loginRequest.password, ip)
        return ResponseEntity.ok(authResponse)
    }

    @HandleException
    @Operation(
        summary = "Rafraîchir un token JWT",
        description = "Permet d'obtenir un nouveau token d'accès à partir d'un token de rafraîchissement valide.",
        responses = [
            ApiResponse(responseCode = "200", description = "Token rafraîchi avec succès"),
            ApiResponse(responseCode = "401", description = "Token de rafraîchissement invalide ou expiré")
        ]
    )
    @PostMapping("/refresh-token")
    fun refreshToken(
        @io.swagger.v3.oas.annotations.parameters.RequestBody(
            description = "Requête de rafraîchissement de token",
            required = true,
            content = [Content(schema = Schema(implementation = RefreshTokenRequest::class))]
        )
        @RequestBody request: RefreshTokenRequest
    ): Any {
        val authResponse = authService.refreshToken(request)
        return ResponseEntity.ok(authResponse)
    }

    @HandleException
    @Operation(
        summary = "Demande de réinitialisation de mot de passe",
        description = "Envoie un lien ou un code de réinitialisation de mot de passe à l'utilisateur.",
        responses = [
            ApiResponse(responseCode = "200", description = "Demande de réinitialisation envoyée"),
            ApiResponse(responseCode = "404", description = "Utilisateur non trouvé")
        ]
    )
    @PostMapping("/password-reset-request")
    fun passwordResetRequest(
        @RequestParam username: String
    ): Any {
        val result = authService.passwordResetRequest(username)
        return ResponseEntity.ok(result)
    }

    @HandleException
    @Operation(
        summary = "Réinitialiser le mot de passe",
        description = "Modifie le mot de passe d’un utilisateur avec un nouveau mot de passe.",
        responses = [
            ApiResponse(responseCode = "200", description = "Mot de passe réinitialisé avec succès"),
            ApiResponse(responseCode = "400", description = "Paramètres invalides")
        ]
    )
    @PostMapping("/reset-password")
    fun resetPassword(
        @RequestParam username: String,
        @RequestParam newPassword: String
    ): Any {
        val result = authService.resetPassword(username, newPassword)
        return ResponseEntity.ok(result)
    }

    @HandleException
    @Operation(summary = "Verification OTP")
    @PostMapping("/verify-otp")
    fun verifyOtp(@RequestParam context: OtpContext, @RequestBody req: OtpRequest): Any = otpService.verifyOtp(
        email = req.email,
        otp = req.otp?.takeIf { it.isNotBlank() } ?: throw IllegalArgumentException("OTP ne peut pas être null ou vide"),
        ip = req.ip,
        context = context
    )

    @HandleException
    @Operation(summary = "Demande de code OTP")
    @PostMapping("/request-otp")
    @JsonView(OtpViews.Request::class)
    fun requestOtp(@RequestParam context: OtpContext, @RequestBody @JsonView(OtpViews.Request::class) req: OtpRequest): Any = otpService.requestOtp(
        req.email,
        req.ip,
        context
    )
}
