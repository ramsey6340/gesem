package ml.espgm.gesem.entities

import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import org.hibernate.annotations.CreationTimestamp
import org.hibernate.annotations.UpdateTimestamp
import java.time.LocalDateTime

@Entity
data class Employe(
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    val id: Long = 0L,

    val lastName: String,

    val firstName: String,

    val poste: String,

    val email: String,

    val hiringDate: LocalDateTime? = LocalDateTime.now(),

    var enabled: Boolean = false,

    @field:CreationTimestamp
    val createdAt: LocalDateTime? = null,

    @field:UpdateTimestamp
    val updatedAt: LocalDateTime? = null,
)
