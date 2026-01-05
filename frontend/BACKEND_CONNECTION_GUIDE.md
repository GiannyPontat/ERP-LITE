# 🔌 GUIDE DE CONNEXION BACKEND - ERP-LITE

**Date:** 6 janvier 2026  
**Statut:** ✅ Mode mock désactivé - Prêt pour connexion API

---

## ✅ CE QUI A ÉTÉ FAIT

### **1. Configuration Environnement**
```typescript
// src/environments/environment.ts
export const environment = {
  production: false,
  apiUrl: 'http://localhost:8080/api/v1'  // ← URL backend configurée
};
```

### **2. Mode Mock Désactivé**
Tous les services sont maintenant en mode API :

| Service | Statut | Fichier |
|---------|--------|---------|
| `AuthService` | ✅ API (jamais eu de mock) | `auth.service.ts` |
| `DashboardService` | ✅ API activée | `dashboard.service.ts` |
| `ClientService` | ✅ API activée | `client.service.ts` |
| `QuoteService` | ✅ API activée | `quote.service.ts` |
| `InvoiceService` | ✅ API activée | `invoice.service.ts` |
| `InterventionService` | ✅ API activée | `intervention.service.ts` |
| `CatalogService` | ✅ API activée | `catalog.service.ts` |

---

## 🎯 ENDPOINTS ATTENDUS PAR LE FRONTEND

### **🔐 Authentification** (`/api/v1/auth`)

```http
POST /api/v1/auth/login
Content-Type: application/json

{
  "email": "user@example.com",
  "password": "password123"
}

Response 200:
{
  "accessToken": "eyJhbGciOiJIUzI1NiIs...",
  "refreshToken": "eyJhbGciOiJIUzI1NiIs...",
  "user": {
    "id": 1,
    "email": "user@example.com",
    "firstName": "John",
    "lastName": "Doe"
  }
}
```

```http
POST /api/v1/auth/register
Content-Type: application/json

{
  "email": "newuser@example.com",
  "password": "password123",
  "firstName": "Jane",
  "lastName": "Smith"
}

Response 201:
{
  "message": "User registered successfully"
}
```

```http
POST /api/v1/auth/refresh
Content-Type: application/json

{
  "refreshToken": "eyJhbGciOiJIUzI1NiIs..."
}

Response 200:
{
  "accessToken": "eyJhbGciOiJIUzI1NiIs...",
  "refreshToken": "eyJhbGciOiJIUzI1NiIs...",
  "user": { ... }
}
```

```http
POST /api/v1/auth/logout
Authorization: Bearer {token}

Response 200:
{
  "message": "Logged out successfully"
}
```

---

### **📊 Dashboard** (`/api/v1/dashboard`)

```http
GET /api/v1/dashboard/stats
Authorization: Bearer {token}

Response 200:
{
  "totalRevenue": 6395.00,
  "totalProfit": 2238.25,
  "unpaidInvoicesCount": 3,
  "unpaidInvoicesAmount": 2300.00,
  "activeQuotesCount": 4,
  "totalClientsCount": 12,
  "totalQuotesCount": 15,
  "totalInvoicesCount": 12
}
```

```http
GET /api/v1/dashboard/monthly-revenue?year=2026
Authorization: Bearer {token}

Response 200:
[
  { "year": 2025, "month": 8, "revenue": 4850 },
  { "year": 2025, "month": 9, "revenue": 5320 },
  { "year": 2025, "month": 10, "revenue": 6890 },
  { "year": 2025, "month": 11, "revenue": 5780 },
  { "year": 2025, "month": 12, "revenue": 7245 },
  { "year": 2026, "month": 1, "revenue": 3500 }
]
```

```http
GET /api/v1/dashboard/top-clients
Authorization: Bearer {token}

Response 200:
[
  {
    "clientId": 8,
    "clientName": "Hôtel Parisien",
    "totalRevenue": 4850.00,
    "invoiceCount": 8
  },
  ...
]
```

---

### **👥 Clients** (`/api/v1/clients`)

```http
GET /api/v1/clients?page=0&size=20&sort=id,desc&search=dupont
Authorization: Bearer {token}

Response 200:
{
  "content": [
    {
      "id": 1,
      "companyName": "Mme Marie Dupont",
      "contactFirstName": "Marie",
      "contactLastName": "Dupont",
      "email": "marie.dupont@gmail.com",
      "phone": "06 12 34 56 78",
      "address": "15 rue de la Paix",
      "city": "Paris",
      "postalCode": "75002",
      "paymentTerms": 30,
      "createdAt": "2022-03-15T10:00:00"
    }
  ],
  "totalElements": 12,
  "totalPages": 1,
  "size": 20,
  "number": 0,
  "numberOfElements": 12,
  "first": true,
  "last": true,
  "empty": false
}
```

