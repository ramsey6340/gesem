package ml.espgm.gesem.exceptions

import ml.espgm.gesem.enums.SecurityState
import org.springframework.security.access.AccessDeniedException
import org.springframework.security.core.AuthenticationException

class LockedException(
    message: String,
    val state: SecurityState? = null
) : AuthenticationException(message)

class BadCredentialsException(
    message: String,
    val state: SecurityState? = null
) : AuthenticationException(message)

class AccessDeniedException(
    message: String,
    val state: SecurityState? = null
) : AccessDeniedException(message)

