# Implémentation Génération PDF - Documentation

## ✅ Fonctionnalité Implémentée

Génération de PDF pour les devis et factures avec formatage professionnel.

## 📦 Dépendances Ajoutées

### OpenPDF 1.3.30
```xml
<dependency>
    <groupId>com.github.librepdf</groupId>
    <artifactId>openpdf</artifactId>
    <version>1.3.30</version>
</dependency>
```

**Pourquoi OpenPDF ?**
- Fork open-source d'iText (licence LGPL/MPL)
- Compatible avec Java 17+
- API similaire à iText (facile à utiliser)
- Actif et maintenu

## 📁 Fichiers Créés/Modifiés

### Nouveaux Fichiers
1. **`services/PdfService.java`** - Interface du service PDF
2. **`services/impl/PdfServiceImpl.java`** - Implémentation complète

### Fichiers Modifiés
1. **`pom.xml`** - Ajout dépendance OpenPDF
2. **`controllers/QuoteController.java`** - Ajout endpoint `/api/v1/quotes/{id}/pdf`
3. **`controllers/InvoiceController.java`** - Ajout endpoint `/api/v1/invoices/{id}/pdf`

## 🔌 Endpoints API

### Générer PDF d'un Devis
```
GET /api/v1/quotes/{id}/pdf
```

**Autorisation :** ADMIN, USER  
**Réponse :** Fichier PDF (application/pdf)  
**Nom du fichier :** `devis-{quoteNumber}.pdf`

**Exemple :**
```bash
curl -X GET "http://localhost:8080/api/v1/quotes/1/pdf" \
  -H "Authorization: Bearer {token}" \
  -o devis.pdf
```

### Générer PDF d'une Facture
```
GET /api/v1/invoices/{id}/pdf
```

**Autorisation :** ADMIN, USER  
**Réponse :** Fichier PDF (application/pdf)  
**Nom du fichier :** `facture-{invoiceNumber}.pdf`

**Exemple :**
```bash
curl -X GET "http://localhost:8080/api/v1/invoices/1/pdf" \
  -H "Authorization: Bearer {token}" \
  -o facture.pdf
```

## 📄 Structure du PDF Généré

### Pour les Devis
1. **En-tête**
   - Titre "DEVIS" (grand, centré)
   - Numéro de devis (ex: "N° DEV-2026-0001")

2. **Informations Entreprise/Client**
   - Colonne gauche : Informations de l'entreprise
   - Colonne droite : Informations du client
   - Date de création et date de validité

3. **Tableau des Articles**
   - Description
   - Quantité
   - Prix unitaire HT
   - Taux TVA
   - Total HT

4. **Totaux**
   - Sous-total HT
   - TVA
   - Total TTC

5. **Notes** (si présentes)
6. **Conditions générales** (si présentes)

### Pour les Factures
Similaire aux devis, avec en plus :
- Date d'échéance
- Date de paiement (si payée)
- Statut de paiement
- Référence au devis (si conversion depuis un devis)

## 🎨 Formatage

- **Police :** Helvetica
- **Format de page :** A4
- **Marges :** 50 points (1,76 cm)
- **Format des dates :** DD/MM/YYYY (français)
- **Format des montants :** 2 décimales + " €"
- **Format des pourcentages :** 2 décimales + "%"

## ⚙️ Configuration Actuelle

### Informations Entreprise
Les informations de l'entreprise sont actuellement **codées en dur** dans le PDF :
- Nom : "ERP-LITE"
- Adresse : "Votre entreprise"
- Adresse : "Votre adresse"
- Ville : "Votre ville, Code postal"
- Téléphone : "Tél: Votre téléphone"
- Email : "Email: Votre email"

### À Personnaliser
Pour personnaliser les informations de l'entreprise, modifier la méthode `addCompanyAndClientInfo()` dans `PdfServiceImpl.java` :

```java
companyCell.addElement(new Paragraph("Votre Nom d'Entreprise", HEADER_FONT));
companyCell.addElement(new Paragraph("Votre adresse complète", NORMAL_FONT));
// etc.
```

**Amélioration future :** Créer une entité `Company` ou table de configuration pour stocker ces informations en base de données.

## 🔍 Utilisation dans le Code

### Service
```java
@Autowired
private PdfService pdfService;

// Générer PDF devis
byte[] pdfBytes = pdfService.generateQuotePdf(quoteDto);

// Générer PDF facture
byte[] pdfBytes = pdfService.generateInvoicePdf(invoiceDto);
```

### Controller
Les endpoints sont automatiquement documentés dans Swagger :
- Accéder à `/swagger-ui.html`
- Chercher les endpoints "Quotes" ou "Invoices"
- Tester l'endpoint `/pdf`

## ✅ Tests à Effectuer

1. **Test Génération PDF Devis**
   - Créer un devis avec des articles
   - Appeler `/api/v1/quotes/{id}/pdf`
   - Vérifier que le PDF se télécharge
   - Ouvrir le PDF et vérifier le contenu

2. **Test Génération PDF Facture**
   - Créer une facture avec des articles
   - Appeler `/api/v1/invoices/{id}/pdf`
   - Vérifier que le PDF se télécharge
   - Vérifier l'affichage du statut de paiement

3. **Test avec Données Réelles**
   - Créer un devis/facture avec toutes les données remplies
   - Vérifier le formatage des montants
   - Vérifier les dates
   - Vérifier les tableaux avec plusieurs articles

## 🐛 Gestion d'Erreurs

- Si le devis/facture n'existe pas : 404 Not Found
- Si erreur de génération PDF : 500 Internal Server Error avec log détaillé
- Les erreurs sont loggées avec `log.error()` pour le débogage

## 📝 Notes Importantes

1. **Performance :** La génération PDF est synchrone. Pour de gros volumes, considérer l'utilisation de tâches asynchrones.

2. **Mémoire :** Les PDF sont générés en mémoire. Pour de très gros documents, envisager un traitement par flux.

3. **Personnalisation :** Le template PDF est dans le code Java. Pour plus de flexibilité, considérer l'utilisation de templates Thymeleaf convertis en PDF.

4. **Logo :** Pas encore implémenté. Pour ajouter un logo :
   ```java
   Image logo = Image.getInstance("path/to/logo.png");
   document.add(logo);
   ```

## 🚀 Prochaines Étapes (Améliorations Futures)

1. **Configuration Entreprise**
   - Créer entité/table pour stocker les infos entreprise
   - Permettre la personnalisation via interface admin

2. **Template Personnalisable**
   - Utiliser Thymeleaf pour les templates HTML
   - Convertir HTML en PDF (flying-saucer-pdf ou OpenHTMLToPDF)

3. **Logo et Branding**
   - Upload de logo
   - Personnalisation des couleurs
   - Choix de polices

4. **Multilingue**
   - Support de plusieurs langues dans le PDF
   - Format de dates localisé

5. **Signature Électronique**
   - Ajout de signature électronique
   - Horodatage

## ✨ Résumé

✅ Génération PDF fonctionnelle pour devis et factures  
✅ Format professionnel avec toutes les informations  
✅ Endpoints API documentés (Swagger)  
✅ Gestion d'erreurs  
✅ Format français (dates, devises)  

⏳ À personnaliser : Informations entreprise (actuellement codées en dur)