```http
GET /api/v1/clients/{id}
POST /api/v1/clients
PUT /api/v1/clients/{id}
DELETE /api/v1/clients/{id}
```

---

### **📝 Devis** (`/api/v1/quotes`)

```http
GET /api/v1/quotes
Authorization: Bearer {token}

Response 200:
[
  {
    "id": 1,
    "quoteNumber": "DEV-2026-0001",
    "clientId": 2,
    "clientName": "M. Jean Martin",
    "date": "2025-12-10",
    "validUntil": "2026-01-10",
    "status": "ACCEPTED",
    "subtotal": 4908.33,
    "taxRate": 20,
    "taxAmount": 981.67,
    "total": 5890.00,
    "items": [
      {
        "id": 1,
        "description": "Dépose baignoire existante",
        "quantity": 1,
        "unitPrice": 250,
        "total": 250
      }
    ]
  }
]
```

```http
GET /api/v1/quotes/{id}
POST /api/v1/quotes
PUT /api/v1/quotes/{id}
DELETE /api/v1/quotes/{id}
GET /api/v1/quotes/client/{clientId}
GET /api/v1/quotes/status/{status}
GET /api/v1/quotes/{id}/pdf (Response: Blob)
POST /api/v1/quotes/{id}/send-email?email=client@example.com
POST /api/v1/quotes/{id}/convert-to-invoice
```

---

### **💰 Factures** (`/api/v1/invoices`)

```http
GET /api/v1/invoices
POST /api/v1/invoices
POST /api/v1/invoices/from-quote/{quoteId}
GET /api/v1/invoices/{id}
PUT /api/v1/invoices/{id}
DELETE /api/v1/invoices/{id}
GET /api/v1/invoices/client/{clientId}
GET /api/v1/invoices/status/{status}
GET /api/v1/invoices/{id}/pdf (Response: Blob)
POST /api/v1/invoices/{id}/send-email?email=client@example.com
POST /api/v1/invoices/{id}/send-reminder?email=client@example.com
PATCH /api/v1/invoices/{id}/mark-as-paid
```

---

### **🔧 Interventions** (`/api/v1/interventions`)

```http
GET /api/v1/interventions
GET /api/v1/interventions?status=URGENT
GET /api/v1/interventions?type=DEPANNAGE
GET /api/v1/interventions/{id}
PATCH /api/v1/interventions/{id}/status
GET /api/v1/interventions/counts
GET /api/v1/interventions/today
```

---

### **📦 Catalogue** (`/api/v1/catalog`)

```http
GET /api/v1/catalog
GET /api/v1/catalog?category=ROBINETTERIE
GET /api/v1/catalog/search?q=mitigeur
GET /api/v1/catalog/{id}
POST /api/v1/catalog
PUT /api/v1/catalog/{id}
DELETE /api/v1/catalog/{id}
GET /api/v1/catalog/low-stock
```

---

## 🧪 TESTS À EFFECTUER

### **1. Démarrer le backend**

```bash
# Depuis le dossier backend
cd /Users/woobackbaby/Projects/ERP-LITE/backend
./mvnw spring-boot:run

# Ou avec Docker
docker-compose up
```

### **2. Vérifier que le backend répond**

```bash
# Test simple
curl http://localhost:8080/api/v1/health

# Test login (si endpoint existe)
curl -X POST http://localhost:8080/api/v1/auth/login \
  -H "Content-Type: application/json" \
  -d '{"email":"test@test.com","password":"test123"}'
```

### **3. Lancer le frontend**

```bash
cd /Users/woobackbaby/Projects/ERP-LITE/frontend
ng serve
```

### **4. Tester l'authentification**

1. Ouvrir `http://localhost:4200`
2. Aller sur `/auth/login`
3. Essayer de se connecter
4. Vérifier dans la console :
   - Requête POST vers `/api/v1/auth/login`
   - Réponse avec `accessToken`
   - Token stocké dans localStorage
   - Redirection vers `/dashboard`

### **5. Tester chaque module**

| Module | URL | Test |
|--------|-----|------|
| Dashboard | `/dashboard` | KPIs chargent |
| Clients | `/clients` | Liste affichée |
| Devis | `/quotes` | Liste affichée |
| Factures | `/invoices` | Liste affichée |
| Interventions | `/interventions` | Liste affichée |
| Catalogue | `/catalog` | Liste affichée |

