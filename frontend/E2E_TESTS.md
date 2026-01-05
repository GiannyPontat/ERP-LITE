# 🧪 TESTS END-TO-END - ERP-LITE

**Date:** 6 janvier 2026  
**Type:** Tests manuels & automatisés  
**Objectif:** Valider l'intégration frontend-backend

---

## 📋 PRÉREQUIS

### **1. Backend démarré**
```bash
cd /Users/woobackbaby/Projects/ERP-LITE/backend
./mvnw spring-boot:run

# Vérifier que le backend répond
curl http://localhost:8080/api/v1/health
```

### **2. Frontend démarré**
```bash
cd /Users/woobackbaby/Projects/ERP-LITE/frontend
ng serve
```

### **3. Base de données**
- Base de données initialisée avec données de test
- Compte utilisateur test disponible :
  - Email: `test@erp-lite.com`
  - Password: `Test123!`

---

## 🧪 TESTS MANUELS

### **TEST 1 : Authentification** 🔐

#### **1.1 - Login avec succès**
```
✓ Étapes :
  1. Ouvrir http://localhost:4200
  2. Cliquer sur "Se connecter" ou naviguer vers /auth/login
  3. Entrer email: test@erp-lite.com
  4. Entrer password: Test123!
  5. Cliquer sur "Se connecter"

✓ Résultat attendu :
  - Loading spinner affiché
  - Pas d'erreur dans la console
  - Redirection vers /dashboard
  - Token JWT stocké dans localStorage
  - Nom utilisateur affiché dans sidebar

✓ À vérifier dans Network tab :
  - POST http://localhost:8080/api/v1/auth/login
  - Status: 200 OK
  - Response contient: accessToken, refreshToken, user
```

#### **1.2 - Login avec échec (mauvais mot de passe)**
```
✓ Étapes :
  1. Aller sur /auth/login
  2. Entrer email: test@erp-lite.com
  3. Entrer password: WrongPassword
  4. Cliquer sur "Se connecter"

✓ Résultat attendu :
  - Message d'erreur affiché
  - Pas de redirection
  - Pas de token stocké
  - Status 401 dans Network tab
```

#### **1.3 - Logout**
```
✓ Étapes :
  1. Se connecter
  2. Cliquer sur le bouton logout (dans sidebar ou header)

✓ Résultat attendu :
  - Token supprimé de localStorage
  - Redirection vers /auth/login
  - POST /api/v1/auth/logout appelé
```

#### **1.4 - Auth Guard**
```
✓ Étapes :
  1. Sans être connecté, essayer d'accéder à /dashboard

✓ Résultat attendu :
  - Redirection automatique vers /auth/login
```

---

### **TEST 2 : Dashboard** 📊

#### **2.1 - Chargement des KPIs**
```
✓ Étapes :
  1. Se connecter
  2. Attendre le chargement du dashboard

✓ Résultat attendu :
  - 4 KPI cards affichées :
    * Chiffre d'affaires
    * Factures impayées
    * Devis en cours
    * Interventions
  - Valeurs numériques affichées
  - GET /api/v1/dashboard/stats → 200

✓ À vérifier :
  - Pas de "NaN" ou "undefined"
  - Montants au format EUR (1 234,56 €)
  - Animations jouent correctement
```

#### **2.2 - Graphique CA mensuel**
```
✓ Résultat attendu :
  - Line chart affiché avec 6 mois
  - Points cliquables avec tooltips
  - Labels mois en français
  - GET /api/v1/dashboard/monthly-revenue → 200
```

#### **2.3 - Donut chart types d'interventions**
```
✓ Résultat attendu :
  - Donut chart coloré affiché
  - Légende avec pourcentages
  - Tooltips au survol des segments
  - Total interventions au centre
```

#### **2.4 - Top clients**
```
✓ Résultat attendu :
  - Liste des 10 meilleurs clients
  - Noms, CA et nombre de factures affichés
  - GET /api/v1/dashboard/top-clients → 200
```

#### **2.5 - Documents récents**
```
✓ Résultat attendu :
  - Tableau avec 5 derniers documents
  - Mix devis et factures
  - Boutons actions fonctionnels
  - GET /api/v1/quotes → 200
  - GET /api/v1/invoices → 200
```

---

### **TEST 3 : Clients** 👥

