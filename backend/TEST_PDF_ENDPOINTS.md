# Guide de Test des Endpoints PDF

Ce guide explique comment tester les endpoints de génération PDF pour les devis et factures.

## Prérequis

1. **Application Spring Boot démarrée**
   ```bash
   cd backend
   mvn spring-boot:run
   ```
   L'application doit être accessible sur `http://localhost:8080`

2. **Données de test**
   - Au moins un devis existant dans la base de données
   - Au moins une facture existante dans la base de données
   - Un compte utilisateur pour l'authentification

## Méthode 1 : Script de Test Automatisé

### Script Interactif
```bash
./test-pdf-endpoints.sh
```

Le script vous demandera :
- Email et mot de passe pour l'authentification
- ID du devis à tester (optionnel)
- ID de la facture à tester (optionnel)

### Script Simple (ligne de commande)
```bash
./test-pdf-endpoints-simple.sh <email> <password> [quote_id] [invoice_id]
```

**Exemple :**
```bash
./test-pdf-endpoints-simple.sh admin@example.com password123 1 1
```

## Méthode 2 : Test Manuel avec cURL

### 1. Authentification

```bash
# Récupérer le token JWT
curl -X POST http://localhost:8080/api/v1/auth/login \
  -H "Content-Type: application/json" \
  -d '{"email":"votre-email@example.com","password":"votre-mot-de-passe"}'
```

**Réponse attendue :**
```json
{
  "accessToken": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
  "refreshToken": "...",
  "type": "Bearer"
}
```

Copiez le `accessToken` pour l'utiliser dans les requêtes suivantes.

### 2. Générer PDF d'un Devis

```bash
# Remplacer {TOKEN} par votre token et {ID} par l'ID du devis
curl -X GET http://localhost:8080/api/v1/quotes/{ID}/pdf \
  -H "Authorization: Bearer {TOKEN}" \
  -o devis-{ID}.pdf
```

**Exemple :**
```bash
curl -X GET http://localhost:8080/api/v1/quotes/1/pdf \
  -H "Authorization: Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9..." \
  -o devis-1.pdf
```

### 3. Générer PDF d'une Facture

```bash
# Remplacer {TOKEN} par votre token et {ID} par l'ID de la facture
curl -X GET http://localhost:8080/api/v1/invoices/{ID}/pdf \
  -H "Authorization: Bearer {TOKEN}" \
  -o facture-{ID}.pdf
```

**Exemple :**
```bash
curl -X GET http://localhost:8080/api/v1/invoices/1/pdf \
  -H "Authorization: Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9..." \
  -o facture-1.pdf
```

## Méthode 3 : Test via Swagger UI

1. **Accéder à Swagger UI**
   ```
   http://localhost:8080/swagger-ui.html
   ```

2. **S'authentifier**
   - Cliquer sur le bouton "Authorize" en haut à droite
   - Entrer : `Bearer {votre-token}`
   - Cliquer sur "Authorize"

3. **Tester l'endpoint**
   - Chercher `/api/v1/quotes/{id}/pdf` ou `/api/v1/invoices/{id}/pdf`
   - Cliquer sur "Try it out"
   - Entrer l'ID du devis/facture
   - Cliquer sur "Execute"
   - Le PDF sera téléchargé automatiquement

## Méthode 4 : Test avec Postman

1. **Créer une requête GET**
   - URL : `http://localhost:8080/api/v1/quotes/1/pdf`
   - Méthode : GET

2. **Ajouter l'header d'autorisation**
   - Key : `Authorization`
   - Value : `Bearer {votre-token}`

3. **Envoyer la requête**
   - Le PDF sera téléchargé automatiquement

## Vérification des Résultats

### Vérifier le fichier PDF généré

1. **Taille du fichier**
   ```bash
   ls -lh devis-*.pdf facture-*.pdf
   ```
   Les fichiers doivent avoir une taille > 0 bytes (généralement quelques KB)

2. **Ouvrir le PDF**
   ```bash
   # Sur macOS
   open devis-1.pdf
   
   # Sur Linux
   xdg-open devis-1.pdf
   
   # Sur Windows
   start devis-1.pdf
   ```

3. **Vérifier le contenu**
   - ✅ En-tête avec titre "DEVIS" ou "FACTURE"
   - ✅ Numéro du document
   - ✅ Informations entreprise et client
   - ✅ Tableau des articles
   - ✅ Totaux (HT, TVA, TTC)
   - ✅ Dates au format français (DD/MM/YYYY)
   - ✅ Montants au format français (X,XX €)

## Codes de Réponse HTTP

| Code | Signification |
|------|---------------|
| 200  | ✅ PDF généré avec succès |
| 401  | ❌ Non authentifié (token manquant ou invalide) |
| 403  | ❌ Accès refusé (rôle insuffisant) |
| 404  | ❌ Devis/Facture non trouvé |
| 500  | ❌ Erreur serveur (vérifier les logs) |

## Dépannage

### Erreur 401 Unauthorized
- Vérifier que le token est valide
- Vérifier le format : `Bearer {token}` (avec l'espace)
- Le token expire après 15 minutes par défaut

### Erreur 404 Not Found
- Vérifier que le devis/facture existe dans la base de données
- Vérifier l'ID utilisé

### Erreur 500 Internal Server Error
- Vérifier les logs de l'application
- Vérifier que le client associé existe
- Vérifier que les données sont complètes

### PDF vide ou corrompu
- Vérifier que le devis/facture a des articles
- Vérifier les logs pour des erreurs de génération
- Vérifier que OpenPDF est bien dans les dépendances

## Script de Test Complet

Pour tester automatiquement avec plusieurs scénarios :

```bash
#!/bin/bash

# Configuration
BASE_URL="http://localhost:8080"
EMAIL="admin@example.com"
PASSWORD="password123"

# Authentification
TOKEN=$(curl -s -X POST "${BASE_URL}/api/v1/auth/login" \
  -H "Content-Type: application/json" \
  -d "{\"email\":\"${EMAIL}\",\"password\":\"${PASSWORD}\"}" \
  | grep -o '"accessToken":"[^"]*' | cut -d'"' -f4)

# Test Quote PDF
curl -X GET "${BASE_URL}/api/v1/quotes/1/pdf" \
  -H "Authorization: Bearer ${TOKEN}" \
  -o test-quote.pdf \
  -w "\nHTTP Status: %{http_code}\n"

# Test Invoice PDF
curl -X GET "${BASE_URL}/api/v1/invoices/1/pdf" \
  -H "Authorization: Bearer ${TOKEN}" \
  -o test-invoice.pdf \
  -w "\nHTTP Status: %{http_code}\n"

echo "Tests terminés. Vérifiez les fichiers test-quote.pdf et test-invoice.pdf"
```

## Prochaines Étapes

Après avoir testé les endpoints :
1. Vérifier le format et la mise en page des PDF
2. Personnaliser les informations de l'entreprise (voir PDF_IMPLEMENTATION.md)
3. Tester avec différents types de données (devis avec/sans TVA, factures payées/non payées, etc.)
4. Vérifier les performances avec des documents contenant beaucoup d'articles

