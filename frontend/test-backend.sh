#!/bin/bash

# 🧪 Script de test rapide backend ERP-LITE
# Ce script vérifie que les endpoints principaux répondent correctement

# Colors
GREEN='\033[0;32m'
RED='\033[0;31m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
NC='\033[0m' # No Color

# Configuration
API_URL="http://localhost:8080/api/v1"
TEST_EMAIL="test@erp-lite.com"
TEST_PASSWORD="Test123!"

echo ""
echo -e "${BLUE}========================================${NC}"
echo -e "${BLUE}   🧪 TEST BACKEND ERP-LITE            ${NC}"
echo -e "${BLUE}========================================${NC}"
echo ""

# Function to test endpoint
test_endpoint() {
    local method=$1
    local endpoint=$2
    local description=$3
    local expected_status=$4
    local token=$5
    
    echo -n "Testing $description... "
    
    if [ -z "$token" ]; then
        response=$(curl -s -w "\n%{http_code}" -X $method "$API_URL$endpoint")
    else
        response=$(curl -s -w "\n%{http_code}" -X $method -H "Authorization: Bearer $token" "$API_URL$endpoint")
    fi
    
    http_code=$(echo "$response" | tail -n1)
    body=$(echo "$response" | sed '$d')
    
    if [ "$http_code" -eq "$expected_status" ]; then
        echo -e "${GREEN}✓ PASS${NC} (Status: $http_code)"
        return 0
    else
        echo -e "${RED}✗ FAIL${NC} (Expected: $expected_status, Got: $http_code)"
        echo -e "${YELLOW}Response: $body${NC}"
        return 1
    fi
}

# Counter
PASSED=0
FAILED=0

# 1. Test health check
echo -e "\n${BLUE}[1] Health Check${NC}"
if test_endpoint "GET" "/health" "Health" 200; then
    ((PASSED++))
else
    ((FAILED++))
fi

# 2. Test login
echo -e "\n${BLUE}[2] Authentication${NC}"
echo -n "Testing Login... "
login_response=$(curl -s -w "\n%{http_code}" -X POST "$API_URL/auth/login" \
    -H "Content-Type: application/json" \
    -d "{\"email\":\"$TEST_EMAIL\",\"password\":\"$TEST_PASSWORD\"}")

http_code=$(echo "$login_response" | tail -n1)
login_body=$(echo "$login_response" | sed '$d')

if [ "$http_code" -eq 200 ]; then
    TOKEN=$(echo "$login_body" | grep -o '"accessToken":"[^"]*"' | cut -d'"' -f4)
    if [ -z "$TOKEN" ]; then
        echo -e "${RED}✗ FAIL${NC} (No token in response)"
        ((FAILED++))
    else
        echo -e "${GREEN}✓ PASS${NC} (Token obtained)"
        ((PASSED++))
    fi
else
    echo -e "${RED}✗ FAIL${NC} (Status: $http_code)"
    echo -e "${YELLOW}Response: $login_body${NC}"
    ((FAILED++))
    echo ""
    echo -e "${RED}Cannot continue without authentication token${NC}"
    exit 1
fi

# 3. Test dashboard endpoints
echo -e "\n${BLUE}[3] Dashboard${NC}"
if test_endpoint "GET" "/dashboard/stats" "Dashboard Stats" 200 "$TOKEN"; then
    ((PASSED++))
else
    ((FAILED++))
fi

if test_endpoint "GET" "/dashboard/monthly-revenue" "Monthly Revenue" 200 "$TOKEN"; then
    ((PASSED++))
else
    ((FAILED++))
fi

if test_endpoint "GET" "/dashboard/top-clients" "Top Clients" 200 "$TOKEN"; then
    ((PASSED++))
else
    ((FAILED++))
fi

# 4. Test clients
echo -e "\n${BLUE}[4] Clients${NC}"
if test_endpoint "GET" "/clients?page=0&size=20" "Clients List" 200 "$TOKEN"; then
    ((PASSED++))
else
    ((FAILED++))
fi

# 5. Test quotes
echo -e "\n${BLUE}[5] Quotes${NC}"
if test_endpoint "GET" "/quotes?page=0&size=20" "Quotes List" 200 "$TOKEN"; then
    ((PASSED++))
else
    ((FAILED++))
fi

# 6. Test invoices
echo -e "\n${BLUE}[6] Invoices${NC}"
if test_endpoint "GET" "/invoices?page=0&size=20" "Invoices List" 200 "$TOKEN"; then
    ((PASSED++))
else
    ((FAILED++))
fi

# 7. Test interventions
echo -e "\n${BLUE}[7] Interventions${NC}"
if test_endpoint "GET" "/interventions?page=0&size=20" "Interventions List" 200 "$TOKEN"; then
    ((PASSED++))
else
    ((FAILED++))
fi

# 8. Test catalog
echo -e "\n${BLUE}[8] Catalog${NC}"
if test_endpoint "GET" "/catalog-items?page=0&size=20" "Catalog List" 200 "$TOKEN"; then
    ((PASSED++))
else
    ((FAILED++))
fi

# Results
echo ""
echo -e "${BLUE}========================================${NC}"
echo -e "${BLUE}   📊 RESULTS                          ${NC}"
echo -e "${BLUE}========================================${NC}"
echo ""
TOTAL=$((PASSED + FAILED))
echo -e "Total tests:  $TOTAL"
echo -e "${GREEN}Passed:       $PASSED${NC}"
echo -e "${RED}Failed:       $FAILED${NC}"
echo ""

SUCCESS_RATE=$(echo "scale=2; $PASSED * 100 / $TOTAL" | bc)
echo -e "Success rate: ${GREEN}${SUCCESS_RATE}%${NC}"
echo ""

if [ $FAILED -eq 0 ]; then
    echo -e "${GREEN}✓ All tests passed! Backend is ready.${NC}"
    echo ""
    exit 0
else
    echo -e "${RED}✗ Some tests failed. Check backend logs.${NC}"
    echo ""
    exit 1
fi

