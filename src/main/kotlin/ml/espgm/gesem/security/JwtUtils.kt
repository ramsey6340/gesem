package ml.espgm.gesem.security

import io.jsonwebtoken.Claims
import io.jsonwebtoken.Jwts
import io.jsonwebtoken.SignatureAlgorithm
import io.jsonwebtoken.security.Keys
import ml.espgm.gesem.entities.Admin
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component
import java.util.Base64
import java.util.Date
import javax.crypto.SecretKey

@Component
class JwtUtils(
    @Value("\${jwt.secret}") secret: String,
    @Value("\${jwt.access-token-expiration-ms}") val accessTokenExpirationMs: Long,
    @Value("\${jwt.refresh-token-expiration-ms}") val refreshTokenExpirationMs: Long
) {
    private val secretKey: SecretKey = Keys.hmacShaKeyFor(Base64.getDecoder().decode(secret))

    /**
     * Génère un access token pour l'utilisateur
     */
    fun generateAccessToken(user: Admin): String =
        createToken(user, accessTokenExpirationMs, includeRole = true)

    /**
     * Génère un refresh token pour l'utilisateur
     */
    fun generateRefreshToken(user: Admin): String =
        createToken(user, refreshTokenExpirationMs, includeRole = true)

    /**
     * Crée un JWT avec une durée donnée
     */
    private fun createToken(user: Admin, durationMs: Long, includeRole: Boolean): String {
        val now = Date()
        val builder = Jwts.builder()
            .setSubject(user.username)
            .setIssuedAt(now)
            .setExpiration(Date(now.time + durationMs))
            .signWith(secretKey, SignatureAlgorithm.HS512)

        if (includeRole) {
            builder.claim("role", user.role.name)
        }

        return builder.compact()
    }

    /**
     * Récupère le username utilisateur à partir d'un token (refresh ou access)
     */
    fun parseUsername(token: String): String {
        return parseClaims(token).subject
    }

    /**
     * Vérifie la validité d'un token
     */
    fun isTokenValid(token: String): Boolean {
        return try {
            parseClaims(token)
            true
        } catch (e: Exception) {
            false
        }
    }

    /**
     * Parse les claims
     */
    private fun parseClaims(token: String): Claims {
        return Jwts.parserBuilder()
            .setSigningKey(secretKey)
            .build()
            .parseClaimsJws(token)
            .body
    }
}
