package ml.espgm.gesem.services.impls

import ml.espgm.gesem.entities.IpSecurityState
import ml.espgm.gesem.enums.SecurityState
import ml.espgm.gesem.repositories.IpSecurityStateRepo
import ml.espgm.gesem.services.IpSecurityStateService
import org.springframework.stereotype.Service
import java.time.LocalDateTime

@Service
class IpSecurityStateImpl(
    private val repo: IpSecurityStateRepo
): IpSecurityStateService {
    override fun get(ip: String): IpSecurityState {
        return repo.findByIp(ip).orElseThrow { IllegalArgumentException("L'adresse IP est introuvable") }
    }

    override fun getOrCreate(ip: String): IpSecurityState {
        return repo.findByIp(ip).orElseGet {
            val newState = IpSecurityState(ip = ip)
            repo.save(newState)
        }
    }


    override fun markAsPermanentlyBlocked(ipState: IpSecurityState) {
        ipState.state = SecurityState.PERMANENTLY_BLOCKED
        ipState.blockedUntil = LocalDateTime.of(9999, 12, 31, 23, 59)
        repo.save(ipState)
    }

    override fun promoteToOtp(ipState: IpSecurityState) {
        ipState.state = SecurityState.REQUIRE_OTP
        repo.save(ipState)
    }

    override fun promoteToLimited(ipState: IpSecurityState) {
        ipState.state = SecurityState.LIMITED_ATTEMPTS
        ipState.updatedAt = LocalDateTime.now()
        repo.save(ipState)
    }

    override fun isPermanentlyBlocked(ipState: IpSecurityState): Boolean {
        return ipState.state == SecurityState.PERMANENTLY_BLOCKED
    }

    override fun isTemporarilyBlocked(ipState: IpSecurityState): Boolean {
        return ipState.state == SecurityState.TEMPORARY_LOCKED
    }
}