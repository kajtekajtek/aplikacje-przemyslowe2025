# Plan implementacji zadania 5: REST API dla systemu zarządzania pracownikami

## Przegląd zadania
Stworzenie kontrolerów REST, które umożliwią udostępnienie funkcjonalności systemu zarządzania pracownikami poprzez interfejs programistyczny (API) zwracający dane w formacie JSON.

## Struktura zadań

### 1. Konfiguracja Spring Web
**Priorytet: Wysoki**

#### 1.1 Dodanie zależności Spring Web
- [x] Dodać `spring-boot-starter-web` do `pom.xml` w głównym katalogu projektu
- [x] Usunąć lub zastąpić `spring-boot-starter` jeśli już istnieje
- [x] Zachować `spring-boot-starter-test` dla testów

#### 1.2 Aktualizacja application.properties
- [x] Dodać `server.port=8080` do `src/main/resources/application.properties`
- [x] Dodać `spring.application.name=employee-management-api`
- [x] Dodać `spring.jackson.serialization.write-dates-as-timestamps=false`
- [x] Zachować istniejące ustawienia z poprzedniego zadania

### 2. Model: Status zatrudnienia
**Priorytet: Wysoki**

#### 2.1 Utworzenie enum EmploymentStatus
- [x] Utworzyć klasę `EmploymentStatus.java` w pakiecie `model`
- [x] Dodać wartości: `ACTIVE`, `ON_LEAVE`, `TERMINATED`

#### 2.2 Rozszerzenie modelu Employee
- [x] Dodać pole `status` typu `EmploymentStatus` do klasy `Employee`
- [x] Zaktualizować konstruktor(-y) klasy `Employee`
- [x] Dodać getter i setter dla pola `status`
- [x] Zaktualizować metodę `toString()` jeśli istnieje

#### 2.3 Aktualizacja istniejących testów
- [ ] Sprawdzić czy wszystkie istniejące testy przechodzą
- [ ] Dodać testy dla nowego pola `status` w `EmployeeTest.java`

### 3. Obiekty transferu danych (DTO)
**Priorytet: Wysoki**

#### 3.1 EmployeeDTO
- [ ] Utworzyć klasę `EmployeeDTO.java` w pakiecie `dto`
- [ ] Dodać pola: `firstName`, `lastName`, `email`, `company`, `position`, `salary`, `status`
- [ ] Dodać domyślny konstruktor i konstruktor z wszystkimi parametrami
- [ ] Dodać gettery i settery dla wszystkich pól
- [ ] Dodać adnotacje Jackson `@JsonCreator` i `@JsonProperty` jeśli potrzebne

#### 3.2 CompanyStatisticsDTO
- [ ] Utworzyć klasę `CompanyStatisticsDTO.java` w pakiecie `dto`
- [ ] Dodać pola: `companyName`, `employeeCount`, `averageSalary`, `highestSalary`, `topEarnerName`
- [ ] Dodać domyślny konstruktor i konstruktor z wszystkimi parametrami
- [ ] Dodać gettery i settery dla wszystkich pól

#### 3.3 ErrorResponse
- [ ] Utworzyć klasę `ErrorResponse.java` w pakiecie `dto`
- [ ] Dodać pola: `message`, `timestamp`, `status`, `path`
- [ ] Dodać domyślny konstruktor i konstruktor z wszystkimi parametrami
- [ ] Dodać gettery i settery dla wszystkich pól

### 4. Wyjątki biznesowe
**Priorytet: Średni**

#### 4.1 EmployeeNotFoundException
- [ ] Utworzyć klasę `EmployeeNotFoundException.java` w pakiecie `exception`
- [ ] Rozszerzyć klasę `RuntimeException` lub `Exception`
- [ ] Dodać konstruktor przyjmujący komunikat błędu
- [ ] Dodać konstruktor przyjmujący komunikat i przyczynę (throwable)

#### 4.2 DuplicateEmailException
- [ ] Utworzyć klasę `DuplicateEmailException.java` w pakiecie `exception`
- [ ] Rozszerzyć klasę `RuntimeException` lub `Exception`
- [ ] Dodać konstruktor przyjmujący komunikat błędu
- [ ] Dodać konstruktor przyjmujący komunikat i przyczynę (throwable)

#### 4.3 InvalidDataException
- [ ] Utworzyć klasę `InvalidDataException.java` w pakiecie `exception`
- [ ] Rozszerzyć klasę `RuntimeException` lub `Exception`
- [ ] Dodać konstruktor przyjmujący komunikat błędu
- [ ] Dodać konstruktor przyjmujący komunikat i przyczynę (throwable)

