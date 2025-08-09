package ml.espgm.gesem.services

import ml.espgm.gesem.dto.AdminDto
import ml.espgm.gesem.dto.AuthResponse
import ml.espgm.gesem.dto.RefreshTokenRequest

interface AuthService {
    fun createAdmin(admin: AdminDto): AdminDto
    fun login(username: String, password: String, ip: String): AuthResponse
    fun refreshToken(request: RefreshTokenRequest): AuthResponse
    fun passwordResetRequest(username: String): String
    fun resetPassword(username: String, newPassword: String): String
}