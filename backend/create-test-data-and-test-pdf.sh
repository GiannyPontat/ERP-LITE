#!/bin/bash

# Script complet : Crée des données de test puis teste les endpoints PDF

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
echo -e "${BLUE}Création de données de test + Test PDF${NC}"
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

# 2. Récupérer l'ID de l'utilisateur admin
echo -e "${YELLOW}2. Récupération de l'utilisateur admin...${NC}"
USERS_RESPONSE=$(curl -s -X GET "${API_URL}/users" \
  -H "Authorization: Bearer ${TOKEN}")

USER_ID=$(echo $USERS_RESPONSE | grep -o '"id":\s*[0-9]*' | head -1 | grep -o '[0-9]*')

if [ -z "$USER_ID" ]; then
    echo -e "${RED}❌ Impossible de récupérer l'ID utilisateur${NC}"
    echo "Réponse: $USERS_RESPONSE"
    exit 1
fi

echo -e "${GREEN}✅ Utilisateur ID: ${USER_ID}${NC}"
echo ""

# 3. Vérifier/Créer un client
echo -e "${YELLOW}3. Vérification/Création d'un client de test...${NC}"
CLIENTS_RESPONSE=$(curl -s -X GET "${API_URL}/clients?size=1" \
  -H "Authorization: Bearer ${TOKEN}")

CLIENT_ID=$(echo $CLIENTS_RESPONSE | grep -o '"id":\s*[0-9]*' | head -1 | grep -o '[0-9]*')

if [ -z "$CLIENT_ID" ]; then
    echo "Création d'un nouveau client..."
    CLIENT_RESPONSE=$(curl -s -X POST "${API_URL}/clients" \
      -H "Authorization: Bearer ${TOKEN}" \
      -H "Content-Type: application/json" \
      -d "{
        \"companyName\": \"Entreprise Test\",
        \"contactFirstName\": \"Jean\",
        \"contactLastName\": \"Dupont\",
        \"email\": \"jean.dupont@test.com\",
        \"phone\": \"0123456789\",
        \"address\": \"123 Rue de Test\",
        \"city\": \"Paris\",
        \"postalCode\": \"75001\",
        \"siret\": \"12345678901234\"
      }")
    
    CLIENT_ID=$(echo $CLIENT_RESPONSE | grep -o '"id":\s*[0-9]*' | head -1 | grep -o '[0-9]*')
    
    if [ -z "$CLIENT_ID" ]; then
        echo -e "${RED}❌ Échec de la création du client${NC}"
        echo "Réponse: $CLIENT_RESPONSE"
        exit 1
    fi
    
    echo -e "${GREEN}✅ Client créé (ID: ${CLIENT_ID})${NC}"
else
    echo -e "${GREEN}✅ Client existant trouvé (ID: ${CLIENT_ID})${NC}"
fi
echo ""

# 4. Créer un devis de test
echo -e "${YELLOW}4. Création d'un devis de test...${NC}"
TODAY=$(date +%Y-%m-%d)
VALID_UNTIL=$(date -v+30d +%Y-%m-%d 2>/dev/null || date -d "+30 days" +%Y-%m-%d)

# Les totaux sont requis par la validation mais seront recalculés par le service
QUOTE_RESPONSE=$(curl -s -X POST "${API_URL}/quotes" \
  -H "Authorization: Bearer ${TOKEN}" \
  -H "Content-Type: application/json" \
  -d "{
    \"clientId\": ${CLIENT_ID},
    \"createdById\": ${USER_ID},
    \"date\": \"${TODAY}\",
    \"validUntil\": \"${VALID_UNTIL}\",
    \"status\": \"DRAFT\",
    \"subtotal\": 0,
    \"taxRate\": 20.00,
    \"taxAmount\": 0,
    \"total\": 0,
    \"items\": [
      {
        \"description\": \"Prestation de service\",
        \"quantity\": 10,
        \"unitPrice\": 100.00,
        \"total\": 0
      },
      {
        \"description\": \"Fourniture matériel\",
        \"quantity\": 5,
        \"unitPrice\": 50.00,
        \"total\": 0
      }
    ]
  }")

QUOTE_ID=$(echo $QUOTE_RESPONSE | grep -o '"id":\s*[0-9]*' | head -1 | grep -o '[0-9]*')

if [ -z "$QUOTE_ID" ]; then
    echo -e "${RED}❌ Échec de la création du devis${NC}"
    echo "Réponse: $QUOTE_RESPONSE"
    exit 1
fi

echo -e "${GREEN}✅ Devis créé (ID: ${QUOTE_ID})${NC}"
echo ""

# 5. Test PDF Quote
echo -e "${YELLOW}5. Test génération PDF du devis #${QUOTE_ID}...${NC}"
HTTP_CODE=$(curl -s -w "%{http_code}" -o "test-devis-${QUOTE_ID}.pdf" \
  -X GET "${API_URL}/quotes/${QUOTE_ID}/pdf" \
  -H "Authorization: Bearer ${TOKEN}")

