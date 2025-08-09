package ml.espgm.gesem.helpers

import org.springframework.http.ResponseEntity

data class ResponseWrapper(
        val error: String? = null,
        val data: Any? = null,
        val code: Int = 200,
        val state: String? = null
) {

    fun hasError(): Boolean = error != null

    companion object {

        private fun formatData(data: Any?): Any? {
            return when (data) {
                is String, is Int, is Double, is Long, is Float, is Boolean ->
                    mapOf("message" to data)
                else -> data
            }
        }

        fun of(data: Any?): ResponseWrapper {
            return ResponseWrapper(null, formatData(data))
        }

        fun success(data: Any?): ResponseWrapper {
            return ResponseWrapper(null, formatData(data))
        }

        fun error(error: String?): ResponseWrapper {
            return ResponseWrapper(error, null, 400)
        }

        fun error(error: String?, code: Int, state: String? = null): ResponseWrapper {
            return ResponseWrapper(error, null, code, state)
        }

        // Création d'une extension
        fun ResponseWrapper.toResponseEntity(): ResponseEntity<ResponseWrapper> {
            return ResponseEntity.status(this.code).body(this)
        }

        val SERVICE_UNAVAILABLE_ERROR: ResponseWrapper =
                error("Service temporairement indisponible. Merci de réessayer ultérieurement", 500)

        val INTERNAL_ERROR: ResponseWrapper =
                error("Erreur interne du service. Merci de réessayer ultérieurement", 500)
    }
}