---

## 🐛 GESTION DES ERREURS

### **Erreurs possibles**

#### **1. CORS Error**
```
Access to XMLHttpRequest at 'http://localhost:8080/api/v1/...' 
from origin 'http://localhost:4200' has been blocked by CORS policy
```

**Solution Backend (Spring Boot):**
```java
@Configuration
public class WebConfig implements WebMvcConfigurer {
    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/api/**")
                .allowedOrigins("http://localhost:4200")
                .allowedMethods("GET", "POST", "PUT", "DELETE", "PATCH")
                .allowedHeaders("*")
                .allowCredentials(true);
    }
}
```

#### **2. 401 Unauthorized**
```
GET /api/v1/clients → 401 Unauthorized
```

**Causes:**
- Token expiré
- Token invalide
- Token non envoyé

**Solution:**
- Vérifier que `JwtInterceptor` ajoute le header `Authorization`
- Vérifier que le token est valide
- Re-login si nécessaire

#### **3. 404 Not Found**
```
GET /api/v1/dashboard/stats → 404 Not Found
```

**Causes:**
- Endpoint n'existe pas côté backend
- URL incorrecte

**Solution:**
- Vérifier les routes backend
- Ajuster les URLs frontend si nécessaire

#### **4. 500 Internal Server Error**
```
POST /api/v1/quotes → 500 Internal Server Error
```

**Causes:**
- Erreur backend (validation, DB, etc.)

**Solution:**
- Vérifier les logs backend
- Corriger le payload envoyé

---

## 🔧 RÉACTIVER LE MODE MOCK (SI BESOIN)

Si le backend n'est pas prêt, tu peux réactiver le mode mock :

```typescript
// Dans chaque service
private useMockData = true; // ← Remettre à true
```

Ou dynamiquement :
```typescript
// Dans un composant
constructor(private clientService: ClientService) {
  this.clientService.setMockMode(true); // Réactive le mock
}
```

---

## 📊 CHECKLIST DE VALIDATION

### **Phase 1 : Auth**
- [ ] Login fonctionne
- [ ] Register fonctionne
- [ ] Token JWT stocké
- [ ] Refresh token fonctionne
- [ ] Logout fonctionne
- [ ] Auth guard redirige si non authentifié

### **Phase 2 : Dashboard**
- [ ] Stats chargent
- [ ] CA mensuel affiché
- [ ] Top clients affichés
- [ ] Graphiques s'affichent

### **Phase 3 : CRUD Clients**
- [ ] Liste clients charge
- [ ] Pagination fonctionne
- [ ] Recherche fonctionne
- [ ] Créer client fonctionne
- [ ] Modifier client fonctionne
- [ ] Supprimer client fonctionne

### **Phase 4 : CRUD Devis**
- [ ] Liste devis charge
- [ ] Créer devis fonctionne
- [ ] Modifier devis fonctionne
- [ ] Supprimer devis fonctionne
- [ ] Générer PDF fonctionne
- [ ] Envoyer email fonctionne
- [ ] Convertir en facture fonctionne

### **Phase 5 : CRUD Factures**
- [ ] Liste factures charge
- [ ] Créer facture fonctionne
- [ ] Modifier facture fonctionne
- [ ] Supprimer facture fonctionne
- [ ] Générer PDF fonctionne
- [ ] Envoyer email fonctionne
- [ ] Marquer payée fonctionne

### **Phase 6 : Interventions**
- [ ] Liste interventions charge
- [ ] Filtres fonctionnent
- [ ] Recherche fonctionne

### **Phase 7 : Catalogue**
- [ ] Liste articles charge
- [ ] Recherche fonctionne
- [ ] Filtres catégorie fonctionnent
- [ ] CRUD fonctionne

---

## 🚀 PROCHAINES ÉTAPES

Une fois la connexion backend validée :

1. **Optimisations**
   - Ajouter cache pour réduire appels API
   - Implémenter pagination côté serveur partout
   - Ajouter debounce sur recherches

2. **Upload fichiers**
   - Upload images pour catalogue
   - Upload documents pour clients

3. **Notifications temps réel**
   - WebSocket pour notifications
   - Mise à jour automatique des listes

4. **Tests**
   - Tests unitaires services
   - Tests E2E avec Cypress

---

**Dernière mise à jour :** 6 janvier 2026, 16:00