### 5. Globalna obsługa błędów
**Priorytet: Wysoki**

#### 5.1 GlobalExceptionHandler
- [ ] Utworzyć klasę `GlobalExceptionHandler.java` w pakiecie `exception`
- [ ] Dodać adnotację `@RestControllerAdvice`

#### 5.2 Obsługa EmployeeNotFoundException
- [ ] Dodać metodę z adnotacją `@ExceptionHandler(EmployeeNotFoundException.class)`
- [ ] Zwracać `ResponseEntity<ErrorResponse>` ze statusem `404 NOT_FOUND`
- [ ] Ustawić poprawnie pola obiektu `ErrorResponse` (message, timestamp, status, path)

#### 5.3 Obsługa DuplicateEmailException
- [ ] Dodać metodę z adnotacją `@ExceptionHandler(DuplicateEmailException.class)`
- [ ] Zwracać `ResponseEntity<ErrorResponse>` ze statusem `409 CONFLICT`
- [ ] Ustawić poprawnie pola obiektu `ErrorResponse`

#### 5.4 Obsługa InvalidDataException
- [ ] Dodać metodę z adnotacją `@ExceptionHandler(InvalidDataException.class)`
- [ ] Zwracać `ResponseEntity<ErrorResponse>` ze statusem `400 BAD_REQUEST`
- [ ] Ustawić poprawnie pola obiektu `ErrorResponse`

#### 5.5 Obsługa IllegalArgumentException
- [ ] Dodać metodę z adnotacją `@ExceptionHandler(IllegalArgumentException.class)`
- [ ] Zwracać `ResponseEntity<ErrorResponse>` ze statusem `400 BAD_REQUEST`
- [ ] Ustawić poprawnie pola obiektu `ErrorResponse`

#### 5.6 Obsługa ogólna (Exception)
- [ ] Dodać metodę z adnotacją `@ExceptionHandler(Exception.class)`
- [ ] Zwracać `ResponseEntity<ErrorResponse>` ze statusem `500 INTERNAL_SERVER_ERROR`
- [ ] Ustawić poprawnie pola obiektu `ErrorResponse`

### 6. Kontroler REST dla pracowników
**Priorytet: Wysoki**

#### 6.1 Utworzenie EmployeeController
- [ ] Utworzyć klasę `EmployeeController.java` w pakiecie `controller`
- [ ] Dodać adnotację `@RestController`
- [ ] Dodać adnotację `@RequestMapping("/api/employees")`
- [ ] Wstrzyknąć `EmployeeService` przez konstruktor

#### 6.2 Metoda GET wszystkich pracowników
- [ ] Dodać metodę z adnotacją `@GetMapping` bez parametrów
- [ ] Zwracać `ResponseEntity<List<EmployeeDTO>>`
- [ ] Mapować listę `Employee` na listę `EmployeeDTO`
- [ ] Zwracać status `200 OK`

#### 6.3 Metoda GET pracownika po emailu
- [ ] Dodać metodę z adnotacją `@GetMapping("/{email}")`
- [ ] Przyjąć parametr `@PathVariable String email`
- [ ] Zwracać `ResponseEntity<EmployeeDTO>`
- [ ] Mapować `Employee` na `EmployeeDTO`
- [ ] Zwracać status `200 OK` jeśli istnieje, `404 NOT_FOUND` jeśli nie

#### 6.4 Metoda GET z filtrowaniem po firmie
- [ ] Dodać metodę z adnotacją `@GetMapping` z parametrem `@RequestParam(required = false)`
- [ ] Zwracać `ResponseEntity<List<EmployeeDTO>>`
- [ ] Obsłużyć przypadek gdy parametr nie jest podany (zwrócić wszystkich)
- [ ] Zwracać status `200 OK`

#### 6.5 Metoda POST tworzenia pracownika
- [ ] Dodać metodę z adnotacją `@PostMapping`
- [ ] Przyjąć parametr `@RequestBody EmployeeDTO`
- [ ] Mapować `EmployeeDTO` na `Employee`
- [ ] Zwracać `ResponseEntity<EmployeeDTO>` ze statusem `201 CREATED`
- [ ] Dodać nagłówek `Location` z URI nowego zasobu
- [ ] Obsłużyć wyjątek `DuplicateEmailException`

