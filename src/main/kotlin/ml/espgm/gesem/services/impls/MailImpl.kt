package ml.espgm.gesem.services.impls

import jakarta.mail.internet.MimeMessage
import ml.espgm.gesem.enums.OtpContext
import ml.espgm.gesem.helpers.DataValidatorRegex
import ml.espgm.gesem.services.MailService
import mu.KotlinLogging
import org.springframework.mail.javamail.JavaMailSender
import org.springframework.mail.javamail.MimeMessageHelper
import org.springframework.stereotype.Service

private val logger = KotlinLogging.logger {}

@Service
class MailImpl(
    private val mailSender: JavaMailSender
) : MailService {

    private fun sendHtmlMail(to: String, subject: String, htmlContent: String) {
        val cleanEmail = to.trim()
        logger.info("Envoi d'email à : '$cleanEmail' - Sujet : '$subject'")

        require(DataValidatorRegex.isValidEmail(cleanEmail)) {
            "Email invalide : $cleanEmail"
        }

        val mimeMessage: MimeMessage = mailSender.createMimeMessage()
        val helper = MimeMessageHelper(mimeMessage, true, "UTF-8")

        helper.setTo(cleanEmail)
        helper.setSubject(subject)
        helper.setText(htmlContent, true)

        mailSender.send(mimeMessage)
    }

    override fun sendOtp(email: String, otp: String, context: OtpContext) {
        val subject = when (context) {
            OtpContext.REGISTRATION   -> "Confirmez votre inscription (OTP) 🔐"
            OtpContext.PASSWORD_RESET -> "Réinitialisation du mot de passe (OTP) 🔐"
            OtpContext.LOGIN_ATTEMPT_FAILED -> "Tentative de connexion à votre compte (OTP) 🔐"
        }

        val bodyIntro = when (context) {
            OtpContext.REGISTRATION   -> "Merci pour votre inscription ! Veuillez utiliser le code ci-dessous pour activer votre compte."
            OtpContext.PASSWORD_RESET -> "Vous avez demandé la réinitialisation de votre mot de passe. Veuillez utiliser le code ci-dessous pour continuer."
            OtpContext.LOGIN_ATTEMPT_FAILED -> "Nous avons détecté une tentative de connexion à votre compte Kelenpe. Si c'est bien vous, veuillez utiliser le code ci-dessous pour continuer, sinon vous pouvez ignorer cet email."
        }

        val htmlContent = """
        <html>
        <body style="font-family: Arial, sans-serif; line-height: 1.6; color: #333;">
            <h2 style="color: #4CAF50;">Code de confirmation</h2>
            <p>Bonjour,</p>
            <p>$bodyIntro</p>
            <div style="font-size: 24px; font-weight: bold; color: #4CAF50; margin: 20px 0;">$otp</div>
            <p>Ce code est valable pendant quelques minutes. Ne le partagez pas avec quelqu’un d’autre.</p>
            <p>Merci,<br/>L’équipe Kelenpe.</p>
        </body>
        </html>
    """.trimIndent()

        sendHtmlMail(email.trim(), subject, htmlContent)
    }

    override fun sendSuccessOTP(email: String, fullName: String, context: OtpContext) {
        val subject = when (context) {
            OtpContext.REGISTRATION -> "🎉 Bienvenue dans l’univers Kelenpe !"
            OtpContext.PASSWORD_RESET -> "🔓 Mot de passe réinitialisé avec succès"
            OtpContext.LOGIN_ATTEMPT_FAILED -> "✅ Connexion validée avec succès"
        }

        val bodyContent = when (context) {
            OtpContext.REGISTRATION -> """
            <h2 style="color: #4CAF50;">Félicitations $fullName !</h2>
            <p>Votre compte <strong>Kelenpe</strong> a bien été créé.</p>
            <p>Vous pouvez maintenant accéder à toutes nos applications et services digitaux avec votre compte unique.</p>
            <p style="margin: 20px 0; font-size: 16px; color: #4CAF50;">
                Bienvenue dans notre univers numérique 🌍 !
            </p>
        """
            OtpContext.PASSWORD_RESET -> """
            <h2 style="color: #4CAF50;">Bonjour $fullName,</h2>
            <p>Votre mot de passe a été réinitialisé avec succès.</p>
            <p>Vous pouvez désormais vous connecter à votre compte Kelenpe avec votre nouveau mot de passe.</p>
            <p style="margin: 20px 0; font-size: 16px; color: #4CAF50;">
                Pensez à le garder confidentiel et sécurisé.
            </p>
        """
            OtpContext.LOGIN_ATTEMPT_FAILED -> """
            <h2 style="color: #4CAF50;">Bonjour $fullName,</h2>
            <p>Votre tentative de connexion a été validée.</p>
            <p>Si ce n’était pas vous, veuillez nous contacter immédiatement ou changer votre mot de passe.</p>
            <p style="margin: 20px 0; font-size: 16px; color: #4CAF50;">
                Sécurité avant tout 🔐.
            </p>
        """
        }

        val htmlContent = """
        <html>
        <body style="font-family: Arial, sans-serif; line-height: 1.6; color: #333;">
            $bodyContent
            <p>Merci de votre confiance,<br/>L’équipe Kelenpe.</p>
        </body>
        </html>
    """.trimIndent()

        sendHtmlMail(email.trim(), subject, htmlContent)
    }


    override fun sendSuccessRegistryToApp(email: String) {
        val subject = "Bienvenue ! Votre inscription est confirmée 🎉"
        val htmlContent = """
    <html>
    <body style="font-family: Arial, sans-serif; line-height: 1.6;">
        <h2>Bienvenue !</h2>
        <p>Bonjour,</p>
        <p>Félicitations ! Votre inscription est maintenant <strong>confirmée</strong>. 🎉</p>
        <p>Vous pouvez dès à présent vous connecter et commencer à profiter de toutes les fonctionnalités disponibles.</p>
        <p>
            👉 <a href="https://votre-site.com/login" style="background-color: #4CAF50; color: white; padding: 10px 20px; text-decoration: none; border-radius: 4px;">
            Se connecter
            </a>
        </p>
        <p>Merci de nous avoir rejoints !</p>
        <p>À très bientôt,<br/>L’équipe.</p>
    </body>
    </html>
    """.trimIndent()

        sendHtmlMail(email, subject, htmlContent)
    }


    override fun sendOtpForPasswordResetRequest(email: String, username: String) {
        val subject = "🔐 Réinitialisation de votre mot de passe"
        val htmlContent = """
        <html>
        <body style="font-family: Arial, sans-serif; line-height: 1.6;">
            <h2 style="color: #4CAF50;">Bonjour $username,</h2>
            <p>Vous avez demandé la réinitialisation de votre mot de passe.</p>
            <p>Veuillez utiliser le code OTP reçu pour finaliser la procédure de réinitialisation.</p>
            <p style="margin: 20px 0; font-size: 16px; color: #4CAF50;">Si vous n’êtes pas à l’origine de cette demande, veuillez ignorer ce message.</p>
            <p>Merci,<br/>L’équipe Suguu.</p>
        </body>
        </html>
        """.trimIndent()

        sendHtmlMail(email, subject, htmlContent)
    }

    override fun sendSuccessPasswordReset(email: String, fullName: String) {
        val subject = "✅ Mot de passe réinitialisé avec succès"
        val htmlContent = """
        <html>
        <body style="font-family: Arial, sans-serif; line-height: 1.6;">
            <h2 style="color: #4CAF50;">Bonjour $fullName,</h2>
            <p>Votre mot de passe a été <strong>réinitialisé avec succès</strong>.</p>
            <p>Vous pouvez maintenant vous connecter avec votre nouveau mot de passe.</p>
            <p>Merci,<br/>L’équipe Suguu.</p>
        </body>
        </html>
        """.trimIndent()

        sendHtmlMail(email, subject, htmlContent)
    }
}