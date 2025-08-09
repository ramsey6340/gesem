package ml.espgm.gesem.dto

import com.fasterxml.jackson.annotation.JsonView
import ml.espgm.gesem.views.CreationViews
import java.time.LocalDateTime

data class EmployeDto(

    @JsonView(CreationViews.Get::class)
    val id: Long = 0L,

    val lastName: String,

    val firstName: String,

    val poste: String,

    val email: String,

    val hiringDate: LocalDateTime?,

    @JsonView(CreationViews.Get::class)
    var enabled: Boolean = false,

    @JsonView(CreationViews.Get::class)
    val createdAt: LocalDateTime? = null,

    @JsonView(CreationViews.Get::class)
    val updatedAt: LocalDateTime? = null,
)
