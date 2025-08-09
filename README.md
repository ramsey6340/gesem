# Gesem

Application Spring Boot pour la gestion des employés et l'authentification.

---

## Configuration

---

## Compte administrateur par défaut

Au démarrage, si aucun compte administrateur n'existe dans la base, l'application crée automatiquement un compte admin par défaut.

Les informations de connexion (nom d'utilisateur et mot de passe) sont définies via les variables d’environnement suivantes, à placer dans votre fichier `.env` :

```env
GESEM_DEFAULT_USERNAME=theking
GESEM_DEFAULT_PASSWORD=nan@tsun0t@1sen
```

### Profils

- `dev` : profil de développement
- `prod` : profil de production

Le profil actif est défini dans `application.properties` via la propriété :

```properties
spring.profiles.active=dev

### Ports

L'application démarre par défaut sur le port **8082** (configurable dans `application.properties`) :

```properties
server.port=8082
```

---

## Configuration des sources de données

Les configurations des bases de données et autres paramètres sensibles sont gérées via variables d'environnement dans les fichiers `application-dev.properties` et `application-prod.properties`.

Exemples des propriétés utilisées (à définir dans l’environnement) :

* `ESPGM_SPRING_DATASOURCE_URL`
* `ESPGM_SPRING_DATASOURCE_USERNAME`
* `ESPGM_SPRING_DATASOURCE_PASSWORD`
* Variables pour la configuration SMTP mail (ex: `ESPGM_SPRING_MAIL_HOST`, etc.)
* Secrets JWT (`GESEM_AUTH_JWT_SECRET_DEV`, `GESEM_AUTH_JWT_SECRET_PROD`)

---

## Sécurité

* Gestion des tokens JWT avec configuration via propriétés `jwt.secret`, `jwt.access-token-expiration-ms` et `jwt.refresh-token-expiration-ms`.
* Système complet d’authentification avec gestion des comptes admins et employés.
* Intégration des règles de sécurité (ex : blocage IP, OTP).
* Les mots de passe sont encodés avec un `PasswordEncoder`.

---

## API REST

### Documentation Swagger / OpenAPI

* Accessible à l’URL :
  `http://localhost:8082/swagger-ui.html`

* Le fichier OpenAPI JSON est disponible sur :
  `http://localhost:8082/api-docs`

---

## Endpoints principaux

### Authentification (`/api/v1/auth`)

* **POST** `/admin` : Création administrateur
* **POST** `/login` : Connexion utilisateur
* **POST** `/refresh-token` : Rafraîchissement de token
* **POST** `/password-reset-request` : Demande de réinitialisation mot de passe
* **POST** `/reset-password` : Réinitialisation mot de passe
* **POST** `/request-otp` et `/verify-otp` : Gestion OTP

### Employés (`/api/v1/employes`)

* **POST** `/` : Création d’un employé
* **GET** `/` : Liste des employés
* **GET** `/{id}` : Détail employé
* **PUT** `/{id}` : Mise à jour employé
* **DELETE** `/{id}` : Suppression employé

---

## Gestion des erreurs

* Gestion centralisée via un aspect `ExceptionHandlingAspect`.
* Réponses encapsulées dans un `ResponseWrapper` avec code HTTP et message clair.
* Prise en charge des erreurs classiques : 400, 401, 403, 404, 409, 423, 500.

---

## Exécution

### Prérequis

* Java 21
* Maven 3.x
* PostgreSQL configuré avec les bonnes variables d’environnement
* Configuration SMTP valide (ex: Gmail App Password)

### Lancer l’application

```bash
mvn clean spring-boot:run -Dspring-boot.run.profiles=dev
```

---

## Remarques

* Utilisation de Jackson `@JsonView` pour la sérialisation contrôlée des DTO.
* Configuration JPA en `update` pour mise à jour automatique du schéma.
* Swagger activé via `springdoc.swagger-ui.enabled=true`.
* Expiration OTP configurable via `app.otp-code.expiration-min`.

---

## Contact

Pour toute question ou contribution : [drissasidiki7219@gmail.com](mailto:drissasidiki7219@gmail.com)

---
