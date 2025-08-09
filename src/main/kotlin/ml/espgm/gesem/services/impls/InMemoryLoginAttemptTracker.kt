package ml.espgm.gesem.services.impls

import ml.espgm.gesem.services.LoginAttemptTrackerService
import org.springframework.stereotype.Service

@Service
class InMemoryLoginAttemptTracker : LoginAttemptTrackerService {
    private val attempts = mutableMapOf<String, Int>()

    override fun recordFailure(username: String, ip: String) {
        val key = "$username-$ip"
        attempts[key] = attempts.getOrDefault(key, 0) + 1
    }

    override fun initFailure(username: String, ip: String, value: Int) {
        val key = "$username-$ip"
        attempts[key] = value
    }

    override fun resetFailures(username: String, ip: String) {
        val key = "$username-$ip"
        attempts.remove(key)
    }

    override fun countFailures(username: String, ip: String): Int {
        val key = "$username-$ip"
        return attempts.getOrDefault(key, 0)
    }
}