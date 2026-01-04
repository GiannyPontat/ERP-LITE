#!/bin/bash

# Script de test simplifié pour les endpoints PDF
# Usage: ./test-pdf-endpoints-simple.sh <email> <password> <quote_id> <invoice_id>

BASE_URL="http://localhost:8080"
API_URL="${BASE_URL}/api/v1"

GREEN='\033[0;32m'
RED='\033[0;31m'
YELLOW='\033[1;33m'
NC='\033[0m'

if [ $# -lt 2 ]; then
    echo "Usage: $0 <email> <password> [quote_id] [invoice_id]"
    echo "Exemple: $0 admin@example.com password123 1 1"
    exit 1
fi

EMAIL=$1
PASSWORD=$2
QUOTE_ID=${3:-""}
INVOICE_ID=${4:-""}

echo "========================================="
echo "Test des Endpoints PDF"
echo "========================================="
echo ""

# Connexion
echo -e "${YELLOW}Authentification...${NC}"
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

# Test Quote PDF
if [ ! -z "$QUOTE_ID" ]; then
    echo -e "${YELLOW}Test PDF Devis #${QUOTE_ID}...${NC}"
    HTTP_CODE=$(curl -s -w "%{http_code}" -o "test-devis-${QUOTE_ID}.pdf" \
      -X GET "${API_URL}/quotes/${QUOTE_ID}/pdf" \
      -H "Authorization: Bearer ${TOKEN}")
    
    if [ "$HTTP_CODE" = "200" ]; then
        if [ -f "test-devis-${QUOTE_ID}.pdf" ] && [ -s "test-devis-${QUOTE_ID}.pdf" ]; then
            FILE_SIZE=$(stat -f%z "test-devis-${QUOTE_ID}.pdf" 2>/dev/null || stat -c%s "test-devis-${QUOTE_ID}.pdf" 2>/dev/null)
            echo -e "${GREEN}✅ PDF devis généré: test-devis-${QUOTE_ID}.pdf (${FILE_SIZE} bytes)${NC}"
        else
            echo -e "${RED}❌ Fichier PDF vide ou non créé${NC}"
        fi
    else
        echo -e "${RED}❌ Erreur HTTP ${HTTP_CODE}${NC}"
    fi
    echo ""
fi

# Test Invoice PDF
if [ ! -z "$INVOICE_ID" ]; then
    echo -e "${YELLOW}Test PDF Facture #${INVOICE_ID}...${NC}"
    HTTP_CODE=$(curl -s -w "%{http_code}" -o "test-facture-${INVOICE_ID}.pdf" \
      -X GET "${API_URL}/invoices/${INVOICE_ID}/pdf" \
      -H "Authorization: Bearer ${TOKEN}")
    
    if [ "$HTTP_CODE" = "200" ]; then
        if [ -f "test-facture-${INVOICE_ID}.pdf" ] && [ -s "test-facture-${INVOICE_ID}.pdf" ]; then
            FILE_SIZE=$(stat -f%z "test-facture-${INVOICE_ID}.pdf" 2>/dev/null || stat -c%s "test-facture-${INVOICE_ID}.pdf" 2>/dev/null)
            echo -e "${GREEN}✅ PDF facture généré: test-facture-${INVOICE_ID}.pdf (${FILE_SIZE} bytes)${NC}"
        else
            echo -e "${RED}❌ Fichier PDF vide ou non créé${NC}"
        fi
    else
        echo -e "${RED}❌ Erreur HTTP ${HTTP_CODE}${NC}"
    fi
    echo ""
fi

echo "========================================="
echo -e "${GREEN}Tests terminés!${NC}"
echo "========================================="

