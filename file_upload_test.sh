#!/bin/bash

CSV_IN="data/sample_in/employees.csv"
CSV_OUT="data/sample_out/employees_export.csv"
PDF_IN="data/sample_in/document.pdf"
PDF_OUT="data/sample_out/company_statistics.pdf"
PHOTO_IN="data/sample_in/picture.jpg"
PHOTO_OUT="data/sample_out/picture.jpg"
EMPLOYEE_EMAIL="john.smith@csvinc.com"

echo "Upload pliku CSV:"

curl -X POST http://localhost:8080/api/files/import/csv \
  -F "file=@$CSV_IN"

echo "Download raportu CSV:"

curl http://localhost:8080/api/files/export/csv \
  --output $CSV_OUT

echo "Upload dokumentu pracownika:"

curl -X POST http://localhost:8080/api/files/documents/$EMPLOYEE_EMAIL \
  -F "file=@$PDF_IN" \
  -F "type=CONTRACT"

echo "Lista dokumentów pracownika:"

curl http://localhost:8080/api/files/documents/$EMPLOYEE_EMAIL

echo "Upload zdjęcia:"

curl -X POST http://localhost:8080/api/files/photos/$EMPLOYEE_EMAIL \
  -F "file=@$PHOTO_IN"

echo "Pobranie zdjęcia:"

curl http://localhost:8080/api/files/photos/$EMPLOYEE_EMAIL \
  --output $PHOTO_OUT
