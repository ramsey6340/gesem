package ml.espgm.gesem.services

import ml.espgm.gesem.enums.OtpContext

interface OtpService {
    fun verifyOtp(email: String, otp: String, ip: String? = null, context: OtpContext = OtpContext.REGISTRATION): String
    fun requestOtp(email: String, ip: String? = null, context: OtpContext = OtpContext.REGISTRATION): String
}