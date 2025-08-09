package ml.espgm.gesem.security

object SecurityWhitelist {
    val urls = arrayOf(
        "/api/v1/auth/**",
        "/swagger-ui.html",
        "/swagger-ui/**",
        "/api-docs/**"
    )
}