# Zadanie 5

System zarządzania pracownikami wymaga udostępnienia funkcjonalności poprzez interfejs programistyczny (API)zwracający dane w formacie JSON. Zadaniem jest stworzenie kontrolerów REST, które umożliwią innym aplikacjom konsumowanie danych systemu.

## Wymagania funkcjonalne

### 1. Konfiguracja Spring Web

Dodać zależność spring-boot-starter-web do pom.xml lub build.gradle. Konfiguracja w application.properties:
```
server.port=8080
spring.application.name=employee-management-api
spring.jackson.serialization.write-dates-as-timestamps=false
```

### 2. Obiekty transferu danych (DTO)

Utworzyć klasy DTO oddzielające model wewnętrzny od reprezentacji API:
- EmployeeDTO - uniwersalna reprezentacja pracownika w API używana we wszystkich operacjach (GET, POST, PUT). Zawiera pola: firstName, lastName, email, company, position, salary, status.
- CompanyStatisticsDTO - statystyki firmy z polami: companyName, employeeCount, averageSalary, highestSalary, topEarnerName.
- ErrorResponse - standardowa odpowiedź błędu z polami: message, timestamp, status, path.

### 3. Kontroler REST dla pracowników

Klasa EmployeeController z adnotacją @RestController i @RequestMapping("/api/employees") implementuje endpointy:
- GET /api/employees - zwraca listę wszystkich pracowników jako List<EmployeeDTO> ze statusem 200 OK.
- GET /api/employees/{email} - zwraca konkretnego pracownika po emailu jako EmployeeDTO. Zwraca 200 OK jeśli istnieje, 404 Not Found jeśli nie.
- GET /api/employees?company=X - filtruje pracowników po nazwie firmy używając @RequestParam(required = false). Jeśli parametr nie podany, zwraca wszystkich.
- POST /api/employees - tworzy nowego pracownika przyjmując @RequestBody EmployeeDTO. Zwraca 201 Created z nagłówkiem Location oraz utworzonym obiektem w ciele.
- PUT /api/employees/{email} - aktualizuje dane pracownika przyjmując email w ścieżce i @RequestBody EmployeeDTO. Zwraca 200 OK z zaktualizowanym obiektem lub 404 Not Found.
- DELETE /api/employees/{email} - usuwa pracownika. Zwraca 204 No Content przy sukcesie lub 404 Not Found jeśli nie istnieje.
- Wszystkie metody zwracają ResponseEntity<T> dla pełnej kontroli nad odpowiedzią HTTP.

### 4. Kontroler REST dla statystyk

Klasa StatisticsController z @RestController i @RequestMapping("/api/statistics") implementuje:
- GET /api/statistics/salary/average - zwraca średnie wynagrodzenie jako Map<String, Double> gdzie klucz to "averageSalary".
- GET /api/statistics/salary/average?company=X - średnie wynagrodzenie w konkretnej firmie (parametr opcjonalny).
- GET /api/statistics/company/{companyName} - szczegółowe statystyki firmy jako CompanyStatisticsDTO.
- GET /api/statistics/positions - liczba pracowników na każdym stanowisku jako Map<String, Integer>.
- GET /api/statistics/status - rozkład pracowników według statusu zatrudnienia jako Map<String, Integer>.

### 5. Obsługa błędów

Klasa GlobalExceptionHandler z @RestControllerAdvice zawiera metody z @ExceptionHandler dla:
- EmployeeNotFoundException - zwraca 403 Not Found z obiektem ErrorResponse.
- DuplicateEmailException - zwraca 409 Conflict gdy próbujemy utworzyć pracownika z istniejącym emailem.
- InvalidDataException - zwraca 400 Bad Request dla błędów walidacji danych.
- IllegalArgumentException - zwraca 400 Bad Request dla nieprawidłowych argumentów.
- Exception - catch-all zwracający 500 Internal Server Error dla nieobsłużonych wyjątków.

### 6. Nowa funkcjonalność: Status zatrudnienia

Dodać do modelu Employee pole status typu enum EmploymentStatus z wartościami: ACTIVE, ON_LEAVE, TERMINATED.

Dodatkowe endpointy:
- PATCH /api/employees/{email}/status - zmienia tylko status pracownika przyjmując @RequestBody z polem status. Zwraca 200 OK z zaktualizowanym pracownikiem.
- GET /api/employees/status/{status} - zwraca listę pracowników o danym statusie ze statusem 200 OK.
- Metody w serwisie powinny umożliwiać filtrowanie po statusie oraz zwracać statystyki rozkładu statusów.

### 7. Testy API z MockMvc

Napisać testy używające @WebMvcTest i MockMvc:
- Test GET wszystkich - weryfikacja statusu 200 i zawartości JSON
- Test GET po emailu - weryfikacja zwróconych danych
- Test GET nieistniejącego - weryfikacja 404
- Test POST - weryfikacja 201 i nagłówka Location
- Test POST z duplikatem - weryfikacja 409
- Test DELETE - weryfikacja 204
- Test filtrowania po firmie
- Test PATCH zmiany statusu
Serwisy mockować przez @MockBean. Do weryfikacji JSON używać jsonPath().

