package ml.espgm.gesem.security

import ml.espgm.gesem.repositories.AdminRepo
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.security.core.userdetails.UsernameNotFoundException
import org.springframework.stereotype.Service

@Service
class CustomUserDetailsService(
        private val userRepo: AdminRepo
) : UserDetailsService {
    override fun loadUserByUsername(username: String): UserDetails {
        val admin = userRepo.findByUsername(username)
                .orElseThrow { UsernameNotFoundException("Admin not found: $username") }
        return org.springframework.security.core.userdetails.User(
                admin.username, admin.password,
                listOf(SimpleGrantedAuthority("ROLE_${admin.role.name}"))
        )
    }
}