#### **3.1 - Liste clients**
```
✓ Étapes :
  1. Cliquer sur "Clients" dans sidebar
  2. Attendre le chargement

✓ Résultat attendu :
  - URL: /clients
  - Tableau avec liste clients
  - Colonnes : Nom, Email, Téléphone, Ville, Actions
  - GET /api/v1/clients?page=0&size=20... → 200
```

#### **3.2 - Recherche client**
```
✓ Étapes :
  1. Sur /clients
  2. Taper "Dupont" dans la barre de recherche
  3. Attendre (debounce)

✓ Résultat attendu :
  - Liste filtrée avec seulement clients "Dupont"
  - GET /api/v1/clients?search=dupont... → 200
```

#### **3.3 - Créer client**
```
✓ Étapes :
  1. Cliquer sur "Nouveau client"
  2. Remplir formulaire :
     - Nom : Test Client E2E
     - Email : e2e@test.com
     - Téléphone : 06 12 34 56 78
  3. Cliquer "Enregistrer"

✓ Résultat attendu :
  - POST /api/v1/clients → 201
  - Message succès affiché
  - Redirection vers liste ou détail
  - Nouveau client visible dans la liste
```

#### **3.4 - Modifier client**
```
✓ Étapes :
  1. Cliquer sur "Modifier" d'un client
  2. Changer l'email
  3. Cliquer "Enregistrer"

✓ Résultat attendu :
  - PUT /api/v1/clients/{id} → 200
  - Message succès
  - Changement visible
```

#### **3.5 - Supprimer client**
```
✓ Étapes :
  1. Cliquer sur "Supprimer" d'un client
  2. Confirmer dans le dialog

✓ Résultat attendu :
  - DELETE /api/v1/clients/{id} → 200
  - Message succès
  - Client retiré de la liste
```

---

### **TEST 4 : Devis** 📝

#### **4.1 - Liste devis**
```
✓ Étapes :
  1. Cliquer sur "Devis" dans sidebar

✓ Résultat attendu :
  - URL: /quotes
  - Tableau avec liste devis
  - Chips de statut colorés
  - GET /api/v1/quotes → 200
```

#### **4.2 - Créer devis**
```
✓ Étapes :
  1. Cliquer "Nouveau devis"
  2. Sélectionner un client
  3. Ajouter des lignes d'articles
  4. Vérifier calcul automatique (HT, TVA, TTC)
  5. Enregistrer

✓ Résultat attendu :
  - POST /api/v1/quotes → 201
  - Total calculé correctement
  - Message succès
```

#### **4.3 - Télécharger PDF**
```
✓ Étapes :
  1. Sur la liste devis
  2. Cliquer icône "Télécharger"

✓ Résultat attendu :
  - GET /api/v1/quotes/{id}/pdf → 200
  - Fichier PDF téléchargé
  - Nom fichier : DEV-2026-XXXX.pdf
```

#### **4.4 - Envoyer par email**
```
✓ Étapes :
  1. Cliquer icône "Envoyer"
  2. Confirmer l'email du client
  3. Valider

✓ Résultat attendu :
  - POST /api/v1/quotes/{id}/send-email → 200
  - Message succès
  - Statut devis passe à "SENT"
```

#### **4.5 - Convertir en facture**
```
✓ Étapes :
  1. Sur un devis accepté
  2. Cliquer "Convertir en facture"
  3. Confirmer

✓ Résultat attendu :
  - POST /api/v1/quotes/{id}/convert-to-invoice → 201
  - Nouvelle facture créée
  - Statut devis → "CONVERTED"
  - Redirection vers facture
```

---

### **TEST 5 : Factures** 💰

#### **5.1 - Liste factures**
```
✓ Étapes :
  1. Cliquer sur "Factures" dans sidebar

✓ Résultat attendu :
  - URL: /invoices
  - Tableau avec colonnes : N°, Client, Date, Échéance, Statut, Total
  - GET /api/v1/invoices → 200
```

#### **5.2 - Marquer comme payée**
```
✓ Étapes :
  1. Sur une facture "SENT" ou "OVERDUE"
  2. Cliquer icône "Marquer payée"
  3. Confirmer

✓ Résultat attendu :
  - PATCH /api/v1/invoices/{id}/mark-as-paid → 200
  - Statut → "PAID"
  - Chip vert affiché
  - Date paiement enregistrée
```