### Struktura projektu

```
src/
├── main/
│   ├── java/com.techcorp.employee/
│   │   ├── EmployeeManagementApplication.java
│   │   ├── controller/
│   │   │   ├── EmployeeController.java
│   │   │   └── StatisticsController.java
│   │   ├── dto/
│   │   │   ├── EmployeeDTO.java
│   │   │   ├── CompanyStatisticsDTO.java
│   │   │   └── ErrorResponse.java
│   │   ├── service/
│   │   │   ├── EmployeeService.java
│   │   │   └── (pozostałe z poprzednich zadań)
│   │   ├── exception/
│   │   │   ├── GlobalExceptionHandler.java
│   │   │   ├── EmployeeNotFoundException.java
│   │   │   ├── DuplicateEmailException.java
│   │   │   └── InvalidDataException.java
│   │   └── model/
│   │       ├── Employee.java
│   │       ├── Position.java
│   │       └── EmploymentStatus.java (nowy)
│   └── resources/
│       └── application.properties
└── test/
    └── java/com.techcorp.employee/
        └── controller/
            ├── EmployeeControllerTest.java
            └── StatisticsControllerTest.java
```

## Wymagania techniczne

- Kontrolery używają @RestController co łączy @Controller i @ResponseBody. Metody oznaczane przez @GetMapping, @PostMapping, @PutMapping, @DeleteMapping, @PatchMapping dla odpowiednich operacji HTTP. Parametry ścieżki przez @PathVariable, query parameters przez @RequestParam, ciało żądania przez @RequestBody.
- Wszystkie metody kontrolerów zwracają ResponseEntity<T> dla kontroli nad kodem HTTP i nagłówkami. DTO są oddzielone od modelu domenowego i mapowane w kontrolerze lub dedykowanym mapperze. Jackson automatycznie serializuje obiekty Java do JSON i deserializuje JSON do obiektów.
- Testy używają @WebMvcTest(NazwaKontrolera.class) do załadowania tylko warstwy web. Serwisy mockowane przez @MockBean. MockMvc wstrzykiwany przez @Autowired służy do wykonywania żądań HTTP.

## Przykłady testowania

Uruchomienie: mvn spring-boot:run

### GET wszystkich
curl http://localhost:8080/api/employees

### POST nowy pracownik
curl -X POST http://localhost:8080/api/employees \
  -H "Content-Type: application/json" \
  -d '{"firstName":"Jan","lastName":"Kowalski","email":"jan@example.com","company":"TechCorp","position":"PROGRAMISTA","salary":8000,"status":"ACTIVE"}'

### PUT aktualizacja
curl -X PUT http://localhost:8080/api/employees/jan@example.com \
  -H "Content-Type: application/json" \
  -d '{"firstName":"Jan","lastName":"Kowalski","email":"jan@example.com","company":"TechCorp","position":"MANAGER","salary":12000,"status":"ACTIVE"}'

### PATCH zmiana statusu
curl -X PATCH http://localhost:8080/api/employees/jan@example.com/status \
  -H "Content-Type: application/json" \
  -d '{"status":"ON_LEAVE"}'

### DELETE
curl -X DELETE http://localhost:8080/api/employees/jan@example.com

### GET statystyki
curl http://localhost:8080/api/statistics/company/TechCorp

## Kryteria oceny

### Kontrolery REST z operacjami CRUD

- Wszystkie endpointy (GET, POST, PUT, DELETE, PATCH) zaimplementowane z poprawnymi adnotacjami. Używanie @PathVariable, @RequestParam, @RequestBody. Zwracanie ResponseEntity z odpowiednimi kodami HTTP (200, 201, 204, 404). Kontroler statystyk zwraca zagregowane dane.
- 35%

### DTO i mapowanie

- Klasy EmployeeDTO, CompanyStatisticsDTO, ErrorResponse poprawnie zdefiniowane. DTO oddzielone od modelu domenowego. Mapowanie między Employee a EmployeeDTO działa w obu kierunkach. Jackson automatycznie serializuje/deserializuje JSON.
- 20%

### Obsługa błędów

- @RestControllerAdvice z metodami @ExceptionHandler dla różnych wyjątków. Spójne obiekty ErrorResponse zwracane z właściwymi kodami HTTP (400, 404, 409, 500).
- 20%

### Status zatrudnienia i endpointy
	
- Enum EmploymentStatus dodany do modelu i DTO. Endpoint PATCH do zmiany statusu. Endpoint GET do filtrowania po statusie. Statystyki uwzględniają rozkład statusów zatrudnienia.
- 10%

### Testy z MockMvc

- Testy używają @WebMvcTest i MockMvc. Pokryte scenariusze pozytywne i negatywne (success i error cases). Weryfikacja kodów HTTP i zawartości JSON przez jsonPath(). Mockowanie serwisów przez @MockBean.
- 15%

## Oddanie

Link do repozytorium z kodem, testami oraz README zawierającym listę wszystkich endpointów z przykładami żądań curl lub Postman oraz instrukcję uruchomienia.