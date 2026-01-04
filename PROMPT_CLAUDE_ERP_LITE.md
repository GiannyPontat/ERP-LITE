# PROMPT POUR CURSOR / CLAUDE CODE
## Génération de l'application ERP-LITE

---

## 🎯 PROMPT À COPIER-COLLER DANS CURSOR COMPOSER (Ctrl+I)

```
Tu es un architecte logiciel senior spécialisé en Angular + Spring Boot.

Je veux créer une application web de gestion pour professionnels du bâtiment, inspirée de l'application Costructor.

CONTEXTE :
- Nom : ERP-LITE
- Secteur : BTP / Construction
- Utilisateurs cibles : Artisans, TPE, PME du bâtiment
- Stack imposée : Angular 17+ (frontend) + Spring Boot 3.x (backend) + PostgreSQL

ARCHITECTURE GLOBALE :

1. BACKEND (Spring Boot)
   - Structure : Architecture en couches (Controller → Service → Repository)
   - Sécurité : Spring Security avec JWT
   - Base de données : PostgreSQL avec Flyway pour migrations
   - API REST : Documentation OpenAPI/Swagger
   - Gestion des erreurs : GlobalExceptionHandler
   - Validation : Bean Validation avec annotations

2. FRONTEND (Angular)
   - Structure : Architecture modulaire (core, shared, features)
   - UI Framework : Angular Material ou PrimeNG
   - State Management : RxJS + Services
   - Routing : Lazy loading des modules
   - Guards : Protection des routes selon rôles

FONCTIONNALITÉS PRIORITAIRES (MVP - Phase 1) :

MODULE 1 : AUTHENTIFICATION ET UTILISATEURS
Backend :
- Entity User avec champs : id, email, password (hashé), firstName, lastName, role (enum), company, createdAt, updatedAt
- Enum Role : ADMIN, MANAGER, EMPLOYEE
- AuthController : /api/auth/register, /api/auth/login, /api/auth/me
- JWT token avec durée de vie configurable
- UserController : CRUD utilisateurs (Admin only)

Frontend :
- Module Auth avec : LoginComponent, RegisterComponent
- AuthService : gestion token, vérification rôle
- AuthGuard : protection des routes
- RoleGuard : vérification rôles spécifiques
- Interceptor : ajout automatique du token JWT

MODULE 2 : GESTION DES CLIENTS
Backend :
- Entity Client : id, companyName, siret, contactFirstName, contactLastName, email, phone, address, city, postalCode, paymentTerms, createdAt, updatedAt, user (relation ManyToOne)
- ClientController : CRUD complet avec pagination et recherche
- Validation : SIRET format, email valide, téléphone français

Frontend :
- ClientListComponent : table avec pagination, tri, recherche
- ClientFormComponent : formulaire de création/édition
- ClientDetailComponent : fiche client complète avec historique
- ClientService : appels API

MODULE 3 : DEVIS ET FACTURES
Backend :
- Entity Quote (Devis) :
  * id, quoteNumber (auto-généré), client (ManyToOne), date, validUntil
  * status (enum: DRAFT, SENT, ACCEPTED, REJECTED)
  * items (OneToMany vers QuoteItem)
  * subtotal, taxRate, taxAmount, total
  * notes, termsAndConditions
  
- Entity QuoteItem :
  * id, description, quantity, unitPrice, total
  * quote (ManyToOne)

- Entity Invoice (Facture) - structure similaire à Quote :
  * Champs supplémentaires : invoiceNumber, dueDate, paidDate
  * status (DRAFT, SENT, PAID, OVERDUE, CANCELLED)
  * relation avec Quote (si conversion)

- QuoteController : CRUD + /convert-to-invoice + /send-email + /generate-pdf
- InvoiceController : CRUD + /send-reminder + /mark-as-paid

Frontend :
- QuoteListComponent / InvoiceListComponent
- QuoteFormComponent / InvoiceFormComponent (avec gestion des items dynamiques)
- QuoteDetailComponent / InvoiceDetailComponent
- Composant partagé : ItemsTableComponent (pour les lignes de devis/factures)
- Services dédiés

MODULE 4 : TABLEAU DE BORD
Backend :
- DashboardController :
  * GET /api/dashboard/stats : CA, bénéfices, factures impayées, devis en cours
  * GET /api/dashboard/monthly-revenue : graphique évolution CA
  * GET /api/dashboard/top-clients : top 10 clients
  
Frontend :
- DashboardComponent avec cartes statistiques
- Graphiques avec ng2-charts ou ngx-charts
- Filtres par période

RÈGLES DE DÉVELOPPEMENT :

Backend :
1. Toujours utiliser @Valid pour la validation
2. DTOs pour les requêtes/réponses (éviter d'exposer les entités directement)
3. Mapper les entités ↔ DTOs avec ModelMapper
4. Gestion centralisée des exceptions
5. Logs avec SLF4J
6. Tests unitaires avec JUnit 5 + Mockito
7. Documentation API avec Swagger

Frontend :
1. Un module par feature (Auth, Client, Quote, Invoice, Dashboard)
2. Services injectables pour les appels API
3. Reactive Forms pour tous les formulaires
4. Gestion des erreurs avec toaster/snackbar
5. Loading indicators pendant les requêtes
6. Confirmation avant suppression
7. Validation côté client + serveur

SÉCURITÉ :
- CORS configuré correctement
- CSRF protection
- Mots de passe hashés avec BCrypt
- SQL injection prevention (JPA)
- XSS protection (Angular par défaut)
- Rate limiting sur les endpoints sensibles

STRUCTURE DES FICHIERS À GÉNÉRER :

Backend (src/main/java/com/erplite) :
/config
  - SecurityConfig.java
  - WebConfig.java
  - SwaggerConfig.java
/controller
  - AuthController.java
  - UserController.java
  - ClientController.java
  - QuoteController.java
  - InvoiceController.java
  - DashboardController.java
/service
  - AuthService.java
  - UserService.java
  - ClientService.java
  - QuoteService.java
  - InvoiceService.java
  - DashboardService.java
  - EmailService.java
  - PdfService.java
/repository
  - UserRepository.java
  - ClientRepository.java
  - QuoteRepository.java
  - InvoiceRepository.java
/entity
  - User.java
  - Client.java
  - Quote.java
  - QuoteItem.java
  - Invoice.java
  - InvoiceItem.java
/dto
  - request/
  - response/
/security
  - JwtTokenProvider.java
  - JwtAuthenticationFilter.java
  - CustomUserDetailsService.java
/exception
  - GlobalExceptionHandler.java
  - ResourceNotFoundException.java
/enums
  - Role.java
  - QuoteStatus.java
  - InvoiceStatus.java

Frontend (src/app) :
/core
  - /guards (auth.guard.ts, role.guard.ts)
  - /interceptors (jwt.interceptor.ts, error.interceptor.ts)
  - /services (auth.service.ts)
  - /models (user.model.ts)
/shared
  - /components (header, sidebar, loading-spinner, etc.)
  - /pipes
  - /directives (has-role.directive.ts)
/features
  - /auth (login, register)
  - /dashboard
  - /clients (list, form, detail)
  - /quotes (list, form, detail)
  - /invoices (list, form, detail)
  - /users (list, form)

TÂCHES À EFFECTUER :

PHASE 1 - SETUP :
1. Génère le squelette du projet backend Spring Boot avec dépendances :
   - Spring Web, Spring Security, Spring Data JPA
   - PostgreSQL Driver, Flyway, Lombok
   - JWT (io.jsonwebtoken), ModelMapper
   - Swagger (springdoc-openapi)
   
2. Génère le squelette du projet frontend Angular avec :
   - Angular Material ou PrimeNG
   - HttpClient, Forms (Reactive), Router
   - ng2-charts ou ngx-charts pour graphiques

3. Configure application.properties / application.yml
4. Configure environment.ts (Angular)

PHASE 2 - BACKEND :
5. Crée toutes les entités avec relations JPA
6. Crée les repositories
7. Crée les services avec logique métier
8. Crée les controllers avec endpoints REST
9. Configure Spring Security + JWT
10. Crée les migrations Flyway initiales

PHASE 3 - FRONTEND :
11. Génère la structure des modules
12. Crée les composants de base (login, dashboard, etc.)
13. Crée les services Angular
14. Configure le routing avec guards
15. Implémente les formulaires réactifs
16. Ajoute la gestion d'erreurs

PHASE 4 - INTÉGRATION :
17. Connecte frontend ↔ backend
18. Teste les flux complets
19. Ajoute la validation des données
20. Gère les cas d'erreur

PRIORITÉS :
1. Authentification fonctionnelle
2. CRUD Clients
3. CRUD Devis
4. CRUD Factures
5. Dashboard basique

INSTRUCTIONS SUPPLÉMENTAIRES :
- Code commenté en français pour les parties métier
- Noms de variables/méthodes en anglais (convention)
- Respect des bonnes pratiques REST
- Pagination sur toutes les listes
- Recherche/filtrage côté serveur
- UI responsive (mobile-friendly)

COMMENCE PAR :
1. Créer la structure complète des deux projets
2. Implémenter l'authentification JWT de bout en bout
3. Puis module par module dans l'ordre de priorité

GÉNÈRE LE CODE MAINTENANT.
```

