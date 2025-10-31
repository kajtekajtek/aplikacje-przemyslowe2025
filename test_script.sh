#!/bin/bash

# GET wszystkich
curl http://localhost:8080/api/employees -w "\n"

# POST nowy pracownik
curl -X POST http://localhost:8080/api/employees \
  -H "Content-Type: application/json" \
  -d '{"firstName":"Jan","lastName":"Kowalski","emailAddress":"jan@example.com","companyName":"TechCorp","role":"ENGINEER","salary":8000,"status":"ACTIVE"}' \
  -w "\n"

# GET statystyki
curl http://localhost:8080/api/statistics/company/TechCorp \
  -w "\n"

# PUT aktualizacja
curl -X PUT http://localhost:8080/api/employees/jan@example.com \
  -H "Content-Type: application/json" \
  -d '{"firstName":"Jan","lastName":"Kowalski","emailAddress":"jan@example.com","companyName":"TechCorp","role":"MANAGER","salary":12000,"status":"ACTIVE"}' \
  -w "\n"

# PATCH zmiana statusu
curl -X PATCH http://localhost:8080/api/employees/jan@example.com/status \
  -H "Content-Type: application/json" \
  -d '{"status":"ON_LEAVE"}' \
  -w "\n"

# DELETE
curl -X DELETE http://localhost:8080/api/employees/jan@example.com \
  -w "\n"

# GET statystyki
curl http://localhost:8080/api/statistics/company/TechCorp \
  -w "\n"