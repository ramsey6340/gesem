package ml.espgm.gesem.helpers

const val MAX_FAILURE_COUNT = 5
const val TOTAL_MAX_FAILURE_COUNT = 7
const val MAX_FAILED_CAPTCHA_ATTEMPTS = 2 // le nombre maximum de validations dans un cycle CAPTCHA
const val NB_HOURS_WAIT_CAPTCHA_FAILURE: Long = 4 // nombre d'heures d'attente suite à des échecs consecutive de validation CAPTCHA
const val MAX_CAPTCHA_FAILURE_COUNT_CYCLE = 3 // le nombre maximum de cycle de validation CAPTCHA.