---

## 🚀 INSTRUCTIONS D'UTILISATION

### Option 1 : Dans Cursor Composer (Recommandé)

1. **Ouvrir Cursor**
2. **Ouvrir votre dossier de projet ERP-LITE**
3. **Appuyer sur `Ctrl+I`** (Composer)
4. **Coller le prompt ci-dessus**
5. **Laisser Claude travailler** - il va générer tous les fichiers

### Option 2 : Avec Claude Code (Terminal)

```bash
# Dans le terminal de Cursor ou votre terminal
npx @anthropic-ai/claude-code "
[Coller le même prompt ici]
"
```

---

## 📋 PROMPT ALTERNATIF (PLUS GUIDÉ - SI VOUS VOULEZ PLUS DE CONTRÔLE)

Si le prompt ci-dessus génère trop de code d'un coup, utilisez cette version **étape par étape** :

### ÉTAPE 1 : Structure Backend
```
Crée la structure complète d'un projet Spring Boot 3.x pour une application ERP.

Nom : erplite-backend
Package de base : com.erplite

Génère :
1. pom.xml avec toutes les dépendances nécessaires :
   - Spring Web, Spring Security, Spring Data JPA
   - PostgreSQL, Flyway, Lombok
   - JWT (io.jsonwebtoken:jjwt-api:0.12.3)
   - ModelMapper, Swagger
   
2. application.yml avec configuration PostgreSQL :
   - DB : erplite
   - Port : 8080
   - JWT secret (généré aléatoirement)
   
3. Structure des packages vides :
   - controller, service, repository, entity, dto, config, security, exception, enums

4. SecurityConfig.java basique avec JWT
5. SwaggerConfig.java
6. GlobalExceptionHandler.java

Ne génère PAS encore les entités ni les controllers, juste la structure.
```

