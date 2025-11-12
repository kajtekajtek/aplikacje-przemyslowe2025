#!/bin/bash

RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
NC='\033[0m'

CSV_IN="data/sample_in/employees.csv"
XML_IN="data/sample_in/employees.xml"
CSV_OUT="data/sample_out/employees_export.csv"
PDF_IN="data/sample_in/document.pdf"
PDF_OUT="data/sample_out/company_statistics.pdf"
PDF_DOWNLOAD="data/sample_out/downloaded_document.pdf"
PHOTO_IN="data/sample_in/picture.jpg"
PHOTO_OUT="data/sample_out/picture.jpg"
EMPLOYEE_COMPANY="CSVINC"
EMPLOYEE_EMAIL="john.smith@csvinc.com"
BASE_URL="http://localhost:8080"
DOCUMENT_ID=""

echo -e "${BLUE}=== Sprawdzanie plików wejściowych ===${NC}"
for file in "$CSV_IN" "$XML_IN" "$PDF_IN" "$PHOTO_IN"; do
    if [ -f "$file" ]; then
        echo -e "${GREEN}✓${NC} Znaleziono: $file"
    else
        echo -e "${RED}✗${NC} Brak pliku: $file"
        exit 1
    fi
done

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

echo -e "\n${BLUE}=== 2. Upload pliku XML ===${NC}"
response=$(curl -s -w "\n%{http_code}" -X POST $BASE_URL/api/files/import/xml \
  -F "file=@$XML_IN")
http_code=$(echo "$response" | tail -n1)
body=$(echo "$response" | sed '$d')

if [ "$http_code" = "200" ]; then
    echo -e "${GREEN}✓ Sukces (HTTP $http_code)${NC}"
    echo "$body" | python3 -m json.tool 2>/dev/null || echo "$body"
else
    echo -e "${RED}✗ Błąd (HTTP $http_code)${NC}"
    echo "$body" | python3 -m json.tool 2>/dev/null || echo "$body"
fi

echo -e "\n${BLUE}=== 3. Download raportu CSV ===${NC}"
http_code=$(curl -s -w "%{http_code}" -o "$CSV_OUT" $BASE_URL/api/files/export/csv)
if [ "$http_code" = "200" ]; then
    echo -e "${GREEN}✓ Sukces (HTTP $http_code)${NC}"
    echo "Zapisano do: $CSV_OUT"
    echo "Liczba wierszy: $(wc -l < "$CSV_OUT")"
else
    echo -e "${RED}✗ Błąd (HTTP $http_code)${NC}"
fi

echo -e "\n${BLUE}=== 4. Upload dokumentu pracownika ===${NC}"
response=$(curl -s -w "\n%{http_code}" -X POST $BASE_URL/api/files/documents/$EMPLOYEE_EMAIL \
  -F "file=@$PDF_IN" \
  -F "type=CONTRACT")
http_code=$(echo "$response" | tail -n1)
body=$(echo "$response" | sed '$d')

if [ "$http_code" = "201" ]; then
    echo -e "${GREEN}✓ Sukces (HTTP $http_code)${NC}"
    echo "$body" | python3 -m json.tool 2>/dev/null || echo "$body"
    DOCUMENT_ID=$(echo "$body" | python3 -c "import sys, json; print(json.load(sys.stdin)['id'])" 2>/dev/null)
else
    echo -e "${RED}✗ Błąd (HTTP $http_code)${NC}"
    echo "$body" | python3 -m json.tool 2>/dev/null || echo "$body"
fi

echo -e "\n${BLUE}=== 5. Lista dokumentów pracownika ===${NC}"
response=$(curl -s -w "\n%{http_code}" $BASE_URL/api/files/documents/$EMPLOYEE_EMAIL)
http_code=$(echo "$response" | tail -n1)
body=$(echo "$response" | sed '$d')

if [ "$http_code" = "200" ]; then
    echo -e "${GREEN}✓ Sukces (HTTP $http_code)${NC}"
    echo "$body" | python3 -m json.tool 2>/dev/null || echo "$body"
    if [ -z "$DOCUMENT_ID" ]; then
        DOCUMENT_ID=$(echo "$body" | python3 -c "import sys, json; docs = json.load(sys.stdin); print(docs[0]['id'] if docs else '')" 2>/dev/null)
    fi
