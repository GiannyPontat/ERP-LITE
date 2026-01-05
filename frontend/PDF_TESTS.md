# 📄 TESTS PDF & GÉNÉRATION DE DOCUMENTS

**Date:** 6 janvier 2026  
**Type:** Guide de test upload fichiers et génération PDF  
**Objectif:** Valider le téléchargement et l'envoi de documents PDF

---

## 📋 PRÉREQUIS

### **1. Backend démarré**
```bash
cd /Users/woobackbaby/Projects/ERP-LITE/backend
./mvnw spring-boot:run
```

### **2. Frontend démarré**
```bash
cd /Users/woobackbaby/Projects/ERP-LITE/frontend
ng serve
```

### **3. Données de test**
- Au moins 1 client créé
- Au moins 1 devis existant
- Au moins 1 facture existante

---

## 🧪 TESTS : TÉLÉCHARGEMENT PDF DEVIS

### **TEST 1.1 - Télécharger PDF d'un devis existant**

#### **Étapes**
1. Se connecter à l'application
2. Naviguer vers "Devis" dans la sidebar
3. Dans la liste des devis, trouver un devis avec statut "SENT" ou "SIGNED"
4. Cliquer sur l'icône "Télécharger" (download icon)

#### **Résultat attendu**
```
✅ Requête HTTP:
   GET /api/v1/quotes/{id}/pdf
   Status: 200 OK
   Content-Type: application/pdf

✅ Navigateur:
   - Fichier PDF téléchargé automatiquement
   - Nom fichier: DEV-2026-XXXX.pdf
   - Taille: >10 KB (dépend du contenu)

✅ Contenu PDF:
   - Logo et en-tête entreprise
   - Numéro du devis (DEV-2026-XXXX)
   - Date d'émission
   - Informations client
   - Liste des articles/services
   - Montants (HT, TVA, TTC)
   - Conditions de paiement
   - Footer avec mentions légales
```

#### **Vérifications**
```bash
# Vérifier la requête dans Network tab (DevTools)
1. Ouvrir DevTools (F12)
2. Onglet Network
3. Filtrer par "pdf"
4. Cliquer sur télécharger
5. Vérifier:
   - Status: 200
   - Type: application/pdf
   - Size: >10 KB
```

#### **Cas d'erreur à tester**
| Test | Résultat attendu |
|------|------------------|
| Devis inexistant (id=99999) | 404 Not Found |
| Sans authentification | 401 Unauthorized |
| Devis d'un autre utilisateur | 403 Forbidden |

---

### **TEST 1.2 - Télécharger PDF depuis détail devis**

#### **Étapes**
1. Se connecter
2. Aller sur "Devis"
3. Cliquer sur un devis pour voir le détail
4. Cliquer sur le bouton "Télécharger PDF"

#### **Résultat attendu**
- Même comportement que TEST 1.1
- PDF téléchargé avec le bon nom
- Contenu identique

---

## 🧪 TESTS : TÉLÉCHARGEMENT PDF FACTURES

### **TEST 2.1 - Télécharger PDF d'une facture existante**

#### **Étapes**
1. Se connecter
2. Naviguer vers "Factures"
3. Trouver une facture avec statut "PAID" ou "SENT"
4. Cliquer sur l'icône "Télécharger"

#### **Résultat attendu**
```
✅ Requête HTTP:
   GET /api/v1/invoices/{id}/pdf
   Status: 200 OK
   Content-Type: application/pdf

✅ Fichier:
   - Nom: FAC-2026-XXXX.pdf
   - Taille: >10 KB
   - Format: PDF valide

✅ Contenu:
   - Logo et en-tête
   - Numéro facture (FAC-2026-XXXX)
   - Date émission et échéance
   - Informations client
   - Liste articles
   - Montants (HT, TVA, TTC)
   - Coordonnées bancaires (RIB)
   - Mentions légales
   - "FACTURE ACQUITTÉE" si payée
```