#### **5.3 - Envoyer relance**
```
✓ Étapes :
  1. Sur une facture "OVERDUE"
  2. Cliquer "Envoyer relance"
  3. Confirmer email

✓ Résultat attendu :
  - POST /api/v1/invoices/{id}/send-reminder → 200
  - Message succès
  - Email relance envoyé
```

---

### **TEST 6 : Interventions** 🔧

#### **6.1 - Liste interventions**
```
✓ Étapes :
  1. Cliquer sur "Interventions" dans sidebar

✓ Résultat attendu :
  - URL: /interventions
  - Cartes d'interventions affichées
  - Filtres statut et type fonctionnels
  - GET /api/v1/interventions → 200
```

#### **6.2 - Filtrer par statut**
```
✓ Étapes :
  1. Sélectionner filtre "URGENT"

✓ Résultat attendu :
  - Seules interventions urgentes affichées
  - GET /api/v1/interventions?status=URGENT → 200
  - Badge "2 résultats" visible
```

#### **6.3 - Filtrer par type**
```
✓ Étapes :
  1. Sélectionner type "DEPANNAGE"

✓ Résultat attendu :
  - Seules interventions dépannage affichées
  - GET /api/v1/interventions?type=DEPANNAGE → 200
```

#### **6.4 - Recherche intervention**
```
✓ Étapes :
  1. Taper "fuite" dans recherche

✓ Résultat attendu :
  - Interventions avec "fuite" dans titre/description
  - Recherche debounced (300ms)
```

---

### **TEST 7 : Catalogue** 📦

#### **7.1 - Liste catalogue**
```
✓ Étapes :
  1. Cliquer sur "Mes tarifs" dans sidebar

✓ Résultat attendu :
  - URL: /catalog
  - Liste articles avec prix
  - Catégories affichées
  - GET /api/v1/catalog → 200
```

#### **7.2 - Recherche article**
```
✓ Étapes :
  1. Taper "mitigeur" dans recherche

✓ Résultat attendu :
  - Articles filtrés
  - GET /api/v1/catalog/search?q=mitigeur → 200
```

#### **7.3 - Créer article**
```
✓ Étapes :
  1. Cliquer "Nouvel article"
  2. Remplir :
     - Référence : TEST-001
     - Nom : Article Test E2E
     - Catégorie : ROBINETTERIE
     - Prix : 99.90€
  3. Enregistrer

✓ Résultat attendu :
  - POST /api/v1/catalog → 201
  - Article visible dans liste
```

---

## 🤖 TESTS AUTOMATISÉS (Cypress)

### **Installation Cypress**

```bash
cd /Users/woobackbaby/Projects/ERP-LITE/frontend
npm install --save-dev cypress @cypress/schematic
ng add @cypress/schematic
```

### **Configuration Cypress**

```javascript
// cypress.config.ts
import { defineConfig } from 'cypress';

export default defineConfig({
  e2e: {
    baseUrl: 'http://localhost:4200',
    viewportWidth: 1280,
    viewportHeight: 720,
    video: false,
    screenshotOnRunFailure: true,
    setupNodeEvents(on, config) {
      // implement node event listeners here
    },
  },
  env: {
    apiUrl: 'http://localhost:8080/api/v1',
    testUser: {
      email: 'test@erp-lite.com',
      password: 'Test123!'
    }
  }
});
```

### **Exemple de test : Authentification**

```javascript
// cypress/e2e/auth.cy.ts
describe('Authentication', () => {
  beforeEach(() => {
    cy.visit('/auth/login');
  });

  it('should login successfully', () => {
    cy.get('[data-cy=email]').type('test@erp-lite.com');
    cy.get('[data-cy=password]').type('Test123!');
    cy.get('[data-cy=login-btn]').click();

    // Should redirect to dashboard
    cy.url().should('include', '/dashboard');
    
    // Token should be stored
    cy.window().then((win) => {
      expect(win.localStorage.getItem('token')).to.exist;
    });
  });

  it('should show error on wrong password', () => {
    cy.get('[data-cy=email]').type('test@erp-lite.com');
    cy.get('[data-cy=password]').type('WrongPassword');
    cy.get('[data-cy=login-btn]').click();

    // Should stay on login page
    cy.url().should('include', '/auth/login');
    
    // Error message should be visible
    cy.contains('Identifiants invalides').should('be.visible');
  });

  it('should logout successfully', () => {
    // Login first
    cy.get('[data-cy=email]').type('test@erp-lite.com');
    cy.get('[data-cy=password]').type('Test123!');
    cy.get('[data-cy=login-btn]').click();
    cy.url().should('include', '/dashboard');

    // Logout
    cy.get('[data-cy=logout-btn]').click();
    
    // Should redirect to login
    cy.url().should('include', '/auth/login');
    
    // Token should be removed
    cy.window().then((win) => {
      expect(win.localStorage.getItem('token')).to.not.exist;
    });
  });
});
```

