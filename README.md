# Employee Management System - Spring Boot REST API

System zarządzania pracownikami zbudowany w Spring Boot z REST API do zarządzania danymi pracowników i statystykami.

## Architektura

Aplikacja składa się z trzech modułów:
- **model** - klasy domenowe (Employee, Role, CompanyStatistics) i DTO
- **service** - logika biznesowa i serwisy
- **api** - aplikacja Spring Boot z REST API

## Konfiguracja Spring

### 1. Adnotacje (@Component, @Service, @Configuration)
- Serwisy oznaczone adnotacjami Spring
- Automatyczne skanowanie komponentów
- Konfiguracja w klasie `EmployeeManagementApplicationConfig`

### 2. Klasy konfiguracyjne
- `EmployeeManagementApplicationConfig` - konfiguracja głównej aplikacji

### 3. Konfiguracja XML
- `employees-beans.xml` - definicje pracowników jako beany
- Lista `xmlEmployees` z referencjami do wszystkich beanów
- Import przez `@ImportResource`

## Kompilacja i uruchomienie

### Uruchomienie przez Maven
```bash
cd api
mvn spring-boot:run
```

### Budowanie wykonywalnego JAR
```bash
mvn clean package -DskipTests
java -jar api/target/app-1.0-SNAPSHOT.jar
```

Aplikacja uruchamia się na porcie **8080** (domyślnie).

## Konfiguracja

### application.properties
```properties
server.port=8080
spring.application.name=employee-management-api
spring.jackson.serialization.write-dates-as-timestamps=false
app.api.url=https://jsonplaceholder.typicode.com/users
```

### employees-beans.xml
Definicje pracowników w XML:
- 6 pracowników firmy XMLCorp
- Różne role: CEO, VP, Manager, Engineer, Intern
- Automatyczne ładowanie przy starcie aplikacji

---

# REST API Dokumentacja

## Base URL
```
http://localhost:8080/api
```

## Employee Controller

### GET /api/employees
Pobiera listę wszystkich pracowników.

**Żądanie:**
```bash
curl -X GET http://localhost:8080/api/employees
```

**Odpowiedź 200 OK:**
```json
[
  {
    "firstName": "Thomas",
    "lastName": "Anderson",
    "emailAddress": "thomas.anderson@xmlcorp.com",
    "companyName": "XMLCorp",
    "role": "CEO",
    "salary": 30000,
    "status": "ACTIVE"
  },
  {
    "firstName": "Sofia",
    "lastName": "Martinez",
    "emailAddress": "sofia.martinez@xmlcorp.com",
    "companyName": "XMLCorp",
    "role": "VP",
    "salary": 20000,
    "status": "ACTIVE"
  }
]
```

### GET /api/employees?company={companyName}
Pobiera listę pracowników filtrowaną po nazwie firmy.

**Żądanie:**
```bash
curl -X GET "http://localhost:8080/api/employees?company=XMLCorp"
```

**Odpowiedź 200 OK:** Lista pracowników danej firmy (format jak powyżej)

### GET /api/employees/{email}
Pobiera pojedynczego pracownika po adresie email.

**Żądanie:**
```bash
curl -X GET http://localhost:8080/api/employees/thomas.anderson@xmlcorp.com
```

**Odpowiedź 200 OK:**
```json
{
  "firstName": "Thomas",
  "lastName": "Anderson",
  "emailAddress": "thomas.anderson@xmlcorp.com",
  "companyName": "XMLCorp",
  "role": "CEO",
  "salary": 30000,
  "status": "ACTIVE"
}
```

**Odpowiedź 404 NOT FOUND:**
```json
{
  "message": "Employee with email notfound@example.com not found",
  "status": 404,
  "path": "/api/employees/notfound@example.com",
  "timestamp": "2025-10-31T17:00:00"
}
```

### GET /api/employees/status/{status}
Pobiera listę pracowników filtrowaną po statusie zatrudnienia.

**Dostępne statusy:** `ACTIVE`, `ON_LEAVE`, `TERMINATED`

**Żądanie:**
```bash
curl -X GET http://localhost:8080/api/employees/status/ACTIVE
```

