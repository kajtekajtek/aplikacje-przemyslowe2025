#!/bin/bash

# File Upload Test Script for Employee Management System
# This script demonstrates the File Upload API endpoints

BASE_URL="http://localhost:8080/api/files"

echo "=================================="
echo "File Upload API Test Script"
echo "=================================="
echo ""

# Colors for output
GREEN='\033[0;32m'
RED='\033[0;31m'
YELLOW='\033[1;33m'
NC='\033[0m' # No Color

# Function to print section headers
print_section() {
    echo ""
    echo -e "${YELLOW}========================================${NC}"
    echo -e "${YELLOW}$1${NC}"
    echo -e "${YELLOW}========================================${NC}"
}

# Function to print success
print_success() {
    echo -e "${GREEN}✓ $1${NC}"
}

# Function to print error
print_error() {
    echo -e "${RED}✗ $1${NC}"
}

# Check if server is running
echo "Checking if server is running..."
if ! curl -s "$BASE_URL/../employees" > /dev/null 2>&1; then
    print_error "Server is not running on $BASE_URL"
    echo "Please start the application first: mvn spring-boot:run"
    exit 1
fi
print_success "Server is running"

# ===========================================
# 1. CSV Import
# ===========================================
print_section "1. Upload CSV File"

if [ -f "sample_employees.csv" ]; then
    echo "Uploading sample_employees.csv..."
    RESPONSE=$(curl -s -X POST "$BASE_URL/import/csv" \
        -F "file=@sample_employees.csv" \
        -w "\n%{http_code}")
    
    HTTP_CODE=$(echo "$RESPONSE" | tail -n1)
    BODY=$(echo "$RESPONSE" | sed '$d')
    
    if [ "$HTTP_CODE" = "200" ]; then
        print_success "CSV uploaded successfully"
        echo "Response: $BODY"
    else
        print_error "CSV upload failed (HTTP $HTTP_CODE)"
        echo "Response: $BODY"
    fi
else
    print_error "sample_employees.csv not found"
fi

# ===========================================
# 2. XML Import
# ===========================================
print_section "2. Upload XML File"

if [ -f "sample_employees.xml" ]; then
    echo "Uploading sample_employees.xml..."
    RESPONSE=$(curl -s -X POST "$BASE_URL/import/xml" \
        -F "file=@sample_employees.xml" \
        -w "\n%{http_code}")
    
    HTTP_CODE=$(echo "$RESPONSE" | tail -n1)
    BODY=$(echo "$RESPONSE" | sed '$d')
    
    if [ "$HTTP_CODE" = "200" ]; then
        print_success "XML uploaded successfully"
        echo "Response: $BODY"
    else
        print_error "XML upload failed (HTTP $HTTP_CODE)"
        echo "Response: $BODY"
    fi
else
    print_error "sample_employees.xml not found"
fi

# ===========================================
# 3. Export CSV Report
# ===========================================
print_section "3. Download CSV Report"

echo "Downloading all employees as CSV..."
curl -s -X GET "$BASE_URL/export/csv" \
    -o "employees_export.csv"

if [ -f "employees_export.csv" ]; then
    print_success "CSV report downloaded to employees_export.csv"
    echo "First 5 lines:"
    head -n 5 "employees_export.csv"
else
    print_error "Failed to download CSV report"
fi

echo ""
echo "Downloading CSV report for TechCorp..."
curl -s -X GET "$BASE_URL/export/csv?companyName=TechCorp" \
    -o "techcorp_export.csv"

if [ -f "techcorp_export.csv" ]; then
    print_success "TechCorp CSV report downloaded to techcorp_export.csv"
    echo "First 5 lines:"
    head -n 5 "techcorp_export.csv"
else
    print_error "Failed to download TechCorp CSV report"
fi

# ===========================================
# 4. PDF Statistics Report
# ===========================================
print_section "4. Download PDF Statistics Report"

echo "Downloading PDF statistics report for TechCorp..."
curl -s -X GET "$BASE_URL/reports/statistics/TechCorp" \
    -o "techcorp_statistics.pdf"

if [ -f "techcorp_statistics.pdf" ]; then
    FILE_SIZE=$(wc -c < "techcorp_statistics.pdf")
    if [ "$FILE_SIZE" -gt 0 ]; then
        print_success "PDF statistics report downloaded to techcorp_statistics.pdf (${FILE_SIZE} bytes)"
    else
        print_error "PDF file is empty"
    fi
else
    print_error "Failed to download PDF statistics report"
fi

# ===========================================
# 5. Upload Employee Photo
# ===========================================
print_section "5. Upload Employee Photo"

# Use existing employee from XMLCorp
EMPLOYEE_EMAIL="thomas.anderson@xmlcorp.com"