### ÉTAPE 2 : Entités Backend
```
Génère toutes les entités JPA pour ERP-LITE (en fonction aussi de se qiu est deja la) :

1. User.java
   - id (UUID), email (unique), password, firstName, lastName
   - role (enum Role), company, active, createdAt, updatedAt
   
2. Client.java
   - id, companyName, siret, contactFirstName, contactLastName
   - email, phone, address, city, postalCode
   - paymentTerms (en jours), notes
   - relation ManyToOne avec User
   
3. Quote.java (Devis)
   - id, quoteNumber (String auto-généré "DEV-2026-0001")
   - client (ManyToOne), createdBy (User)
   - date, validUntil, status (enum QuoteStatus)
   - subtotal, taxRate, taxAmount, total
   - notes, termsAndConditions
   
4. QuoteItem.java
   - id, quote (ManyToOne), description, quantity, unitPrice, total
   
5. Invoice.java (même structure que Quote)
   - invoiceNumber ("FACT-2026-0001")
   - dueDate, paidDate
   - quote (si conversion)
   
6. InvoiceItem.java

Avec tous les enums (Role, QuoteStatus, InvoiceStatus).
Ajoute les annotations Lombok (@Data, @Entity, etc).
```

### ÉTAPE 3 : Authentication JWT
```
Implémente l'authentification JWT complète :

1. JwtTokenProvider.java
   - generateToken(UserDetails)
   - validateToken(String)
   - getUsernameFromToken(String)
   - Secret depuis application.yml
   - Durée token : 24h
   
2. JwtAuthenticationFilter.java
   - Filtre pour vérifier le token dans chaque requête
   
3. CustomUserDetailsService.java
   - Charge User depuis la DB
   
4. AuthService.java
   - login(email, password) → retourne token
   - register(RegisterDto) → crée user + retourne token
   
5. AuthController.java
   - POST /api/auth/login
   - POST /api/auth/register
   - GET /api/auth/me
   
6. DTOs : LoginDto, RegisterDto, AuthResponseDto

Configure SecurityConfig pour autoriser /api/auth/** sans token.
```

