package ml.espgm.gesem.dto

import com.fasterxml.jackson.annotation.JsonView
import ml.espgm.gesem.enums.GesemRole
import ml.espgm.gesem.views.CreationViews
import java.time.LocalDateTime

data class AdminDto(

    @JsonView(CreationViews.Get::class)
    val id: Long = 0L,

    @JsonView(CreationViews.Creation::class, CreationViews.Get::class)
    val fullName: String,

    val username: String,

    @JsonView(CreationViews.Get::class, CreationViews.Creation::class)
    val email: String,

    @JsonView(CreationViews.Login::class, CreationViews.Creation::class)
    val password: String,

    @JsonView(CreationViews.Get::class)
    val role: GesemRole = GesemRole.ADMIN,

    @JsonView(CreationViews.Get::class)
    val createdAt: LocalDateTime? = null,

    @JsonView(CreationViews.Get::class)
    val updatedAt: LocalDateTime? = null,

    /*@JsonView(CreationViews.Login::class)
    val ip: String? = null*/
)
