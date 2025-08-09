package ml.espgm.gesem.repositories

import ml.espgm.gesem.entities.IpSecurityState
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import java.util.Optional

@Repository
interface IpSecurityStateRepo: JpaRepository<IpSecurityState, Long> {
    fun findByIp(ip: String): Optional<IpSecurityState>
}