#### 6.6 Metoda PUT aktualizacji pracownika
- [ ] Dodać metodę z adnotacją `@PutMapping("/{email}")`
- [ ] Przyjąć parametry `@PathVariable String email` i `@RequestBody EmployeeDTO`
- [ ] Mapować `EmployeeDTO` na `Employee`
- [ ] Zwracać `ResponseEntity<EmployeeDTO>` ze statusem `200 OK`
- [ ] Obsłużyć przypadek braku pracownika (`404 NOT_FOUND`)

#### 6.7 Metoda DELETE usuwania pracownika
- [ ] Dodać metodę z adnotacją `@DeleteMapping("/{email}")`
- [ ] Przyjąć parametr `@PathVariable String email`
- [ ] Zwracać `ResponseEntity<Void>` ze statusem `204 NO_CONTENT`
- [ ] Obsłużyć przypadek braku pracownika (`404 NOT_FOUND`)

#### 6.8 Metoda PATCH zmiany statusu
- [ ] Dodać metodę z adnotacją `@PatchMapping("/{email}/status")`
- [ ] Przyjąć parametry `@PathVariable String email` i `@RequestBody Map<String, String>`
- [ ] Zwracać `ResponseEntity<EmployeeDTO>` ze statusem `200 OK`
- [ ] Obsłużyć przypadek braku pracownika (`404 NOT_FOUND`)

#### 6.9 Metoda GET pracowników po statusie
- [ ] Dodać metodę z adnotacją `@GetMapping("/status/{status}")`
- [ ] Przyjąć parametr `@PathVariable String status`
- [ ] Zwracać `ResponseEntity<List<EmployeeDTO>>`
- [ ] Zwracać status `200 OK`

### 7. Mapper między Employee a EmployeeDTO
**Priorytet: Średni**

#### 7.1 Klasa EmployeeMapper
- [ ] Utworzyć klasę `EmployeeMapper.java` w pakiecie `controller` lub `dto`
- [ ] Dodać metodę statyczną `toDTO(Employee employee)` - mapowanie Employee -> EmployeeDTO
- [ ] Dodać metodę statyczną `toEntity(EmployeeDTO dto)` - mapowanie EmployeeDTO -> Employee
- [ ] Dodać metodę statyczną `toDTOList(List<Employee> employees)` - mapowanie listy

### 8. Rozszerzenie serwisów
**Priorytet: Średni**

#### 8.1 Rozszerzenie EmployeeService
- [ ] Dodać metodę `getEmployeesByStatus(EmploymentStatus status)` jeśli nie istnieje
- [ ] Dodać metodę `updateEmployeeStatus(String email, EmploymentStatus status)` jeśli nie istnieje
- [ ] Upewnić się, że metody rzucają odpowiednie wyjątki (`EmployeeNotFoundException`)

#### 8.2 Rozszerzenie CompanyStatistics
- [ ] Upewnić się, że metoda zwracająca statystyki uwzględnia status zatrudnienia
- [ ] Dodać metodę zwracającą rozkład statusów jeśli potrzebna

### 9. Kontroler REST dla statystyk
**Priorytet: Wysoki**

#### 9.1 Utworzenie StatisticsController
- [ ] Utworzyć klasę `StatisticsController.java` w pakiecie `controller`
- [ ] Dodać adnotację `@RestController`
- [ ] Dodać adnotację `@RequestMapping("/api/statistics")`
- [ ] Wstrzyknąć `EmployeeService` przez konstruktor

#### 9.2 Metoda GET średniego wynagrodzenia
- [ ] Dodać metodę z adnotacją `@GetMapping("/salary/average")`
- [ ] Przyjąć parametr `@RequestParam(required = false) String company`
- [ ] Zwracać `ResponseEntity<Map<String, Double>>` z kluczem "averageSalary"
- [ ] Zwracać status `200 OK`

#### 9.3 Metoda GET statystyk firmy
- [ ] Dodać metodę z adnotacją `@GetMapping("/company/{companyName}")`
- [ ] Przyjąć parametr `@PathVariable String companyName`
- [ ] Zwracać `ResponseEntity<CompanyStatisticsDTO>`
- [ ] Zwracać status `200 OK`

#### 9.4 Metoda GET liczby stanowisk
- [ ] Dodać metodę z adnotacją `@GetMapping("/positions")`
- [ ] Zwracać `ResponseEntity<Map<String, Integer>>`
- [ ] Zwracać status `200 OK`

