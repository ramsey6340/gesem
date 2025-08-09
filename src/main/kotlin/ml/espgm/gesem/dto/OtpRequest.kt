package ml.espgm.gesem.dto

import com.fasterxml.jackson.annotation.JsonView
import ml.espgm.gesem.views.OtpViews

data class OtpRequest(
    val email: String,
    val ip: String? = null,

    @JsonView(OtpViews.Verify::class)
    val otp: String? = null
)