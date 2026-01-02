# Module de Gestion des Clients - Implémentation Complète

## ✅ Toutes les fonctionnalités implémentées

### 1. ✅ ClientRepository amélioré
**Fichier:** `repositories/ClientRepo.java`

**Nouvelles fonctionnalités:**
- **Pagination Spring Data:** Utilisation de `Pageable` pour la pagination
- **Recherche par nom:** Recherche dans `contactFirstName`, `contactLastName`, `nom`, `companyName`
- **Recherche par SIRET:** `findBySiret(String siret)`
- **Recherche par email:** `findByEmail(String email)`
- **Vérification d'unicité:** Méthodes pour vérifier SIRET/email unique (avec exclusion d'un ID)
- **Requêtes personnalisées:** Utilisation de `@Query` pour des recherches avancées

**Méthodes disponibles:**
```java
Page<Client> findBySearchTerm(String search, Pageable pageable)
Optional<Client> findBySiret(String siret)
Optional<Client> findByEmail(String email)
boolean existsBySiretExcludingId(String siret, Long excludeId)
boolean existsByEmailExcludingId(String email, Long excludeId)
```

---

### 2. ✅ DTOs créés
**Fichiers:**
- `dtos/ClientDto.java` - DTO complet avec tous les champs
- `dtos/CreateClientDto.java` - DTO pour la création
- `dtos/UpdateClientDto.java` - DTO pour la mise à jour

**ClientDto:** Contient tous les champs:
- Champs nouveaux: companyName, siret, contactFirstName, contactLastName, email, phone, address, city, postalCode, paymentTerms, notes, userId
- Champs de compatibilité: nom, entreprise, telephone, adresse

**CreateClientDto:** Validations pour la création
- `@NotBlank` pour companyName
- `@Pattern` pour SIRET (14 chiffres exactement)
- `@Email` pour email
- `@Pattern` pour phone
- `@Size` pour tous les champs texte

**UpdateClientDto:** Validations optionnelles pour la mise à jour
- Tous les champs sont optionnels (pas de `@NotBlank`)
- Mêmes validations de format que CreateClientDto

---

### 3. ✅ ClientService amélioré
**Fichier:** `services/impl/ClientServiceImpl.java`

**Fonctionnalités:**
- **CRUD complet:**
  - `findAll(Pageable)` - Liste paginée
  - `search(String, Pageable)` - Recherche avec pagination
  - `findById(Long)` - Trouve un client
  - `create(CreateClientDto)` - Crée un client avec validations
  - `update(Long, UpdateClientDto)` - Met à jour un client avec validations
  - `delete(Long)` - Supprime un client

- **Validations métier:**
  - ✅ **Vérification SIRET unique:** Vérifie que le SIRET n'existe pas déjà
  - ✅ **Vérification email valide:** Valide le format email avec regex
  - ✅ **Vérification email unique:** Vérifie que l'email n'existe pas déjà
  - ✅ **Gestion des conflits:** Retourne HTTP 409 (CONFLICT) si SIRET/email existe déjà
  - ✅ **Gestion des erreurs:** Messages d'erreur clairs

**Exemple de validations:**
```java
// Vérification SIRET unique à la création
if (siret existe) → HTTP 409 CONFLICT

// Vérification email valide
if (email invalide) → HTTP 400 BAD REQUEST

// Vérification email unique à la création
if (email existe) → HTTP 409 CONFLICT
```

---

### 4. ✅ ClientController amélioré
**Fichier:** `controllers/ClientController.java`

**Endpoints implémentés:**

1. **GET `/api/v1/clients`**
   - **Pagination:** `?page=0&size=20&sort=id`
   - **Recherche:** `?search=terme` (recherche dans nom, entreprise, etc.)
   - **Accès:** ADMIN + MANAGER
   - **Retourne:** `Page<ClientDto>`

2. **GET `/api/v1/clients/{id}`**
   - **Accès:** ADMIN + MANAGER
   - **Retourne:** `ClientDto`

3. **POST `/api/v1/clients`**
   - **Body:** `CreateClientDto` avec `@Valid`
   - **Accès:** ADMIN + MANAGER
   - **Retourne:** `ClientDto` (HTTP 201 CREATED)

4. **PUT `/api/v1/clients/{id}`**
   - **Body:** `UpdateClientDto` avec `@Valid`
   - **Accès:** ADMIN + MANAGER
   - **Retourne:** `ClientDto`

5. **DELETE `/api/v1/clients/{id}`**
   - **Accès:** ADMIN + MANAGER
   - **Retourne:** HTTP 204 NO CONTENT