**Odpowiedź 200 OK:** Lista pracowników o danym statusie (format jak GET /api/employees)

**Odpowiedź 400 BAD REQUEST:**
```json
{
  "message": "Invalid status: INVALID",
  "status": 400,
  "path": "/api/employees/status/INVALID",
  "timestamp": "2025-10-31T17:00:00"
}
```

### POST /api/employees
Tworzy nowego pracownika.

**Żądanie:**
```bash
curl -X POST http://localhost:8080/api/employees \
  -H "Content-Type: application/json" \
  -d '{
    "firstName": "Jan",
    "lastName": "Kowalski",
    "emailAddress": "jan@example.com",
    "companyName": "TechCorp",
    "role": "ENGINEER",
    "salary": 8000,
    "status": "ACTIVE"
  }'
```

**Odpowiedź 201 CREATED:**
```json
{
  "firstName": "Jan",
  "lastName": "Kowalski",
  "emailAddress": "jan@example.com",
  "companyName": "TechCorp",
  "role": "ENGINEER",
  "salary": 8000,
  "status": "ACTIVE"
}
```
Nagłówek `Location` zawiera URL nowo utworzonego zasobu.

**Odpowiedź 409 CONFLICT:**
```json
{
  "message": "Employee with email jan@example.com already exists.",
  "status": 409,
  "path": "/api/employees",
  "timestamp": "2025-10-31T17:00:00"
}
```

### PUT /api/employees/{email}
Aktualizuje istniejącego pracownika.

**Żądanie:**
```bash
curl -X PUT http://localhost:8080/api/employees/jan@example.com \
  -H "Content-Type: application/json" \
  -d '{
    "firstName": "Jan",
    "lastName": "Kowalski",
    "emailAddress": "jan@example.com",
    "companyName": "TechCorp",
    "role": "MANAGER",
    "salary": 12000,
    "status": "ACTIVE"
  }'
```

**Odpowiedź 200 OK:** Zaktualizowany obiekt pracownika (format jak GET /api/employees/{email})

**Odpowiedź 404 NOT FOUND:** (format jak w GET /api/employees/{email})

### PATCH /api/employees/{email}/status
Aktualizuje status zatrudnienia pracownika.

**Żądanie:**
```bash
curl -X PATCH http://localhost:8080/api/employees/jan@example.com/status \
  -H "Content-Type: application/json" \
  -d '{
    "status": "ON_LEAVE"
  }'
```

**Odpowiedź 200 OK:** Zaktualizowany obiekt pracownika

**Odpowiedź 400 BAD REQUEST:**
```json
{
  "message": "Status field is required",
  "status": 400,
  "path": "/api/employees/jan@example.com/status",
  "timestamp": "2025-10-31T17:00:00"
}
```

### DELETE /api/employees/{email}
Usuwa pracownika po adresie email.

**Żądanie:**
```bash
curl -X DELETE http://localhost:8080/api/employees/jan@example.com
```

**Odpowiedź 204 NO CONTENT:** Brak treści

**Odpowiedź 404 NOT FOUND:** (format jak w GET /api/employees/{email})

## Statistics Controller

### GET /api/statistics/salary/average
Pobiera średnią pensję wszystkich pracowników lub pracowników danej firmy.

**Żądanie (wszyscy pracownicy):**
```bash
curl -X GET http://localhost:8080/api/statistics/salary/average
```

**Żądanie (dla konkretnej firmy):**
```bash
curl -X GET "http://localhost:8080/api/statistics/salary/average?company=XMLCorp"
```

**Odpowiedź 200 OK:**
```json
{
  "averageSalary": 14666.67
}
```

### GET /api/statistics/company/{companyName}
Pobiera statystyki dla konkretnej firmy.

**Żądanie:**
```bash
curl -X GET http://localhost:8080/api/statistics/company/XMLCorp
```

**Odpowiedź 200 OK:**
```json
{
  "companyName": "XMLCorp",
  "employeeCount": 6,
  "averageSalary": 14666.67,
  "highestSalary": 30000,
  "topEarnerName": "Thomas Anderson"
}
```

