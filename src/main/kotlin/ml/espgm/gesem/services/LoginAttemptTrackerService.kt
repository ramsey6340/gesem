package ml.espgm.gesem.services

interface LoginAttemptTrackerService {

    /**
     * Enregistre une tentative de connexion échouée pour un nom d'utilisateur donné,
     * ainsi que l'adresse IP d'origine.
     *
     * @param username le nom d'utilisateur tenté
     * @param ip l'adresse IP de la requête
     */
    fun recordFailure(username: String, ip: String)

    fun initFailure(username: String, ip: String, value: Int)

    /**
     * Réinitialise le compteur de tentatives échouées pour un utilisateur.
     * Appelé généralement après une connexion réussie ou après vérification d'une étape de sécurité (OTP, etc.)
     *
     * @param username le nom d'utilisateur concerné
     */
    fun resetFailures(username: String, ip: String)

    /**
     * Renvoie le nombre total de tentatives échouées associées à ce nom d'utilisateur.
     * Utile pour déterminer quand déclencher une étape de sécurité (CAPTCHA, OTP, blocage, etc.)
     *
     * @param username le nom d'utilisateur concerné
     * @return le nombre de tentatives échouées
     */
    fun countFailures(username: String, ip: String): Int
}