### ÉTAPE 4 : Module Clients (Backend)
```
Implémente le module de gestion des clients :

1. ClientRepository.java
   - Recherche par nom, siret, email
   - Pagination Spring Data
   
2. ClientService.java
   - CRUD complet
   - Vérification SIRET unique
   - Vérification email valide
   
3. ClientController.java
   - GET /api/clients (avec pagination + recherche)
   - GET /api/clients/{id}
   - POST /api/clients
   - PUT /api/clients/{id}
   - DELETE /api/clients/{id}
   - Accessible : ADMIN + MANAGER
   
4. DTOs : ClientDto, CreateClientDto, UpdateClientDto

Ajoute validation avec @Valid.
```

### ÉTAPE 5 : Structure Frontend Angular
```
Crée la structure Angular 17+ pour ERP-LITE :

Nom : erplite-frontend

1. Génère le projet :
   ng new erplite-frontend --routing --style=scss --standalone=false

2. Installe dépendances :
   - Angular Material
   - @angular/forms
   - ngx-charts
   
3. Structure des dossiers :
   src/app/
   ├── core/
   │   ├── guards/
   │   ├── interceptors/
   │   ├── services/
   │   └── models/
   ├── shared/
   │   ├── components/
   │   └── directives/
   └── features/
       ├── auth/
       ├── dashboard/
       ├── clients/
       ├── quotes/
       └── invoices/

4. Configure environment.ts avec API_URL

5. Génère AppComponent avec Material Toolbar + Sidenav

6. Mets en place le stabdalone et le lazy loading
```

### ÉTAPE 6 : Module Auth (Frontend)
```
Crée le module d'authentification Angular :

1. AuthService
   - login(email, password)
   - register(data)
   - logout()
   - getToken() / isAuthenticated()
   - currentUser$ (Observable)
   
2. LoginComponent
   - Reactive Form avec validation
   - Appel AuthService.login()
   - Redirect vers /dashboard après login
   
3. AuthGuard
   - Vérifie si token valide
   - Redirect vers /login si non authentifié
   
4. JwtInterceptor
   - Ajoute header Authorization: Bearer {token}
   
5. ErrorInterceptor
   - Gère les erreurs HTTP
   - Affiche snackbar Material

6. Routes :
   /login → LoginComponent
   /register → RegisterComponent (optionnel pour MVP)
```

Continuez ainsi module par module...

---

## 💡 CONSEILS POUR UTILISER LE PROMPT

### ✅ BONNES PRATIQUES

1. **Commencez par le prompt complet** dans Cursor Composer
   - Laissez Claude générer tout d'un coup
   - Examinez le résultat
   - Affinez ensuite

2. **Si trop complexe**, utilisez les **prompts étape par étape**
   - Plus de contrôle
   - Validation à chaque étape
   - Correction facile des erreurs

3. **Combinez les deux approches**
   - Prompt complet pour avoir la vision d'ensemble
   - Puis prompts ciblés pour les parties complexes

### ⚠️ ERREURS À ÉVITER

1. ❌ Ne demandez pas "fais-moi une app comme Costructor" sans détails
   → ✅ Utilisez le prompt structuré ci-dessus

2. ❌ Ne lancez pas la génération sans avoir lu le cahier des charges
   → ✅ Comprenez ce que vous voulez avant de générer

3. ❌ N'acceptez pas le code sans le tester
   → ✅ Testez au fur et à mesure

---

## 🎓 APRÈS LA GÉNÉRATION

### Vérification du code généré

```bash
# Backend
cd erplite-backend
./mvnw clean install
./mvnw spring-boot:run

# Frontend
cd erplite-frontend
npm install
ng serve
```

### Prochaines étapes

1. Tester l'authentification
2. Créer un client de test
3. Créer un devis
4. Vérifier le dashboard

---

**BONNE CHANCE ! 🚀**