#### **Vérifications supplémentaires**
- [ ] Logo entreprise visible
- [ ] QR Code présent (si implémenté)
- [ ] Police lisible
- [ ] Couleurs correctes (pas de rose/violet)
- [ ] Pagination si >1 page
- [ ] Footer sur chaque page

---

## 🧪 TESTS : ENVOI PAR EMAIL

### **TEST 3.1 - Envoyer devis par email**

#### **Prérequis**
- SMTP configuré dans le backend
- Email de test valide dans le client

#### **Étapes**
1. Se connecter
2. Aller sur "Devis"
3. Trouver un devis "DRAFT" ou "SENT"
4. Cliquer sur l'icône "Envoyer" (email icon)
5. Confirmer l'email du destinataire dans le dialog
6. Cliquer "Envoyer"

#### **Résultat attendu**
```
✅ Requête HTTP:
   POST /api/v1/quotes/{id}/send-email
   Body: { "recipientEmail": "client@example.com" }
   Status: 200 OK

✅ Interface:
   - Dialog de confirmation affiché
   - Email pré-rempli avec email du client
   - Message de succès: "Devis envoyé par email"
   - Statut devis passe à "SENT"

✅ Email reçu:
   - Expéditeur: noreply@erp-lite.com (ou config)
   - Destinataire: email du client
   - Objet: "Devis DEV-2026-XXXX - [Nom entreprise]"
   - Corps: Message personnalisé + infos devis
   - Pièce jointe: DEV-2026-XXXX.pdf
   - Taille PJ: >10 KB
```

#### **Vérifier l'email reçu**
```
Subject: Devis DEV-2026-001 - ERP-LITE

Bonjour [Nom Client],

Veuillez trouver ci-joint votre devis DEV-2026-001 
d'un montant de 1 500,00 € TTC.

N'hésitez pas à nous contacter pour toute question.

Cordialement,
[Nom entreprise]

---
[Pièce jointe: DEV-2026-001.pdf]
```

---

### **TEST 3.2 - Envoyer facture par email**

#### **Étapes**
1. Se connecter
2. Aller sur "Factures"
3. Trouver une facture "SENT" ou "OVERDUE"
4. Cliquer sur "Envoyer" (ou "Relancer" si overdue)
5. Confirmer email
6. Envoyer

#### **Résultat attendu**
```
✅ Requête HTTP:
   POST /api/v1/invoices/{id}/send-email
   Status: 200 OK

✅ Email:
   - Objet: "Facture FAC-2026-XXXX - [Nom entreprise]"
   - Corps: Message avec infos paiement
   - PJ: FAC-2026-XXXX.pdf
```

---

### **TEST 3.3 - Envoyer relance facture impayée**

#### **Étapes**
1. Se connecter
2. Aller sur "Factures"
3. Trouver une facture "OVERDUE"
4. Cliquer sur "Envoyer relance"
5. Confirmer

#### **Résultat attendu**
```
✅ Requête HTTP:
   POST /api/v1/invoices/{id}/send-reminder
   Status: 200 OK

✅ Email de relance:
   - Objet: "⚠️ RELANCE - Facture FAC-2026-XXXX en retard"
   - Ton plus ferme
   - Mention du retard
   - Date échéance dépassée
   - Pénalités de retard (si applicable)
   - PJ: FAC-2026-XXXX.pdf
```

---

## 🧪 TESTS : GÉNÉRATION PDF AVANCÉE

### **TEST 4.1 - PDF multi-pages**

#### **Préparation**
Créer un devis avec >20 lignes d'articles pour forcer plusieurs pages

#### **Vérifications**
- [ ] Pagination correcte (page 1/3, 2/3, 3/3)
- [ ] Header répété sur chaque page
- [ ] Footer répété sur chaque page
- [ ] Pas de coupure au milieu d'une ligne
- [ ] Total TTC sur la dernière page

---

### **TEST 4.2 - PDF avec caractères spéciaux**

#### **Préparation**
Créer un devis avec :
- Client avec nom accentué : "François Cœur"
- Article : "Tuyau Ø 32mm"
- Notes : "Devis valable jusqu'au 31/12/2026"

