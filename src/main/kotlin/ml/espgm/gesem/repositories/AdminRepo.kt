package ml.espgm.gesem.repositories

import ml.espgm.gesem.entities.Admin
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import java.util.Optional

@Repository
interface AdminRepo: JpaRepository<Admin, Long> {
    fun findByUsername(username: String): Optional<Admin>
    fun findByEmail(email: String): Optional<Admin>
}