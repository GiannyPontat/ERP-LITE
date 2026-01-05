# 🧪 GUIDE RAPIDE - TESTS E2E

**Dernière mise à jour:** 6 janvier 2026  
**Version:** 1.0.0

---

## 🎯 DÉMARRAGE RAPIDE (30 secondes)

### **Étape 1 : Démarrer le backend**
```bash
cd /Users/woobackbaby/Projects/ERP-LITE/backend
./mvnw spring-boot:run
```

### **Étape 2 : Tester que le backend répond**
```bash
cd /Users/woobackbaby/Projects/ERP-LITE/frontend
./test-backend.sh
```

**Résultat attendu :**
```
✓ All tests passed! Backend is ready.
Success rate: 100.00%
```

### **Étape 3 : Démarrer le frontend**
```bash
ng serve
```

### **Étape 4 : Tester manuellement**
Ouvrir http://localhost:4200 et se connecter :
- Email: `test@erp-lite.com`
- Password: `Test123!`

---

## 📚 DOCUMENTS DISPONIBLES

| Document | Description | Lignes |
|----------|-------------|--------|
| `E2E_TESTS.md` | Suite complète de 27 tests manuels | 600+ |
| `BACKEND_CONNECTION_GUIDE.md` | Guide détaillé de connexion backend | 517 |
| `FINAL_SUMMARY.md` | Résumé complet de la mission | 400+ |
| `cypress-example-tests.ts` | Exemples de tests Cypress automatisés | 700+ |
| `test-backend.sh` | Script de vérification automatique | 200+ |
| `README_TESTS.md` | Ce fichier (guide rapide) | 250+ |

---

## 🧪 OPTIONS DE TEST

### **Option 1 : Test Automatique Rapide (10 secondes)**

```bash
./test-backend.sh
```

**Ce que ça teste :**
- ✅ Health check
- ✅ Login & JWT
- ✅ Dashboard (3 endpoints)
- ✅ Clients
- ✅ Quotes
- ✅ Invoices
- ✅ Interventions
- ✅ Catalog

**Total : 10 tests en 10 secondes**

---

### **Option 2 : Test Manuel Complet (25 minutes)**

```bash
# 1. Ouvrir le guide
open E2E_TESTS.md

# 2. Démarrer les serveurs
./start-servers.sh  # (ou manuellement comme ci-dessus)

# 3. Suivre les 27 tests du guide
```

**Ce que ça teste :**
- ✅ Authentification (4 tests)
- ✅ Dashboard (5 tests)
- ✅ Clients (5 tests)
- ✅ Devis (5 tests)
- ✅ Factures (3 tests)
- ✅ Interventions (4 tests)
- ✅ Catalogue (3 tests)

**Total : 27 tests en 25 minutes**

---

### **Option 3 : Tests Cypress Automatisés (À installer)**

```bash
# Installation (première fois seulement)
npm install --save-dev cypress @cypress/schematic
ng add @cypress/schematic

# Créer les fichiers de test
# Voir cypress-example-tests.ts pour les exemples

# Lancer les tests
npx cypress open    # Interface graphique
npx cypress run     # Mode headless
```

**Avantages :**
- ✅ Tests automatiques reproductibles
- ✅ Screenshots en cas d'erreur
- ✅ Intégrable en CI/CD
- ✅ Rapports détaillés

---

## 🐛 DÉPANNAGE

### **Problème : Backend ne répond pas**

```bash
# Vérifier que le backend est démarré
ps aux | grep "spring-boot"

# Si non démarré
cd /Users/woobackbaby/Projects/ERP-LITE/backend
./mvnw spring-boot:run
```

### **Problème : Erreur CORS**

**Solution :** Vérifier la configuration CORS dans le backend.

Le backend doit autoriser `http://localhost:4200` :

```java
@CrossOrigin(origins = "http://localhost:4200")
```

### **Problème : 401 Unauthorized**

**Causes possibles :**
1. Token expiré → Se re-connecter
2. Backend JWT secret non configuré → Vérifier `application.properties`
3. Token mal formé → Vider localStorage

**Solution rapide :**
```javascript
// Dans la console du navigateur
localStorage.clear();
location.reload();
```

### **Problème : 404 Not Found**

**Vérifier que les endpoints existent :**
```bash
curl http://localhost:8080/api/v1/clients
```

**Si 404, consulter :**
- `BACKEND_CONNECTION_GUIDE.md` - Liste complète des 46 endpoints attendus

### **Problème : Données ne s'affichent pas**

**Checklist :**
1. ✅ Backend démarré ?
2. ✅ Frontend en mode API (pas mock) ?
3. ✅ Token JWT valide ?
4. ✅ Erreurs dans la console ?
5. ✅ Erreurs Network tab ?

**Vérifier mode API :**
```typescript
// Dans chaque service, vérifier :
private useMockData = false; // ✅ Doit être false
```

---

## 📊 RÉSULTATS ATTENDUS

### **Script test-backend.sh**

```
========================================
   📊 RESULTS
========================================

Total tests:  10
Passed:       10  ✅
Failed:       0   

Success rate: 100.00% ✅

✓ All tests passed! Backend is ready.
```

### **Tests manuels E2E**