### **Lancer les tests Cypress**

```bash
# Ouvrir interface Cypress
npx cypress open

# Lancer tests en headless
npx cypress run

# Lancer un test spécifique
npx cypress run --spec "cypress/e2e/auth.cy.ts"
```

---

## 📊 RÉSULTATS ATTENDUS

### **Checklist complète**

| Test | Status | Temps |
|------|--------|-------|
| 1.1 - Login succès | ☐ | ~30s |
| 1.2 - Login échec | ☐ | ~20s |
| 1.3 - Logout | ☐ | ~20s |
| 1.4 - Auth Guard | ☐ | ~10s |
| 2.1 - Dashboard KPIs | ☐ | ~1min |
| 2.2 - Graphique CA | ☐ | ~30s |
| 2.3 - Donut chart | ☐ | ~30s |
| 2.4 - Top clients | ☐ | ~30s |
| 2.5 - Documents récents | ☐ | ~30s |
| 3.1 - Liste clients | ☐ | ~30s |
| 3.2 - Recherche client | ☐ | ~30s |
| 3.3 - Créer client | ☐ | ~1min |
| 3.4 - Modifier client | ☐ | ~1min |
| 3.5 - Supprimer client | ☐ | ~30s |
| 4.1 - Liste devis | ☐ | ~30s |
| 4.2 - Créer devis | ☐ | ~2min |
| 4.3 - Télécharger PDF | ☐ | ~30s |
| 4.4 - Envoyer email | ☐ | ~1min |
| 4.5 - Convertir facture | ☐ | ~1min |
| 5.1 - Liste factures | ☐ | ~30s |
| 5.2 - Marquer payée | ☐ | ~1min |
| 5.3 - Envoyer relance | ☐ | ~1min |
| 6.1 - Liste interventions | ☐ | ~30s |
| 6.2 - Filtrer statut | ☐ | ~20s |
| 6.3 - Filtrer type | ☐ | ~20s |
| 6.4 - Recherche | ☐ | ~30s |
| 7.1 - Liste catalogue | ☐ | ~30s |
| 7.2 - Recherche article | ☐ | ~30s |
| 7.3 - Créer article | ☐ | ~1min |

**Temps total estimé : ~25 minutes**

---

## 🐛 BUGS FRÉQUENTS À VÉRIFIER

1. **CORS errors** → Backend doit autoriser localhost:4200
2. **401 après quelques minutes** → Token expiré, refresh fonctionne ?
3. **Données ne s'affichent pas** → Console errors ? Network 404/500 ?
4. **PDF ne télécharge pas** → Content-Type: application/pdf ?
5. **Pagination ne fonctionne pas** → Paramètres page/size corrects ?
6. **Recherche trop lente** → Debounce implémenté ?
7. **Formulaires ne soumettent pas** → Validation client bloque ?
8. **Animations ne jouent pas** → CSS/JS erreur ?

---

## 📝 RAPPORT DE TEST

### **Template de rapport**

```markdown
# Rapport Test E2E - ERP-LITE
Date: [DATE]
Testeur: [NOM]
Environnement: Dev

## Résumé
- Tests passés: X/27
- Tests échoués: Y/27
- Taux de réussite: Z%
- Bugs critiques: N

## Détails des échecs
[Pour chaque test échoué]
- Test: [NOM]
- Erreur: [DESCRIPTION]
- Steps to reproduce: [ÉTAPES]
- Logs: [CONSOLE/NETWORK]

## Bugs identifiés
1. [BUG-001] Description
   - Sévérité: Critique/Majeur/Mineur
   - Impact: [DESCRIPTION]
   - Solution proposée: [SOLUTION]
```

---

**Dernière mise à jour :** 6 janvier 2026, 16:30

**Prêt pour les tests ! 🧪**