**Uwaga:** Statystyki zawierają tylko aktywnych pracowników (`status = ACTIVE`).

### GET /api/statistics/positions
Pobiera liczbę pracowników według pozycji/roli.

**Żądanie:**
```bash
curl -X GET http://localhost:8080/api/statistics/positions
```

**Odpowiedź 200 OK:**
```json
{
  "CEO": 1,
  "VP": 1,
  "MANAGER": 1,
  "ENGINEER": 2,
  "INTERN": 1
}
```

### GET /api/statistics/status
Pobiera rozkład pracowników według statusu zatrudnienia.

**Żądanie:**
```bash
curl -X GET http://localhost:8080/api/statistics/status
```

**Odpowiedź 200 OK:**
```json
{
  "ACTIVE": 5,
  "ON_LEAVE": 1,
  "TERMINATED": 0
}
```

---

## Struktura DTO

### EmployeeDTO
```json
{
  "firstName": "string",        // Imię (wymagane)
  "lastName": "string",         // Nazwisko (wymagane)
  "emailAddress": "string",     // Adres email (wymagane, unikalny)
  "companyName": "string",      // Nazwa firmy (wymagane)
  "role": "string",             // Rola: CEO, VP, MANAGER, ENGINEER, INTERN
  "salary": 0,                  // Pensja (wymagane, >= 0)
  "status": "string"            // Status: ACTIVE, ON_LEAVE, TERMINATED
}
```

### CompanyStatisticsDTO
```json
{
  "companyName": "string",      // Nazwa firmy
  "employeeCount": 0,           // Liczba aktywnych pracowników
  "averageSalary": 0.0,         // Średnia pensja
  "highestSalary": 0,           // Najwyższa pensja
  "topEarnerName": "string"     // Imię i nazwisko najlepiej zarabiającego
}
```

### ErrorResponse
```json
{
  "message": "string",          // Komunikat błędu
  "status": 0,                  // Kod statusu HTTP
  "path": "string",             // Ścieżka, która spowodowała błąd
  "timestamp": "2025-10-31T17:00:00"  // Czas wystąpienia błędu
}
```

## Role i Statusy

### Role
- `CEO` - Dyrektor (base salary: 25000)
- `VP` - Wiceprezes (base salary: 18000)
- `MANAGER` - Kierownik (base salary: 12000)
- `ENGINEER` - Inżynier (base salary: 8000)
- `INTERN` - Stażysta (base salary: 3000)

### EmploymentStatus
- `ACTIVE` - Aktywny
- `ON_LEAVE` - Na urlopie
- `TERMINATED` - Zwolniony

---

## Kody odpowiedzi HTTP

| Kod | Opis | Kiedy |
|-----|------|-------|
| **200 OK** | Sukces | Operacja zakończona powodzeniem |
| **201 CREATED** | Utworzono | Nowy pracownik został utworzony (POST) |
| **204 NO CONTENT** | Brak treści | Pracownik został usunięty (DELETE) |
| **400 BAD REQUEST** | Błędne żądanie | Nieprawidłowe parametry, brakujące pola, nieprawidłowy status/rola |
| **404 NOT FOUND** | Nie znaleziono | Pracownik lub firma nie istnieje |
| **409 CONFLICT** | Konflikt | Próba utworzenia pracownika z istniejącym emailem |
| **500 INTERNAL SERVER ERROR** | Błąd serwera | Nieoczekiwany błąd serwera |

---

## Obsługa błędów

API używa `GlobalExceptionHandler` do centralnej obsługi wszystkich wyjątków. Wszystkie błędy zwracają obiekt `ErrorResponse` w formacie JSON.

### Obsługiwane wyjątki:

#### EmployeeNotFoundException
- **Kod:** 404 NOT FOUND
- **Kiedy:** Pracownik z podanym emailem nie istnieje
- **Przykład:**
```json
{
  "message": "Employee with email notfound@example.com not found",
  "status": 404,
  "path": "/api/employees/notfound@example.com",
  "timestamp": "2025-10-31T17:00:00"
}
```

