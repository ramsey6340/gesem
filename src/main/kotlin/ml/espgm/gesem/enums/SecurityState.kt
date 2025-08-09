package ml.espgm.gesem.enums

enum class SecurityState {
    NORMAL,
    DISABLED,
    REQUIRE_OTP,
    LIMITED_ATTEMPTS,
    TEMPORARY_LOCKED,
    PERMANENTLY_BLOCKED,
    FORCE_PASSWORD_RESET
}