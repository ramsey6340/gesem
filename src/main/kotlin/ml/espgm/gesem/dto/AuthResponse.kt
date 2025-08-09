package ml.espgm.gesem.dto

data class AuthResponse(
    val fullName: String,
    val username: String,
    val accessToken: String,
    val refreshToken: String,
    val accessTokenExpiry: Long,
    val refreshTokenExpiry: Long,
    val role: String
)
