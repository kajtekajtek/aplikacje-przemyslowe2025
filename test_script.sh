#!/bin/bash

echo -e "\nGET wszystkich"
echo -e "curl http://localhost:8080/api/employees\n"

curl http://localhost:8080/api/employees -w "\n"

echo -e "\nPOST nowy pracownik"
echo -e "curl -X POST http://localhost:8080/api/employees\n"

curl -X POST http://localhost:8080/api/employees \
  -H "Content-Type: application/json" \
  -d '{"firstName":"Grzegorz","lastName":"Kamieniec","emailAddress":"grzegorz@example.com","companyName":"TechCorp","role":"ENGINEER","salary":8000,"status":"ACTIVE"}' \
  -w "\n"

echo -e "\nGET statystyki"
echo -e "curl http://localhost:8080/api/statistics/company/TechCorp\n"

curl http://localhost:8080/api/statistics/company/TechCorp \
  -w "\n"

echo -e "\nPUT aktualizacja"
echo -e "curl -X PUT http://localhost:8080/api/employees/grzegorz@example.com\n"

curl -X PUT http://localhost:8080/api/employees/grzegorz@example.com \
  -H "Content-Type: application/json" \
  -d '{"firstName":"Grzegorz","lastName":"Kamieniec","emailAddress":"grzegorz@example.com","companyName":"TechCorp","role":"MANAGER","salary":12000,"status":"ACTIVE"}' \
  -w "\n"

echo -e "\nPATCH zmiana statusu"
echo -e "curl -X PATCH http://localhost:8080/api/employees/grzegorz@example.com/status\n"

curl -X PATCH http://localhost:8080/api/employees/grzegorz@example.com/status \
  -H "Content-Type: application/json" \
  -d '{"status":"ON_LEAVE"}' \
  -w "\n"

echo -e "\nDELETE"
echo -e "curl -X DELETE http://localhost:8080/api/employees/grzegorz@example.com\n"

curl -X DELETE http://localhost:8080/api/employees/grzegorz@example.com \
  -w "\n"

echo -e "\nGET statystyki"
echo -e "curl http://localhost:8080/api/statistics/company/TechCorp\n"

curl http://localhost:8080/api/statistics/company/TechCorp \
  -w "\n"