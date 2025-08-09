package ml.espgm.gesem.helpers

import mu.KotlinLogging
import org.owasp.html.PolicyFactory
import org.owasp.html.Sanitizers
import org.springframework.stereotype.Component
import java.net.URI

private val logger = KotlinLogging.logger {  }

@Component
class HtmlSanitizer {
    private val policy: PolicyFactory = Sanitizers.FORMATTING
            .and(Sanitizers.BLOCKS)
            .and(Sanitizers.LINKS)
    // .and(Sanitizers.STYLES) // Optionnel
    // .and(Sanitizers.IMAGES) // Optionnel si tu veux garder <img>

    fun sanitize(input: String?): String {
        return policy.sanitize(input)
    }

    fun sanitizeUrl(url: String?, allowedPrefixes: List<String>): String {
        if (url.isNullOrBlank()) {
            throw IllegalArgumentException("L'URL ne peut pas être vide ou nulle.")
        }

        val uri = try {
            URI(url)
        } catch (e: Exception) {
            throw IllegalArgumentException("URL mal formée : $url, elle doit avoir la forme suivante: https://kelenpe.com/files/image", e)
        }

        if (uri.scheme != "https") {
            throw IllegalArgumentException("Seules les URLs en HTTPS sont autorisées (https://kelenpe.com/files/image). URL reçue : $url")
        }

        val isAllowedPrefix = allowedPrefixes.any { prefix -> url.startsWith(prefix) }
        if (!isAllowedPrefix) {
            throw SecurityException("L'URL ne correspond à aucun préfixe autorisé (https://kelenpe.com/files/image). URL reçue : $url")
        }

        return url
    }

}