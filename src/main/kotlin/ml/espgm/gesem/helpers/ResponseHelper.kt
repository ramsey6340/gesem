package ml.espgm.gesem.helpers

import org.springframework.http.ResponseEntity


object ResponseHelper {
    fun error(message: String?): ResponseEntity<*> {
        val err: MutableMap<String?, Any?> = HashMap()
        err["message"] = message
        err["status"] = 400
        return ResponseEntity.status(400).body<MutableMap<String?, Any?>?>(err)
    }

    fun success(message: String?): ResponseEntity<*> {
        val r: MutableMap<String?, Any?> = HashMap()
        r["message"] = message
        return ResponseEntity.ok<MutableMap<String?, Any?>?>(r)
    }

    fun success(responseObj: Any?): ResponseEntity<*> {
        return ResponseEntity.ok<Any?>(responseObj)
    }

    fun error(message: String?, code: Int): ResponseEntity<*> {
        val err: MutableMap<String?, Any?> = HashMap()
        err.put("message", message)
        err.put("status", code)
        return ResponseEntity.status(code).body<MutableMap<String?, Any?>?>(err)
    }

    fun respondFromWrapper(wrapper: ResponseWrapper?): ResponseEntity<*> {
        if (wrapper == null) {
            return error("Internal error", 500)
        }
        if (wrapper.error != null) {
            //error occured
            return error(wrapper.error, wrapper.code)
        }
        return success(wrapper.data)
    }
}