#### 9.5 Metoda GET rozkładu statusów
- [ ] Dodać metodę z adnotacją `@GetMapping("/status")`
- [ ] Zwracać `ResponseEntity<Map<String, Integer>>`
- [ ] Zwracać status `200 OK`

### 10. Testy kontrolerów z MockMvc
**Priorytet: Wysoki**

#### 10.1 Utworzenie EmployeeControllerTest
- [ ] Utworzyć klasę `EmployeeControllerTest.java` w pakiecie `controller` (katalog test)
- [ ] Dodać adnotację `@WebMvcTest(EmployeeController.class)`
- [ ] Dodać `@MockBean EmployeeService`
- [ ] Dodać `@Autowired MockMvc`

#### 10.2 Test GET wszystkich pracowników
- [ ] Dodać test weryfikujący status `200 OK`
- [ ] Weryfikować zawartość JSON używając `jsonPath()`
- [ ] Mockować odpowiedź serwisu

#### 10.3 Test GET pracownika po emailu
- [ ] Dodać test weryfikujący status `200 OK` i zwrócone dane
- [ ] Dodać test weryfikujący status `404 NOT_FOUND` dla nieistniejącego emaila
- [ ] Używać `jsonPath()` do weryfikacji JSON

#### 10.4 Test GET z filtrowaniem po firmie
- [ ] Dodać test weryfikujący filtrowanie z parametrem
- [ ] Dodać test weryfikujący zwrócenie wszystkich bez parametru

#### 10.5 Test POST tworzenia pracownika
- [ ] Dodać test weryfikujący status `201 CREATED`
- [ ] Weryfikować nagłówek `Location`
- [ ] Weryfikować zwrócony obiekt w ciele odpowiedzi

#### 10.6 Test POST z duplikatem emaila
- [ ] Dodać test weryfikujący status `409 CONFLICT`
- [ ] Weryfikować obiekt `ErrorResponse`

#### 10.7 Test PUT aktualizacji pracownika
- [ ] Dodać test weryfikujący status `200 OK`
- [ ] Dodać test weryfikujący status `404 NOT_FOUND`

#### 10.8 Test DELETE usuwania pracownika
- [ ] Dodać test weryfikujący status `204 NO_CONTENT`
- [ ] Dodać test weryfikujący status `404 NOT_FOUND`

#### 10.9 Test PATCH zmiany statusu
- [ ] Dodać test weryfikujący status `200 OK`
- [ ] Weryfikować zmianę statusu w zwróconym obiekcie

#### 10.10 Test GET pracowników po statusie
- [ ] Dodać test weryfikujący filtrowanie po statusie
- [ ] Weryfikować zawartość zwróconej listy

#### 10.11 Utworzenie StatisticsControllerTest
- [ ] Utworzyć klasę `StatisticsControllerTest.java` w pakiecie `controller` (katalog test)
- [ ] Dodać adnotację `@WebMvcTest(StatisticsController.class)`
- [ ] Dodać `@MockBean EmployeeService`
- [ ] Dodać `@Autowired MockMvc`

#### 10.12 Testy StatisticsController
- [ ] Dodać testy dla wszystkich endpointów statystyk
- [ ] Weryfikować status `200 OK`
- [ ] Weryfikować format odpowiedzi JSON

### 11. Weryfikacja i testowanie
**Priorytet: Wysoki**

#### 11.1 Testy jednostkowe
- [ ] Uruchomić wszystkie testy jednostkowe (`mvn test`)
- [ ] Sprawdzić czy wszystkie testy przechodzą
- [ ] Naprawić ewentualne błędy

#### 11.2 Testy integracyjne
- [ ] Uruchomić aplikację komendą `mvn spring-boot:run`
- [ ] Sprawdzić czy serwer startuje na porcie 8080
- [ ] Sprawdzić logi aplikacji pod kątem błędów

#### 11.3 Testowanie endpointów z curl
- [ ] Przetestować GET wszystkich pracowników
- [ ] Przetestować POST nowego pracownika
- [ ] Przetestować PUT aktualizacji pracownika
- [ ] Przetestować PATCH zmiany statusu
- [ ] Przetestować DELETE pracownika
- [ ] Przetestować GET statystyk

### 12. Dokumentacja
**Priorytet: Średni**

#### 12.1 Aktualizacja README.md
- [ ] Dodać sekcję o REST API
- [ ] Opisać wszystkie endpointy z przykładami żądań curl
- [ ] Dodać instrukcje uruchomienia aplikacji
- [ ] Opisać format odpowiedzi JSON