#### DuplicateEmailException
- **Kod:** 409 CONFLICT
- **Kiedy:** Próba utworzenia pracownika z emailem, który już istnieje
- **Przykład:**
```json
{
  "message": "Employee with email jan@example.com already exists.",
  "status": 409,
  "path": "/api/employees",
  "timestamp": "2025-10-31T17:00:00"
}
```

#### IllegalArgumentException
- **Kod:** 400 BAD REQUEST
- **Kiedy:** Nieprawidłowe parametry żądania
- **Przykłady:**
  - Nieprawidłowy status: `"Invalid status: INVALID"`
  - Brakujące pole: `"Status field is required"`
  - Inne błędy walidacji

#### InvalidDataException
- **Kod:** 400 BAD REQUEST
- **Kiedy:** Nieprawidłowe dane w żądaniu

#### Exception (ogólny)
- **Kod:** 500 INTERNAL SERVER ERROR
- **Kiedy:** Nieoczekiwany błąd serwera
- **Przykład:**
```json
{
  "message": "An unexpected error occurred: [szczegóły błędu]",
  "status": 500,
  "path": "/api/employees",
  "timestamp": "2025-10-31T17:00:00"
}
```

---

## Testy

### Testy jednostkowe
```bash
mvn clean test
```

### Testy z pokryciem kodu
```bash
mvn clean jacoco:prepare-agent test jacoco:report
```

Raport HTML:
- `service/target/site/jacoco/index.html`
- `model/target/site/jacoco/index.html`
- `api/target/site/jacoco/index.html`

### Testy kontrolerów
Testy REST API używają MockMvc:
- `EmployeeControllerTest` - testy wszystkich endpointów EmployeeController
- `StatisticsControllerTest` - testy wszystkich endpointów StatisticsController

---

## Funkcjonalności

1. **Zarządzanie pracownikami**
   - Dodawanie/usuwanie pracowników przez REST API
   - Listowanie według różnych kryteriów (firma, status)
   - Aktualizacja danych pracowników
   - Zmiana statusu zatrudnienia

2. **Statystyki**
   - Średnia pensja (globalna lub dla firmy)
   - Statystyki firmowe (liczba pracowników, średnia pensja, najlepiej zarabiający)
   - Rozkład według ról
   - Rozkład według statusów zatrudnienia

3. **Import danych**
   - Z pliku CSV (przez ImportService)
   - Z zewnętrznego API (przez ApiService)
   - Z konfiguracji XML (przy starcie aplikacji)

4. **Walidacja**
   - Sprawdzanie spójności wynagrodzeń
   - Walidacja unikalności emaili
   - Obsługa błędów przez GlobalExceptionHandler

---

## Przykłady użycia

### Utworzenie pracownika
```bash
curl -X POST http://localhost:8080/api/employees \
  -H "Content-Type: application/json" \
  -d '{
    "firstName": "Anna",
    "lastName": "Nowak",
    "emailAddress": "anna.nowak@example.com",
    "companyName": "TechCorp",
    "role": "ENGINEER",
    "salary": 9000,
    "status": "ACTIVE"
  }'
```

### Aktualizacja pensji
```bash
curl -X PUT http://localhost:8080/api/employees/anna.nowak@example.com \
  -H "Content-Type: application/json" \
  -d '{
    "firstName": "Anna",
    "lastName": "Nowak",
    "emailAddress": "anna.nowak@example.com",
    "companyName": "TechCorp",
    "role": "ENGINEER",
    "salary": 10000,
    "status": "ACTIVE"
  }'
```

### Zmiana statusu na urlop
```bash
curl -X PATCH http://localhost:8080/api/employees/anna.nowak@example.com/status \
  -H "Content-Type: application/json" \
  -d '{"status": "ON_LEAVE"}'
```

### Pobranie statystyk firmy
```bash
curl -X GET http://localhost:8080/api/statistics/company/TechCorp
```

### Usunięcie pracownika
```bash
curl -X DELETE http://localhost:8080/api/employees/anna.nowak@example.com
```

---

## Wersja
1.0-SNAPSHOT

## Autor
TechCorp