| Catégorie | Tests | Status |
|-----------|-------|--------|
| Auth | 4 | ☑️ À tester |
| Dashboard | 5 | ☑️ À tester |
| Clients | 5 | ☑️ À tester |
| Devis | 5 | ☑️ À tester |
| Factures | 3 | ☑️ À tester |
| Interventions | 4 | ☑️ À tester |
| Catalogue | 3 | ☑️ À tester |
| **TOTAL** | **27** | **☑️ 0/27** |

### **Tests Cypress**

```
  Auth Tests
    ✓ should display login form (245ms)
    ✓ should login successfully (1234ms)
    ✓ should show error for invalid credentials (456ms)
    ✓ should logout successfully (789ms)

  Dashboard Tests
    ✓ should display all 4 KPI cards (567ms)
    ✓ should display line chart (892ms)
    ✓ should display donut chart (456ms)

  7 passing (5.2s)
```

---

## 🚀 CHECKLIST FINALE

Avant de considérer les tests terminés, vérifier que :

### **Backend**
- [ ] Backend démarré et accessible sur port 8080
- [ ] Base de données initialisée avec données de test
- [ ] Compte test créé (`test@erp-lite.com / Test123!`)
- [ ] CORS configuré pour localhost:4200
- [ ] JWT secret configuré

### **Frontend**
- [ ] Services en mode API (useMockData = false)
- [ ] Environment.ts configuré (apiUrl correct)
- [ ] Application démarre sans erreur (ng serve)
- [ ] Aucune erreur dans la console navigateur

### **Tests Automatiques**
- [ ] `test-backend.sh` passe au vert (10/10)
- [ ] Login fonctionne via l'interface
- [ ] Dashboard affiche des données réelles
- [ ] Création client fonctionne
- [ ] Création devis fonctionne
- [ ] PDF téléchargeable
- [ ] Email envoyé (si SMTP configuré)

### **Tests Manuels**
- [ ] 27 tests E2E complétés
- [ ] Rapport de test rempli
- [ ] Bugs identifiés documentés
- [ ] Screenshots des erreurs pris

### **Tests Cypress (Optionnel)**
- [ ] Cypress installé
- [ ] Tests auth créés et passent
- [ ] Tests dashboard créés et passent
- [ ] Tests clients créés et passent
- [ ] CI/CD configuré (si applicable)

---

## 📞 AIDE ET SUPPORT

### **Problème non résolu ?**

1. **Consulter les logs**
   ```bash
   # Backend logs
   cd backend && tail -f logs/application.log
   
   # Frontend console
   # Ouvrir DevTools (F12) → Console tab
   ```

2. **Consulter la documentation**
   - `E2E_TESTS.md` - Tests manuels détaillés
   - `BACKEND_CONNECTION_GUIDE.md` - Guide connexion backend
   - `FINAL_SUMMARY.md` - Résumé complet du projet

3. **Vérifier l'état du backend**
   ```bash
   ./test-backend.sh
   ```

4. **Nettoyer et redémarrer**
   ```bash
   # Frontend
   rm -rf node_modules/.cache
   ng serve
   
   # Backend
   ./mvnw clean spring-boot:run
   ```

---

## 📈 PROCHAINES ÉTAPES

Après avoir complété tous les tests :

### **Immédiat**
1. ✅ Documenter les bugs trouvés
2. ✅ Corriger les bugs critiques
3. ✅ Re-tester après corrections

### **Court terme**
1. ⏳ Installer Cypress
2. ⏳ Créer tests automatisés
3. ⏳ Configurer CI/CD

### **Moyen terme**
1. ⏳ Tests de performance (Lighthouse)
2. ⏳ Tests d'accessibilité (a11y)
3. ⏳ Tests de sécurité (OWASP)

### **Long terme**
1. ⏳ Tests de charge (K6, JMeter)
2. ⏳ Tests de régression automatiques
3. ⏳ Monitoring en production

---

## 🎯 OBJECTIFS DE QUALITÉ

| Métrique | Cible | Actuel | Status |
|----------|-------|--------|--------|
| Tests E2E passés | 27/27 | 0/27 | ⏳ À tester |
| Script backend | 10/10 | ? | ⏳ À tester |
| Couverture code | >80% | ? | ⏳ À mesurer |
| Bugs critiques | 0 | ? | ⏳ À identifier |
| Performance (Lighthouse) | >90 | ? | ⏳ À mesurer |
| Accessibilité | >95 | ? | ⏳ À mesurer |

---

## 🏆 SUCCÈS

Tu auras réussi cette mission quand :

- ✅ `./test-backend.sh` affiche 10/10 tests passés
- ✅ Les 27 tests manuels E2E sont validés
- ✅ L'application fonctionne sans erreur en mode API
- ✅ Les données s'affichent correctement dans toutes les pages
- ✅ Les CRUD (Create, Read, Update, Delete) fonctionnent
- ✅ Les PDF se téléchargent correctement
- ✅ Les emails s'envoient (si SMTP configuré)
- ✅ Aucun bug bloquant identifié

---

**🚀 Prêt à tester ? Commence par lancer `./test-backend.sh` ! 🚀**

---

*Dernière mise à jour : 6 janvier 2026, 16:50*  
*Version : 1.0.0*

