#!/bin/bash

# Test rapide des endpoints PDF
# Utilise les credentials par défaut : admin@erplite.com / Admin@123

BASE_URL="http://localhost:8080"
API_URL="${BASE_URL}/api/v1"

EMAIL="admin@erplite.com"
PASSWORD="Admin@123"

GREEN='\033[0;32m'
RED='\033[0;31m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
NC='\033[0m'

echo -e "${BLUE}=========================================${NC}"
echo -e "${BLUE}Test des Endpoints PDF${NC}"
echo -e "${BLUE}=========================================${NC}"
echo ""

# 1. Authentification
echo -e "${YELLOW}1. Authentification...${NC}"
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

# 2. Lister les devis pour obtenir un ID
echo -e "${YELLOW}2. Récupération de la liste des devis...${NC}"
QUOTES_RESPONSE=$(curl -s -X GET "${API_URL}/quotes" \
  -H "Authorization: Bearer ${TOKEN}")

QUOTE_ID=$(echo $QUOTES_RESPONSE | grep -o '"id":\s*[0-9]*' | head -1 | grep -o '[0-9]*')

if [ -z "$QUOTE_ID" ]; then
    echo -e "${YELLOW}⚠️  Aucun devis trouvé. Créez un devis d'abord.${NC}"
else
    echo -e "${GREEN}✅ Devis trouvé (ID: ${QUOTE_ID})${NC}"
    echo ""
    
    # 3. Test PDF Quote
    echo -e "${YELLOW}3. Test génération PDF du devis #${QUOTE_ID}...${NC}"
    HTTP_CODE=$(curl -s -w "%{http_code}" -o "test-devis-${QUOTE_ID}.pdf" \
      -X GET "${API_URL}/quotes/${QUOTE_ID}/pdf" \
      -H "Authorization: Bearer ${TOKEN}")
    
    if [ "$HTTP_CODE" = "200" ]; then
        if [ -f "test-devis-${QUOTE_ID}.pdf" ] && [ -s "test-devis-${QUOTE_ID}.pdf" ]; then
            FILE_SIZE=$(stat -f%z "test-devis-${QUOTE_ID}.pdf" 2>/dev/null || stat -c%s "test-devis-${QUOTE_ID}.pdf" 2>/dev/null)
            echo -e "${GREEN}✅ PDF devis généré avec succès!${NC}"
            echo -e "   📄 Fichier: ${BLUE}test-devis-${QUOTE_ID}.pdf${NC} (${FILE_SIZE} bytes)"
            
            # Vérifier que c'est bien un PDF
            if file "test-devis-${QUOTE_ID}.pdf" 2>/dev/null | grep -q "PDF"; then
                echo -e "   ${GREEN}✅ Format PDF valide${NC}"
            fi
        else
            echo -e "${RED}❌ Le fichier PDF n'a pas été créé ou est vide${NC}"
        fi
    else
        echo -e "${RED}❌ Erreur HTTP ${HTTP_CODE}${NC}"
        if [ "$HTTP_CODE" = "404" ]; then
            echo "   Le devis #${QUOTE_ID} n'existe pas"
        elif [ "$HTTP_CODE" = "500" ]; then
            echo "   Erreur serveur - vérifiez les logs"
        fi
    fi
    echo ""
fi

# 4. Lister les factures pour obtenir un ID
echo -e "${YELLOW}4. Récupération de la liste des factures...${NC}"
INVOICES_RESPONSE=$(curl -s -X GET "${API_URL}/invoices" \
  -H "Authorization: Bearer ${TOKEN}")

INVOICE_ID=$(echo $INVOICES_RESPONSE | grep -o '"id":\s*[0-9]*' | head -1 | grep -o '[0-9]*')

if [ -z "$INVOICE_ID" ]; then
    echo -e "${YELLOW}⚠️  Aucune facture trouvée. Créez une facture d'abord.${NC}"
else
    echo -e "${GREEN}✅ Facture trouvée (ID: ${INVOICE_ID})${NC}"
    echo ""
    
    # 5. Test PDF Invoice
    echo -e "${YELLOW}5. Test génération PDF de la facture #${INVOICE_ID}...${NC}"
    HTTP_CODE=$(curl -s -w "%{http_code}" -o "test-facture-${INVOICE_ID}.pdf" \
      -X GET "${API_URL}/invoices/${INVOICE_ID}/pdf" \
      -H "Authorization: Bearer ${TOKEN}")
    
    if [ "$HTTP_CODE" = "200" ]; then
        if [ -f "test-facture-${INVOICE_ID}.pdf" ] && [ -s "test-facture-${INVOICE_ID}.pdf" ]; then
            FILE_SIZE=$(stat -f%z "test-facture-${INVOICE_ID}.pdf" 2>/dev/null || stat -c%s "test-facture-${INVOICE_ID}.pdf" 2>/dev/null)
            echo -e "${GREEN}✅ PDF facture généré avec succès!${NC}"
            echo -e "   📄 Fichier: ${BLUE}test-facture-${INVOICE_ID}.pdf${NC} (${FILE_SIZE} bytes)"
            
            # Vérifier que c'est bien un PDF
            if file "test-facture-${INVOICE_ID}.pdf" 2>/dev/null | grep -q "PDF"; then
                echo -e "   ${GREEN}✅ Format PDF valide${NC}"
            fi
        else
            echo -e "${RED}❌ Le fichier PDF n'a pas été créé ou est vide${NC}"
        fi
    else
        echo -e "${RED}❌ Erreur HTTP ${HTTP_CODE}${NC}"
        if [ "$HTTP_CODE" = "404" ]; then
            echo "   La facture #${INVOICE_ID} n'existe pas"
        elif [ "$HTTP_CODE" = "500" ]; then
            echo "   Erreur serveur - vérifiez les logs"
        fi
    fi
    echo ""
fi

echo -e "${BLUE}=========================================${NC}"
echo -e "${GREEN}Tests terminés!${NC}"
echo -e "${BLUE}=========================================${NC}"
echo ""
echo "Pour ouvrir les PDF générés:"
if [ ! -z "$QUOTE_ID" ] && [ -f "test-devis-${QUOTE_ID}.pdf" ]; then
    echo "  open test-devis-${QUOTE_ID}.pdf"
fi
if [ ! -z "$INVOICE_ID" ] && [ -f "test-facture-${INVOICE_ID}.pdf" ]; then
    echo "  open test-facture-${INVOICE_ID}.pdf"
fi

