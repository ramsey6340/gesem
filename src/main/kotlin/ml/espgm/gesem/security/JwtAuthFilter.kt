package ml.espgm.gesem.security

import jakarta.servlet.FilterChain
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource
import org.springframework.stereotype.Component
import org.springframework.web.filter.OncePerRequestFilter
import io.jsonwebtoken.JwtException

@Component
class JwtAuthFilter(
        private val jwtUtils: JwtUtils,
        private val userDetailsService: CustomUserDetailsService
) : OncePerRequestFilter() {
    override fun doFilterInternal(
            request: HttpServletRequest,
            response: HttpServletResponse,
            filterChain: FilterChain
    ) {
        val header = request.getHeader("Authorization")
        if (header?.startsWith("Bearer ") == true) {
            val token = header.substring(7)
            try {
                val username = jwtUtils.parseUsername(token)
                val userDetails = userDetailsService.loadUserByUsername(username)
                val auth = UsernamePasswordAuthenticationToken(userDetails, null, userDetails.authorities)
                auth.details = WebAuthenticationDetailsSource().buildDetails(request)
                SecurityContextHolder.getContext().authentication = auth
            } catch (ex: JwtException) {
                // invalid token
            }
        }
        filterChain.doFilter(request, response)
    }
}