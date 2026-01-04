#!/bin/bash

# Script de test pour les endpoints PDF
# Prérequis : Application Spring Boot doit être démarrée sur http://localhost:8080

BASE_URL="http://localhost:8080"
API_URL="${BASE_URL}/api/v1"

# Couleurs pour l'affichage
GREEN='\033[0;32m'
RED='\033[0;31m'
YELLOW='\033[1;33m'
NC='\033[0m' # No Color

echo "========================================="
echo "Test des Endpoints PDF - ERP-LITE"
echo "========================================="
echo ""

# Vérifier si l'application est démarrée
echo -e "${YELLOW}Vérification de l'application...${NC}"
if ! curl -s "${BASE_URL}/actuator/health" > /dev/null 2>&1 && ! curl -s "${BASE_URL}/swagger-ui.html" > /dev/null 2>&1; then
    echo -e "${RED}❌ L'application ne semble pas être démarrée sur ${BASE_URL}${NC}"
    echo -e "${YELLOW}Démarrez l'application avec: mvn spring-boot:run${NC}"
    exit 1
fi
echo -e "${GREEN}✅ Application détectée${NC}"
echo ""

# Demander les credentials
echo -e "${YELLOW}Authentification requise${NC}"
read -p "Email: " EMAIL
read -sp "Password: " PASSWORD
echo ""

# Connexion pour obtenir le token
echo -e "${YELLOW}Connexion...${NC}"
LOGIN_RESPONSE=$(curl -s -X POST "${API_URL}/auth/login" \
  -H "Content-Type: application/json" \
  -d "{\"email\":\"${EMAIL}\",\"password\":\"${PASSWORD}\"}")

TOKEN=$(echo $LOGIN_RESPONSE | grep -o '"accessToken":"[^"]*' | cut -d'"' -f4)

if [ -z "$TOKEN" ]; then
    echo -e "${RED}❌ Échec de l'authentification${NC}"
    echo "Réponse: $LOGIN_RESPONSE"
    exit 1
fi

echo -e "${GREEN}✅ Authentification réussie${NC}"
echo ""

# Demander l'ID du devis à tester
echo -e "${YELLOW}Test PDF Devis${NC}"
read -p "ID du devis à tester (ou laissez vide pour passer): " QUOTE_ID

if [ ! -z "$QUOTE_ID" ]; then
    echo "Génération du PDF du devis #${QUOTE_ID}..."
    
    PDF_RESPONSE=$(curl -s -w "\n%{http_code}" -X GET "${API_URL}/quotes/${QUOTE_ID}/pdf" \
      -H "Authorization: Bearer ${TOKEN}" \
      -o "test-devis-${QUOTE_ID}.pdf")
    
    HTTP_CODE=$(echo "$PDF_RESPONSE" | tail -n1)
    
    if [ "$HTTP_CODE" = "200" ]; then
        if [ -f "test-devis-${QUOTE_ID}.pdf" ] && [ -s "test-devis-${QUOTE_ID}.pdf" ]; then
            FILE_SIZE=$(stat -f%z "test-devis-${QUOTE_ID}.pdf" 2>/dev/null || stat -c%s "test-devis-${QUOTE_ID}.pdf" 2>/dev/null)
            echo -e "${GREEN}✅ PDF généré avec succès!${NC}"
            echo "   Fichier: test-devis-${QUOTE_ID}.pdf (${FILE_SIZE} bytes)"
        else
            echo -e "${RED}❌ Le fichier PDF n'a pas été créé ou est vide${NC}"
        fi
    else
        echo -e "${RED}❌ Erreur HTTP: ${HTTP_CODE}${NC}"
        echo "Réponse: $(echo "$PDF_RESPONSE" | head -n-1)"
    fi
    echo ""
else
    echo "Test du devis ignoré"
    echo ""
fi

# Demander l'ID de la facture à tester
echo -e "${YELLOW}Test PDF Facture${NC}"
read -p "ID de la facture à tester (ou laissez vide pour passer): " INVOICE_ID

if [ ! -z "$INVOICE_ID" ]; then
    echo "Génération du PDF de la facture #${INVOICE_ID}..."
    
    PDF_RESPONSE=$(curl -s -w "\n%{http_code}" -X GET "${API_URL}/invoices/${INVOICE_ID}/pdf" \
      -H "Authorization: Bearer ${TOKEN}" \
      -o "test-facture-${INVOICE_ID}.pdf")
    
    HTTP_CODE=$(echo "$PDF_RESPONSE" | tail -n1)
    
    if [ "$HTTP_CODE" = "200" ]; then
        if [ -f "test-facture-${INVOICE_ID}.pdf" ] && [ -s "test-facture-${INVOICE_ID}.pdf" ]; then
            FILE_SIZE=$(stat -f%z "test-facture-${INVOICE_ID}.pdf" 2>/dev/null || stat -c%s "test-facture-${INVOICE_ID}.pdf" 2>/dev/null)
            echo -e "${GREEN}✅ PDF généré avec succès!${NC}"
            echo "   Fichier: test-facture-${INVOICE_ID}.pdf (${FILE_SIZE} bytes)"
        else
            echo -e "${RED}❌ Le fichier PDF n'a pas été créé ou est vide${NC}"
        fi
    else
        echo -e "${RED}❌ Erreur HTTP: ${HTTP_CODE}${NC}"
        echo "Réponse: $(echo "$PDF_RESPONSE" | head -n-1)"
    fi
    echo ""
else
    echo "Test de la facture ignoré"
    echo ""
fi

echo "========================================="
echo -e "${GREEN}Tests terminés!${NC}"
echo "========================================="