#### **Vérifications**
- [ ] Accents affichés correctement (é, è, à, ç)
- [ ] Symboles spéciaux OK (Ø, €, %, °)
- [ ] Pas de carrés ou "?" à la place

---

### **TEST 4.3 - PDF avec logo personnalisé**

#### **Prérequis**
Logo entreprise uploadé dans les paramètres

#### **Vérifications**
- [ ] Logo visible en haut du PDF
- [ ] Taille appropriée (pas trop grand/petit)
- [ ] Qualité correcte (pas pixelisé)
- [ ] Alignement correct

---

## 🧪 TESTS : PERFORMANCE PDF

### **TEST 5.1 - Temps de génération**

#### **Méthodologie**
```javascript
// Dans DevTools Console
console.time('PDF Generation');
// Cliquer sur télécharger
console.timeEnd('PDF Generation');
```

#### **Résultats attendus**
| Type de document | Temps max acceptable |
|------------------|----------------------|
| Devis simple (<10 lignes) | < 2 secondes |
| Devis complexe (>20 lignes) | < 5 secondes |
| Facture simple | < 2 secondes |
| Facture multi-pages | < 5 secondes |

---

### **TEST 5.2 - Taille fichier**

#### **Vérifications**
```bash
# Télécharger un PDF puis vérifier la taille
ls -lh ~/Downloads/DEV-2026-*.pdf

# Tailles attendues
Devis simple:     50-200 KB
Devis avec logo:  100-500 KB
Facture:          50-200 KB
```

---

## 🧪 TESTS : COMPATIBILITÉ

### **TEST 6.1 - Ouverture dans différents viewers**

Télécharger un PDF et ouvrir avec :

| Viewer | Test | Status |
|--------|------|--------|
| Chrome intégré | ☐ Ouvre sans erreur | ⏳ |
| Firefox intégré | ☐ Ouvre sans erreur | ⏳ |
| Safari intégré | ☐ Ouvre sans erreur | ⏳ |
| Adobe Acrobat Reader | ☐ Ouvre sans erreur | ⏳ |
| Preview (macOS) | ☐ Ouvre sans erreur | ⏳ |
| Edge intégré | ☐ Ouvre sans erreur | ⏳ |

---

### **TEST 6.2 - Impression PDF**

#### **Étapes**
1. Télécharger un PDF
2. L'ouvrir
3. Imprimer (ou "Imprimer en PDF")

#### **Vérifications**
- [ ] Aperçu impression correct
- [ ] Marges correctes
- [ ] Couleurs préservées
- [ ] Pas de coupures
- [ ] Qualité texte OK

---

## 🧪 TESTS : SÉCURITÉ PDF

### **TEST 7.1 - Autorisation téléchargement**

#### **Test sans authentification**
```bash
# Essayer de télécharger sans token
curl -X GET http://localhost:8080/api/v1/quotes/1/pdf
```

**Résultat attendu:** 401 Unauthorized

#### **Test avec mauvais utilisateur**
```bash
# Se connecter avec user A
# Essayer de télécharger devis de user B
```

**Résultat attendu:** 403 Forbidden (si multi-tenancy)

---

### **TEST 7.2 - Protection PDF**

Si les PDF sont protégés, vérifier :
- [ ] Pas de copie de texte (si souhaité)
- [ ] Pas de modification (si souhaité)
- [ ] Pas d'impression (si souhaité)
- [ ] Mot de passe requis (si souhaité)

---

## 🧪 TESTS : ERREURS & EDGE CASES

### **TEST 8.1 - Devis vide**

Créer un devis sans aucun article

**Résultat attendu:**
- PDF généré quand même
- Affiche 0,00 € TTC
- Message "Aucun article"

---

### **TEST 8.2 - Client sans email**

Essayer d'envoyer un devis pour un client sans email

**Résultat attendu:**
- Message d'erreur: "Le client n'a pas d'email"
- Proposition de modifier le client
- Envoi annulé

---

### **TEST 8.3 - SMTP non configuré**

Si SMTP pas configuré dans le backend

