package ml.espgm.gesem.entities

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import ml.espgm.gesem.enums.GesemRole
import org.hibernate.annotations.CreationTimestamp
import org.hibernate.annotations.UpdateTimestamp
import java.time.LocalDateTime

@Entity
data class Admin(
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    val id: Long = 0L,

    val fullName: String,

    val username: String,

    var password: String,

    @Column(unique = true)
    val email: String,

    var enabled: Boolean = false,

    var canResetPassword: Boolean = false,

    var otpExpiresAt: LocalDateTime? = null,

    var otp: String? = null,

    var otpValidated: Boolean? = false,

    var lockedUntil: LocalDateTime? = null,

    val role: GesemRole = GesemRole.ADMIN,

    @field:CreationTimestamp
    val createdAt: LocalDateTime? = null,

    @field:UpdateTimestamp
    val updatedAt: LocalDateTime? = null,
)
