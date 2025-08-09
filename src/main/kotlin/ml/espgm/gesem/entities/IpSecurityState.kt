package ml.espgm.gesem.entities

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.EnumType
import jakarta.persistence.Enumerated
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.Table
import ml.espgm.gesem.enums.SecurityState
import org.hibernate.annotations.CreationTimestamp
import org.hibernate.annotations.UpdateTimestamp
import java.time.LocalDateTime

@Entity
@Table(name = "ip_security_state")
data class IpSecurityState(
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,

    @Column(unique = true)
    val ip: String, // adresse IP de la personne

    @Enumerated(EnumType.STRING)
    var state: SecurityState = SecurityState.NORMAL, // l'etat actuel de l'IP

    var failedCaptchaAttempts: Int = 0, // Le nombre de tentatives d'échec de CAPTCHA

    var lastCaptchaFailedAt: LocalDateTime? = null, // la data du dernier tentative

    var blockedUntil: LocalDateTime? = null, // bloquer l'ip jusqu'à une date precis

    var captchaFailureCountCycle: Int = 0, // Pour compter le nombre de cycles "2 essais échoués + 4h"

    var otp: String? = null,

    var otpExpiresAt: LocalDateTime? = null,

    @field:CreationTimestamp
    var createdAt: LocalDateTime? = null,

    @field:UpdateTimestamp
    var updatedAt: LocalDateTime? = null
)