**Sécurité:**
- Tous les endpoints protégés par `@PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")`
- Validation avec `@Valid` sur tous les DTOs

---

## 🔍 Fonctionnalités de Recherche

### Recherche par nom
La recherche fonctionne sur plusieurs champs:
- `contactFirstName`
- `contactLastName`
- `nom` (compatibilité)
- `companyName`

**Exemple:**
```
GET /api/v1/clients?search=dupont
→ Recherche "dupont" dans tous les champs nommés ci-dessus
```

### Pagination
Utilise Spring Data pagination standard:
```
GET /api/v1/clients?page=0&size=20&sort=id,desc
```

**Paramètres:**
- `page`: Numéro de page (commence à 0)
- `size`: Nombre d'éléments par page (défaut: 20)
- `sort`: Tri (ex: `id,desc` ou `companyName,asc`)

---

## ✅ Validations Implémentées

### CreateClientDto
- ✅ `@NotBlank` sur `companyName`
- ✅ `@Size(min=14, max=14)` sur `siret` (exactement 14 chiffres)
- ✅ `@Pattern` pour SIRET (uniquement chiffres)
- ✅ `@Email` pour `email`
- ✅ `@Pattern` pour `phone` (format téléphone)
- ✅ `@Size` pour tous les champs texte

### UpdateClientDto
- ✅ Toutes les validations sont optionnelles (tous les champs peuvent être null)
- ✅ Mêmes validations de format que CreateClientDto

### Validations Métier (Service)
- ✅ **SIRET unique:** Vérifié à la création ET à la mise à jour
- ✅ **Email valide:** Format validé avec regex
- ✅ **Email unique:** Vérifié à la création ET à la mise à jour
- ✅ **Gestion des conflits:** HTTP 409 CONFLICT si doublon

---

## 📊 Structure des Réponses

### Page<ClientDto>
```json
{
  "content": [
    {
      "id": 1,
      "companyName": "Acme Corp",
      "siret": "12345678901234",
      "contactFirstName": "John",
      "contactLastName": "Doe",
      "email": "john@acme.com",
      "phone": "+33123456789",
      "address": "123 Rue Example",
      "city": "Paris",
      "postalCode": "75001",
      "paymentTerms": 30,
      "notes": "Client important",
      "userId": null,
      "nom": "Doe",
      "entreprise": "Acme Corp",
      "telephone": "+33123456789",
      "adresse": "123 Rue Example"
    }
  ],
  "pageable": {
    "pageNumber": 0,
    "pageSize": 20
  },
  "totalElements": 1,
  "totalPages": 1
}
```

---

## 🔒 Sécurité

### Rôles autorisés
- **ADMIN:** Accès complet (CRUD)
- **MANAGER:** Accès complet (CRUD)
- **USER:** Pas d'accès aux endpoints clients

### Configuration
```java
@PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
```

---

## 🧪 Exemples d'utilisation

### Créer un client
```bash
POST /api/v1/clients
Authorization: Bearer <token>

{
  "companyName": "Acme Corp",
  "siret": "12345678901234",
  "contactFirstName": "John",
  "contactLastName": "Doe",
  "email": "john@acme.com",
  "phone": "+33123456789",
  "address": "123 Rue Example",
  "city": "Paris",
  "postalCode": "75001",
  "paymentTerms": 30,
  "notes": "Client important"
}
```

### Rechercher des clients
```bash
GET /api/v1/clients?search=acme&page=0&size=10
Authorization: Bearer <token>
```

### Mettre à jour un client
```bash
PUT /api/v1/clients/1
Authorization: Bearer <token>

{
  "email": "newemail@acme.com",
  "paymentTerms": 45
}
```

---

## ✅ Statut de Compilation

**BUILD SUCCESS** ✓
- Tous les fichiers compilent sans erreur
- Validations Bean Validation fonctionnelles
- Pagination Spring Data opérationnelle
- Recherche multi-champs implémentée

---

## 📝 Résumé des Fichiers Modifiés/Créés

### Créés
1. `dtos/CreateClientDto.java` - DTO pour création
2. `dtos/UpdateClientDto.java` - DTO pour mise à jour

### Modifiés
1. `repositories/ClientRepo.java` - Ajout recherche et pagination
2. `dtos/ClientDto.java` - Ajout de tous les champs
3. `services/ClientService.java` - Interface mise à jour avec pagination
4. `services/impl/ClientServiceImpl.java` - Réécriture complète avec validations
5. `controllers/ClientController.java` - Mise à jour avec pagination et recherche
6. `models/Client.java` - Méthode dto() mise à jour

---

**✅ Module de gestion des clients complètement implémenté et prêt à l'emploi !**

