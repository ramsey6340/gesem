package ml.espgm.gesem.security

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.http.SessionCreationPolicy
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.security.web.SecurityFilterChain
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter
import org.springframework.security.config.annotation.web.invoke  // << important !

@Configuration
@EnableMethodSecurity
class SecurityConfig(
    private val jwtAuthFilter: JwtAuthFilter
) {
    @Bean
    fun passwordEncoder(): PasswordEncoder = BCryptPasswordEncoder()

    @Bean
    fun authenticationManager(config: AuthenticationConfiguration): AuthenticationManager =
        config.authenticationManager

    @Bean
    fun filterChain(http: HttpSecurity): SecurityFilterChain {
        http {
            csrf { disable() }

            sessionManagement {
                sessionCreationPolicy = SessionCreationPolicy.STATELESS
            }

            authorizeHttpRequests {
                // Autoriser Swagger et documentation publique
                authorize("/swagger-ui.html", permitAll)
                authorize("/swagger-ui/**", permitAll)
                authorize("/api-docs/**", permitAll)

                authorize(
                    { request ->
                        request.servletPath.startsWith("/api/v1/auth")
                                && request.servletPath != "/api/v1/auth/admin"
                    },
                    permitAll
                )

                authorize("/api/v1/auth/admin", hasRole("ADMIN"))
                authorize("/api/v1/auth/employes/**", hasRole("ADMIN"))

                authorize(anyRequest, authenticated)
            }

            //addFilterBefore<UsernamePasswordAuthenticationFilter>(jwtAuthFilter)
            http.addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter::class.java)
        }
        return http.build()
    }
}