**Résultat attendu:**
- Message d'erreur: "Service email non disponible"
- 500 Internal Server Error ou message custom
- Suggestion de configurer SMTP

---

### **TEST 8.4 - Email invalide**

Essayer d'envoyer à un email mal formé

**Résultat attendu:**
- Validation côté client bloque
- Message: "Format d'email invalide"

---

## 📊 CHECKLIST COMPLÈTE

| ID | Test | Status | Notes |
|----|------|--------|-------|
| 1.1 | Télécharger PDF devis | ☐ | |
| 1.2 | Télécharger depuis détail | ☐ | |
| 2.1 | Télécharger PDF facture | ☐ | |
| 3.1 | Envoyer devis par email | ☐ | |
| 3.2 | Envoyer facture par email | ☐ | |
| 3.3 | Envoyer relance impayé | ☐ | |
| 4.1 | PDF multi-pages | ☐ | |
| 4.2 | Caractères spéciaux | ☐ | |
| 4.3 | Logo personnalisé | ☐ | |
| 5.1 | Performance génération | ☐ | |
| 5.2 | Taille fichier | ☐ | |
| 6.1 | Compatibilité viewers | ☐ | |
| 6.2 | Impression | ☐ | |
| 7.1 | Autorisation | ☐ | |
| 7.2 | Protection PDF | ☐ | |
| 8.1 | Devis vide | ☐ | |
| 8.2 | Client sans email | ☐ | |
| 8.3 | SMTP non configuré | ☐ | |
| 8.4 | Email invalide | ☐ | |

**Total:** 19 tests

---

## 🔧 CONFIGURATION SMTP (Backend)

Pour que les emails fonctionnent, vérifier la configuration backend :

```properties
# application.properties
spring.mail.host=smtp.gmail.com
spring.mail.port=587
spring.mail.username=your-email@gmail.com
spring.mail.password=your-app-password
spring.mail.properties.mail.smtp.auth=true
spring.mail.properties.mail.smtp.starttls.enable=true

# Email settings
app.email.from=noreply@erp-lite.com
app.email.replyTo=contact@erp-lite.com
```

---

## 🐛 DÉPANNAGE

### **PDF ne se télécharge pas**

**Causes possibles:**
1. Backend ne génère pas le PDF
2. Content-Type incorrect
3. CORS bloque le téléchargement

**Solutions:**
```bash
# Vérifier endpoint direct
curl -X GET http://localhost:8080/api/v1/quotes/1/pdf \
  -H "Authorization: Bearer YOUR_TOKEN" \
  --output test.pdf

# Si fichier créé OK → problème frontend/CORS
# Si erreur → problème backend
```

---

### **Email ne s'envoie pas**

**Causes possibles:**
1. SMTP non configuré
2. Firewall bloque port 587
3. Credentials incorrects
4. Gmail/Yahoo bloque (besoin App Password)

**Solutions:**
```bash
# Tester SMTP manuellement
telnet smtp.gmail.com 587

# Vérifier logs backend
tail -f backend/logs/application.log | grep "email"
```

---

### **PDF corrompu ou vide**

**Causes possibles:**
1. Erreur génération backend
2. Encoding problème
3. Template PDF mal configuré

**Solutions:**
- Vérifier logs backend
- Tester avec un devis simple
- Vérifier la librairie PDF (iText, PDFKit, etc.)

---

## 🎯 CRITÈRES DE SUCCÈS

Les tests PDF sont réussis quand :

- ✅ Tous les PDF se téléchargent sans erreur
- ✅ Contenu des PDF est correct et complet
- ✅ Emails s'envoient avec PDF en pièce jointe
- ✅ Performance acceptable (<2s pour simple)
- ✅ Compatibilité tous viewers OK
- ✅ Sécurité respectée (auth requise)
- ✅ Gestion erreurs correcte
- ✅ 19/19 tests passés ✅

---

**Temps estimé pour tous les tests:** ~45 minutes

---

*Dernière mise à jour : 6 janvier 2026, 17:00*  
*Version : 1.0.0*