else
    echo -e "${RED}✗ Błąd (HTTP $http_code)${NC}"
    echo "$body" | python3 -m json.tool 2>/dev/null || echo "$body"
fi

echo -e "\n${BLUE}=== 6. Pobranie konkretnego dokumentu ===${NC}"
if [ -n "$DOCUMENT_ID" ]; then
    http_code=$(curl -s -w "%{http_code}" -o "$PDF_DOWNLOAD" $BASE_URL/api/files/documents/$EMPLOYEE_EMAIL/$DOCUMENT_ID)
    if [ "$http_code" = "200" ]; then
        echo -e "${GREEN}✓ Sukces (HTTP $http_code)${NC}"
        echo "Zapisano do: $PDF_DOWNLOAD"
        echo "Rozmiar: $(du -h "$PDF_DOWNLOAD" | cut -f1)"
    else
        echo -e "${RED}✗ Błąd (HTTP $http_code)${NC}"
    fi
else
    echo -e "${YELLOW}⚠ Pominięto - brak ID dokumentu${NC}"
fi

echo -e "\n${BLUE}=== 7. Usunięcie dokumentu ===${NC}"
if [ -n "$DOCUMENT_ID" ]; then
    http_code=$(curl -s -w "%{http_code}" -X DELETE $BASE_URL/api/files/documents/$EMPLOYEE_EMAIL/$DOCUMENT_ID -o /dev/null)
    if [ "$http_code" = "204" ]; then
        echo -e "${GREEN}✓ Sukces (HTTP $http_code)${NC}"
        echo "Dokument został usunięty"
    else
        echo -e "${RED}✗ Błąd (HTTP $http_code)${NC}"
    fi
else
    echo -e "${YELLOW}⚠ Pominięto - brak ID dokumentu${NC}"
fi

echo -e "\n${BLUE}=== 8. Upload zdjęcia ===${NC}"
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

echo -e "\n${BLUE}=== 9. Pobranie zdjęcia ===${NC}"
http_code=$(curl -s -w "%{http_code}" -o "$PHOTO_OUT" $BASE_URL/api/files/photos/$EMPLOYEE_EMAIL)
if [ "$http_code" = "200" ]; then
    echo -e "${GREEN}✓ Sukces (HTTP $http_code)${NC}"
    echo "Zapisano do: $PHOTO_OUT"
    echo "Rozmiar: $(du -h "$PHOTO_OUT" | cut -f1)"
else
    echo -e "${RED}✗ Błąd (HTTP $http_code)${NC}"
fi

echo -e "\n${BLUE}=== 10. Usunięcie zdjęcia ===${NC}"
http_code=$(curl -s -w "%{http_code}" -X DELETE $BASE_URL/api/files/photos/$EMPLOYEE_EMAIL -o /dev/null)
if [ "$http_code" = "204" ]; then
    echo -e "${GREEN}✓ Sukces (HTTP $http_code)${NC}"
    echo "Zdjęcie zostało usunięte"
else
    echo -e "${RED}✗ Błąd (HTTP $http_code)${NC}"
fi

echo -e "\n${BLUE}=== 11. Download raportu PDF ===${NC}"
http_code=$(curl -s -w "%{http_code}" -o "$PDF_OUT" $BASE_URL/api/files/reports/statistics/$EMPLOYEE_COMPANY)
if [ "$http_code" = "200" ]; then
    echo -e "${GREEN}✓ Sukces (HTTP $http_code)${NC}"
    echo "Zapisano do: $PDF_OUT"
    echo "Rozmiar: $(du -h "$PDF_OUT" | cut -f1)"
else
    echo -e "${RED}✗ Błąd (HTTP $http_code)${NC}"
fi

echo -e "\n${GREEN}=== Test zakończony ===${NC}"
echo -e "${BLUE}Wszystkie endpointy zostały przetestowane!${NC}"