package ml.espgm.gesem.services

import ml.espgm.gesem.enums.OtpContext

interface MailService {
    fun sendOtp(email: String, otp: String, context: OtpContext = OtpContext.REGISTRATION)
    fun sendSuccessOTP(email: String, fullName: String, context: OtpContext = OtpContext.REGISTRATION)
    fun sendSuccessRegistryToApp(email: String)
    fun sendOtpForPasswordResetRequest(email: String, username: String)
    fun sendSuccessPasswordReset(email: String, fullName: String)
}