# Create a sample photo (1x1 PNG)
echo "Creating sample photo..."
echo -n -e '\x89\x50\x4e\x47\x0d\x0a\x1a\x0a\x00\x00\x00\x0d\x49\x48\x44\x52\x00\x00\x00\x01\x00\x00\x00\x01\x08\x06\x00\x00\x00\x1f\x15\xc4\x89\x00\x00\x00\x0a\x49\x44\x41\x54\x78\x9c\x63\x00\x01\x00\x00\x05\x00\x01\x0d\x0a\x2d\xb4\x00\x00\x00\x00\x49\x45\x4e\x44\xae\x42\x60\x82' > sample_photo.png

if [ -f "sample_photo.png" ]; then
    echo "Uploading photo for $EMPLOYEE_EMAIL..."
    RESPONSE=$(curl -s -X POST "$BASE_URL/photos/$EMPLOYEE_EMAIL" \
        -F "file=@sample_photo.png" \
        -w "\n%{http_code}")
    
    HTTP_CODE=$(echo "$RESPONSE" | tail -n1)
    BODY=$(echo "$RESPONSE" | sed '$d')
    
    if [ "$HTTP_CODE" = "200" ]; then
        print_success "Photo uploaded successfully"
        echo "Response: $BODY" | head -c 200
        echo "..."
    else
        print_error "Photo upload failed (HTTP $HTTP_CODE)"
        echo "Response: $BODY"
    fi
else
    print_error "Failed to create sample photo"
fi

# ===========================================
# 6. Download Employee Photo
# ===========================================
print_section "6. Download Employee Photo"

echo "Downloading photo for $EMPLOYEE_EMAIL..."
HTTP_CODE=$(curl -s -X GET "$BASE_URL/photos/$EMPLOYEE_EMAIL" \
    -o "downloaded_photo.png" \
    -w "%{http_code}")

if [ "$HTTP_CODE" = "200" ]; then
    if [ -f "downloaded_photo.png" ]; then
        FILE_SIZE=$(wc -c < "downloaded_photo.png")
        print_success "Photo downloaded to downloaded_photo.png (${FILE_SIZE} bytes)"
    else
        print_error "Downloaded file not found"
    fi
else
    print_error "Photo download failed (HTTP $HTTP_CODE)"
fi

# ===========================================
# 7. Upload Employee Document
# ===========================================
print_section "7. Upload Employee Document"

# Create a sample PDF-like document
echo "Creating sample document..."
echo "%PDF-1.4
Sample Contract Document
This is a test document for $EMPLOYEE_EMAIL" > sample_contract.pdf

if [ -f "sample_contract.pdf" ]; then
    echo "Uploading contract for $EMPLOYEE_EMAIL..."
    RESPONSE=$(curl -s -X POST "$BASE_URL/documents/$EMPLOYEE_EMAIL?type=CONTRACT" \
        -F "file=@sample_contract.pdf" \
        -w "\n%{http_code}")
    
    HTTP_CODE=$(echo "$RESPONSE" | tail -n1)
    BODY=$(echo "$RESPONSE" | sed '$d')
    
    if [ "$HTTP_CODE" = "201" ]; then
        print_success "Document uploaded successfully"
        echo "Response: $BODY" | head -c 200
        echo "..."
        
        # Extract document ID from response
        DOCUMENT_ID=$(echo "$BODY" | grep -o '"id":"[^"]*"' | sed 's/"id":"\(.*\)"/\1/')
        echo "Document ID: $DOCUMENT_ID"
    else
        print_error "Document upload failed (HTTP $HTTP_CODE)"
        echo "Response: $BODY"
    fi
else
    print_error "Failed to create sample document"
fi

# ===========================================
# 8. Get Employee Documents
# ===========================================
print_section "8. Get Employee Documents"

echo "Getting documents for $EMPLOYEE_EMAIL..."
RESPONSE=$(curl -s -X GET "$BASE_URL/documents/$EMPLOYEE_EMAIL" \
    -w "\n%{http_code}")

HTTP_CODE=$(echo "$RESPONSE" | tail -n1)
BODY=$(echo "$RESPONSE" | sed '$d')

if [ "$HTTP_CODE" = "200" ]; then
    print_success "Documents retrieved successfully"
    echo "Response: $BODY" | head -c 200
    echo "..."
else
    print_error "Failed to get documents (HTTP $HTTP_CODE)"
    echo "Response: $BODY"
fi

# ===========================================
# Summary
# ===========================================
print_section "Test Summary"

echo "All tests completed!"
echo ""
echo "Generated files:"
ls -lh employees_export.csv techcorp_export.csv techcorp_statistics.pdf downloaded_photo.png sample_photo.png sample_contract.pdf 2>/dev/null | awk '{print "  - " $9 " (" $5 ")"}'

echo ""
echo "To clean up test files, run:"
echo "  rm -f employees_export.csv techcorp_export.csv techcorp_statistics.pdf downloaded_photo.png sample_photo.png sample_contract.pdf"