#### 12.2 Dokumentacja endpointów
- [ ] Opisać EmployeeController i wszystkie jego endpointy
- [ ] Opisać StatisticsController i wszystkie jego endpointy
- [ ] Opisać kody odpowiedzi HTTP (200, 201, 204, 400, 404, 409, 500)
- [ ] Opisać strukturę DTO

#### 12.3 Dokumentacja obsługi błędów
- [ ] Opisać GlobalExceptionHandler
- [ ] Opisać wszystkie obsługiwane wyjątki i kody odpowiedzi
- [ ] Dodać przykłady błędów

## Harmonogram realizacji

### Faza 1: Podstawowa konfiguracja i model (1 dzień)
- Konfiguracja Spring Web
- Dodanie enum EmploymentStatus do modelu Employee

### Faza 2: DTO i wyjątki (1 dzień)
- Tworzenie wszystkich klas DTO
- Tworzenie wyjątków biznesowych

### Faza 3: Obsługa błędów (0.5 dnia)
- Implementacja GlobalExceptionHandler

### Faza 4: Kontroler pracowników (2-3 dni)
- Implementacja wszystkich endpointów w EmployeeController
- Implementacja mappera EmployeeMapper
- Rozszerzenie EmployeeService

### Faza 5: Kontroler statystyk (1 dzień)
- Implementacja wszystkich endpointów w StatisticsController

### Faza 6: Testy kontrolerów (2 dni)
- Implementacja testów dla EmployeeController
- Implementacja testów dla StatisticsController

### Faza 7: Weryfikacja i dokumentacja (1 dzień)
- Testowanie wszystkich endpointów
- Aktualizacja dokumentacji

## Potencjalne problemy i rozwiązania

### Problem 1: Problemy z mapowaniem Employee na EmployeeDTO
**Rozwiązanie**: Upewnić się, że wszystkie pola w obu klasach są poprawnie zmapowane, zwłaszcza enum Position i EmploymentStatus

### Problem 2: Problemy z datami w JSON
**Rozwiązanie**: Upewnić się, że `spring.jackson.serialization.write-dates-as-timestamps=false` jest ustawione w application.properties

### Problem 3: Problemy z walidacją danych
**Rozwiązanie**: Dodać adnotacje `@Valid` i `@RequestBody` w kontrolerach, oraz utworzyć walidatory w DTO

### Problem 4: Konflikty z istniejącymi testami
**Rozwiązanie**: Upewnić się, że nowe pola w Employee nie łamią istniejących testów

### Problem 5: Problemy z wstrzykiwaniem zależności
**Rozwiązanie**: Sprawdzić czy wszystkie serwisy są poprawnie oznaczone adnotacją `@Service` i czy nie ma cyklicznych zależności

## Kryteria sukcesu

- [ ] Wszystkie endpointy zaimplementowane zgodnie z wymaganiami
- [ ] Wszystkie testy jednostkowe przechodzą
- [ ] Aplikacja uruchamia się bez błędów na porcie 8080
- [ ] Wszystkie operacje CRUD działają poprawnie
- [ ] Obsługa błędów działa dla wszystkich scenariuszy
- [ ] Status zatrudnienia jest poprawnie obsługiwany
- [ ] Testy MockMvc pokrywają wszystkie endpointy
- [ ] Dokumentacja jest kompletna i aktualna
- [ ] Kod jest czytelny i dobrze zorganizowany
- [ ] Użycie ResponseEntity dla wszystkich odpowiedzi HTTP

## Technologie i narzędzia

- **Spring Boot Starter Web** - podstawowa funkcjonalność webowa
- **Jackson** - serializacja/deserializacja JSON
- **MockMvc** - testowanie warstwy web
- **JUnit** - framework testowy
- **cURL** - testowanie endpointów
- **Postman** (opcjonalnie) - testowanie API

## Ważne uwagi

- Wszystkie metody kontrolerów muszą zwracać `ResponseEntity<T>` dla pełnej kontroli nad odpowiedzią HTTP
- DTO muszą być oddzielone od modelu domenowego
- Wszystkie wyjątki muszą być obsługiwane przez GlobalExceptionHandler
- Testy powinny pokrywać zarówno scenariusze sukcesu jak i błędu
- Dokumentacja powinna zawierać przykłady dla wszystkich endpointów