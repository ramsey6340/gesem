package ml.espgm.gesem.exceptions

import com.fasterxml.jackson.annotation.JsonView
import com.fasterxml.jackson.databind.ObjectMapper
import ml.espgm.gesem.helpers.ResponseWrapper
import ml.espgm.gesem.helpers.ResponseWrapper.Companion.toResponseEntity
import mu.KotlinLogging
import org.aspectj.lang.ProceedingJoinPoint
import org.aspectj.lang.annotation.Around
import org.aspectj.lang.annotation.Aspect
import org.aspectj.lang.reflect.MethodSignature
import org.springframework.dao.DataIntegrityViolationException
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.http.converter.json.MappingJacksonValue
import org.springframework.stereotype.Component
import org.springframework.web.client.HttpClientErrorException

@Aspect
@Component
class ExceptionHandlingAspect(
    private val objectMapper: ObjectMapper
) {

    private val logger = KotlinLogging.logger {}

    @Around("@annotation(ml.espgm.gesem.annotations.HandleException)")
    fun handleServiceExceptions(joinPoint: ProceedingJoinPoint): Any {
        return try {
            val result = joinPoint.proceed()
            if (result is ResponseEntity<*>) return result

            // 1) récupérer la classe de vue depuis la méthode
            val method = (joinPoint.signature as MethodSignature).method
            val view = method.getAnnotation(JsonView::class.java)?.value?.firstOrNull()

            // 2) envelopper dans le wrapper
            val wrapper = ResponseWrapper.success(result)

            if (view != null) {
                val mjv = MappingJacksonValue(wrapper)
                mjv.serializationView = view.java
                return ResponseEntity.status(wrapper.code).body(mjv)
            }

            wrapper.toResponseEntity()
        } catch (ex: HttpClientErrorException) {
            handleHttpClientErrorException(ex)

        } catch (ex: Exception) {
            handleGenericException(ex)
        }
    }

    private fun handleHttpClientErrorException(ex: HttpClientErrorException): ResponseEntity<Any> {
        return try {
            val wrapper = objectMapper.readValue(ex.responseBodyAsString, ResponseWrapper::class.java)
            ResponseEntity.status(wrapper.code)
                .contentType(MediaType.APPLICATION_JSON)
                .body(wrapper as Any)  // ok de caster ici, c’est le corps
        } catch (e: Exception) {
            logger.error(e) { "Erreur parsing ResponseWrapper depuis HttpClientErrorException" }
            ResponseEntity.status(ex.statusCode)
                .contentType(MediaType.APPLICATION_JSON)
                .body(mapOf("error" to "Erreur serveur: ${ex.message}", "code" to ex.statusCode.value()) as Any)
        }
    }

    private fun handleGenericException(ex: Exception): ResponseEntity<Any> {
        // Fonction locale pour convertir ResponseWrapper en ResponseEntity<Any>
        fun toEntity(wrapper: ResponseWrapper): ResponseEntity<Any> =
            ResponseEntity.status(wrapper.code)
                .contentType(MediaType.APPLICATION_JSON)
                .body(wrapper as Any)

        // Tentative de désérialisation du message d'exception en ResponseWrapper
        val maybeWrapper = try {
            ex.message?.let { objectMapper.readValue(it, ResponseWrapper::class.java) }
        } catch (e: Exception) {
            null
        }

        val wrapper = when {
            maybeWrapper != null -> maybeWrapper

            ex is NoSuchElementException ->
                ResponseWrapper.error("Not found: ${ex.message}", HttpStatus.NOT_FOUND.value())

            ex is IllegalArgumentException ->
                ResponseWrapper.error("Bad request: ${ex.message}", HttpStatus.BAD_REQUEST.value())

            ex is AccessDeniedException ->
                ResponseWrapper.error("Accès refusé: ${ex.message}", HttpStatus.FORBIDDEN.value(), state = ex.state?.name)

            ex is DataIntegrityViolationException ->
                ResponseWrapper.error("Conflit de données: ${ex.message}", HttpStatus.CONFLICT.value())

            ex is BadCredentialsException ->
                ResponseWrapper.error("Mauvais identifiants: ${ex.message}", HttpStatus.BAD_REQUEST.value(), state = ex.state?.name)

            ex is IllegalStateException ->
                ResponseWrapper.error("État invalide : ${ex.message}", 422)

            ex is LockedException ->
                ResponseWrapper.error("L'accès au ressource est bloqué : ${ex.message}", 423, state = ex.state?.name)

            else -> {
                logger.error(ex) { "Erreur non capturée" }
                ResponseWrapper.error("Erreur serveur: ${ex.message}", 500)
            }
        }

        return toEntity(wrapper)
    }
}