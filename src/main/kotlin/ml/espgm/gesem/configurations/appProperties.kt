package ml.espgm.gesem.configurations

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.stereotype.Component

@Component
@ConfigurationProperties(prefix = "app.otp-code")
class OtpCodeProperties {
    var expirationMin: Long = 30
}
