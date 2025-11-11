#!/bin/bash

# Kolory dla lepszej czytelności
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
NC='\033[0m' # No Color

CSV_IN="data/sample_in/employees.csv"
CSV_OUT="data/sample_out/employees_export.csv"
PDF_IN="data/sample_in/document.pdf"
PDF_OUT="data/sample_out/company_statistics.pdf"
PHOTO_IN="data/sample_in/picture.jpg"
PHOTO_OUT="data/sample_out/picture.jpg"
EMPLOYEE_COMPANY="CSVINC"
EMPLOYEE_EMAIL="john.smith@csvinc.com"
BASE_URL="http://localhost:8080"

# Sprawdź czy pliki wejściowe istnieją
echo -e "${BLUE}=== Sprawdzanie plików wejściowych ===${NC}"
for file in "$CSV_IN" "$PDF_IN" "$PHOTO_IN"; do
    if [ -f "$file" ]; then
        echo -e "${GREEN}✓${NC} Znaleziono: $file"
    else
        echo -e "${RED}✗${NC} Brak pliku: $file"
        exit 1
    fi
done

# Utwórz katalog wyjściowy jeśli nie istnieje
mkdir -p data/sample_out

echo -e "\n${BLUE}=== 1. Upload pliku CSV ===${NC}"
response=$(curl -s -w "\n%{http_code}" -X POST $BASE_URL/api/files/import/csv \
  -F "file=@$CSV_IN")
http_code=$(echo "$response" | tail -n1)
body=$(echo "$response" | sed '$d')

if [ "$http_code" = "200" ]; then
    echo -e "${GREEN}✓ Sukces (HTTP $http_code)${NC}"
    echo "$body" | python3 -m json.tool 2>/dev/null || echo "$body"
else
    echo -e "${RED}✗ Błąd (HTTP $http_code)${NC}"
    echo "$body" | python3 -m json.tool 2>/dev/null || echo "$body"
fi

echo -e "\n${BLUE}=== 2. Download raportu CSV ===${NC}"
http_code=$(curl -s -w "%{http_code}" -o "$CSV_OUT" $BASE_URL/api/files/export/csv)
if [ "$http_code" = "200" ]; then
    echo -e "${GREEN}✓ Sukces (HTTP $http_code)${NC}"
    echo "Zapisano do: $CSV_OUT"
    echo "Liczba wierszy: $(wc -l < "$CSV_OUT")"
else
    echo -e "${RED}✗ Błąd (HTTP $http_code)${NC}"
fi

echo -e "\n${BLUE}=== 3. Upload dokumentu pracownika ===${NC}"
response=$(curl -s -w "\n%{http_code}" -X POST $BASE_URL/api/files/documents/$EMPLOYEE_EMAIL \
  -F "file=@$PDF_IN" \
  -F "type=CONTRACT")
http_code=$(echo "$response" | tail -n1)
body=$(echo "$response" | sed '$d')

if [ "$http_code" = "201" ]; then
    echo -e "${GREEN}✓ Sukces (HTTP $http_code)${NC}"
    echo "$body" | python3 -m json.tool 2>/dev/null || echo "$body"
else
    echo -e "${RED}✗ Błąd (HTTP $http_code)${NC}"
    echo "$body" | python3 -m json.tool 2>/dev/null || echo "$body"
fi

echo -e "\n${BLUE}=== 4. Lista dokumentów pracownika ===${NC}"
response=$(curl -s -w "\n%{http_code}" $BASE_URL/api/files/documents/$EMPLOYEE_EMAIL)
http_code=$(echo "$response" | tail -n1)
body=$(echo "$response" | sed '$d')

if [ "$http_code" = "200" ]; then
    echo -e "${GREEN}✓ Sukces (HTTP $http_code)${NC}"
    echo "$body" | python3 -m json.tool 2>/dev/null || echo "$body"
else
    echo -e "${RED}✗ Błąd (HTTP $http_code)${NC}"
    echo "$body" | python3 -m json.tool 2>/dev/null || echo "$body"
fi

echo -e "\n${BLUE}=== 5. Upload zdjęcia ===${NC}"
response=$(curl -s -w "\n%{http_code}" -X POST $BASE_URL/api/files/photos/$EMPLOYEE_EMAIL \
  -F "file=@$PHOTO_IN")
http_code=$(echo "$response" | tail -n1)
body=$(echo "$response" | sed '$d')

if [ "$http_code" = "200" ]; then
    echo -e "${GREEN}✓ Sukces (HTTP $http_code)${NC}"
    echo "$body" | python3 -m json.tool 2>/dev/null || echo "$body"
else
    echo -e "${RED}✗ Błąd (HTTP $http_code)${NC}"
    echo "$body" | python3 -m json.tool 2>/dev/null || echo "$body"
fi

echo -e "\n${BLUE}=== 6. Pobranie zdjęcia ===${NC}"
http_code=$(curl -s -w "%{http_code}" -o "$PHOTO_OUT" $BASE_URL/api/files/photos/$EMPLOYEE_EMAIL)
if [ "$http_code" = "200" ]; then
    echo -e "${GREEN}✓ Sukces (HTTP $http_code)${NC}"
    echo "Zapisano do: $PHOTO_OUT"
    echo "Rozmiar: $(du -h "$PHOTO_OUT" | cut -f1)"
else
    echo -e "${RED}✗ Błąd (HTTP $http_code)${NC}"
fi

echo -e "\n${BLUE}=== 7. Download raportu PDF ===${NC}"
http_code=$(curl -s -w "%{http_code}" -o "$PDF_OUT" $BASE_URL/api/files/reports/statistics/$EMPLOYEE_COMPANY)
if [ "$http_code" = "200" ]; then
    echo -e "${GREEN}✓ Sukces (HTTP $http_code)${NC}"
    echo "Zapisano do: $PDF_OUT"
    echo "Rozmiar: $(du -h "$PDF_OUT" | cut -f1)"
else
    echo -e "${RED}✗ Błąd (HTTP $http_code)${NC}"
fi

echo -e "\n${GREEN}=== Test zakończony ===${NC}"