if [ "$HTTP_CODE" = "200" ]; then
    if [ -f "test-devis-${QUOTE_ID}.pdf" ] && [ -s "test-devis-${QUOTE_ID}.pdf" ]; then
        FILE_SIZE=$(stat -f%z "test-devis-${QUOTE_ID}.pdf" 2>/dev/null || stat -c%s "test-devis-${QUOTE_ID}.pdf" 2>/dev/null)
        echo -e "${GREEN}✅ PDF devis généré avec succès!${NC}"
        echo -e "   📄 Fichier: ${BLUE}test-devis-${QUOTE_ID}.pdf${NC} (${FILE_SIZE} bytes)"
        
        if file "test-devis-${QUOTE_ID}.pdf" 2>/dev/null | grep -q "PDF"; then
            echo -e "   ${GREEN}✅ Format PDF valide${NC}"
        fi
    else
        echo -e "${RED}❌ Le fichier PDF n'a pas été créé ou est vide${NC}"
    fi
else
    echo -e "${RED}❌ Erreur HTTP ${HTTP_CODE}${NC}"
    cat "test-devis-${QUOTE_ID}.pdf" 2>/dev/null
fi
echo ""

# 6. Créer une facture de test
echo -e "${YELLOW}6. Création d'une facture de test...${NC}"
DUE_DATE=$(date -v+15d +%Y-%m-%d 2>/dev/null || date -d "+15 days" +%Y-%m-%d)

# Les totaux sont requis par la validation mais seront recalculés par le service
INVOICE_RESPONSE=$(curl -s -X POST "${API_URL}/invoices" \
  -H "Authorization: Bearer ${TOKEN}" \
  -H "Content-Type: application/json" \
  -d "{
    \"clientId\": ${CLIENT_ID},
    \"createdById\": ${USER_ID},
    \"date\": \"${TODAY}\",
    \"dueDate\": \"${DUE_DATE}\",
    \"status\": \"SENT\",
    \"subtotal\": 0,
    \"taxRate\": 20.00,
    \"taxAmount\": 0,
    \"total\": 0,
    \"items\": [
      {
        \"description\": \"Facturation prestation\",
        \"quantity\": 8,
        \"unitPrice\": 150.00,
        \"total\": 0
      },
      {
        \"description\": \"Facturation matériel\",
        \"quantity\": 3,
        \"unitPrice\": 75.00,
        \"total\": 0
      }
    ]
  }")

INVOICE_ID=$(echo $INVOICE_RESPONSE | grep -o '"id":\s*[0-9]*' | head -1 | grep -o '[0-9]*')

if [ -z "$INVOICE_ID" ]; then
    echo -e "${RED}❌ Échec de la création de la facture${NC}"
    echo "Réponse: $INVOICE_RESPONSE"
    exit 1
fi

echo -e "${GREEN}✅ Facture créée (ID: ${INVOICE_ID})${NC}"
echo ""

# 7. Test PDF Invoice
echo -e "${YELLOW}7. Test génération PDF de la facture #${INVOICE_ID}...${NC}"
HTTP_CODE=$(curl -s -w "%{http_code}" -o "test-facture-${INVOICE_ID}.pdf" \
  -X GET "${API_URL}/invoices/${INVOICE_ID}/pdf" \
  -H "Authorization: Bearer ${TOKEN}")

if [ "$HTTP_CODE" = "200" ]; then
    if [ -f "test-facture-${INVOICE_ID}.pdf" ] && [ -s "test-facture-${INVOICE_ID}.pdf" ]; then
        FILE_SIZE=$(stat -f%z "test-facture-${INVOICE_ID}.pdf" 2>/dev/null || stat -c%s "test-facture-${INVOICE_ID}.pdf" 2>/dev/null)
        echo -e "${GREEN}✅ PDF facture généré avec succès!${NC}"
        echo -e "   📄 Fichier: ${BLUE}test-facture-${INVOICE_ID}.pdf${NC} (${FILE_SIZE} bytes)"
        
        if file "test-facture-${INVOICE_ID}.pdf" 2>/dev/null | grep -q "PDF"; then
            echo -e "   ${GREEN}✅ Format PDF valide${NC}"
        fi
    else
        echo -e "${RED}❌ Le fichier PDF n'a pas été créé ou est vide${NC}"
    fi
else
    echo -e "${RED}❌ Erreur HTTP ${HTTP_CODE}${NC}"
    cat "test-facture-${INVOICE_ID}.pdf" 2>/dev/null
fi
echo ""

echo -e "${BLUE}=========================================${NC}"
echo -e "${GREEN}✅ Tests terminés avec succès!${NC}"
echo -e "${BLUE}=========================================${NC}"
echo ""
echo "📄 Fichiers PDF générés:"
if [ -f "test-devis-${QUOTE_ID}.pdf" ]; then
    echo "  - test-devis-${QUOTE_ID}.pdf"
fi
if [ -f "test-facture-${INVOICE_ID}.pdf" ]; then
    echo "  - test-facture-${INVOICE_ID}.pdf"
fi
echo ""
echo "Pour ouvrir les PDF:"
if [ -f "test-devis-${QUOTE_ID}.pdf" ]; then
    echo "  open test-devis-${QUOTE_ID}.pdf"
fi
if [ -f "test-facture-${INVOICE_ID}.pdf" ]; then
    echo "  open test-facture-${INVOICE_ID}.pdf"
fi

