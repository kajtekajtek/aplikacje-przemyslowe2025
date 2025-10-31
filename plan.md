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
- [x] Sprawdzić czy wszystkie istniejące testy przechodzą
- [x] Dodać testy dla nowego pola `status` w `EmployeeTest.java`

### 3. Obiekty transferu danych (DTO)
**Priorytet: Wysoki** ✅ **ZREALIZOWANE**

#### 3.1 EmployeeDTO
- [x] Utworzyć klasę `EmployeeDTO.java` w pakiecie `dto`
- [x] Dodać pola: `firstName`, `lastName`, `email`, `company`, `position`, `salary`, `status`
- [x] Dodać domyślny konstruktor i konstruktor z wszystkimi parametrami
- [x] Dodać gettery i settery dla wszystkich pól
- [x] Dodać adnotacje Jackson `@JsonCreator` i `@JsonProperty` jeśli potrzebne

#### 3.2 CompanyStatisticsDTO
- [x] Utworzyć klasę `CompanyStatisticsDTO.java` w pakiecie `dto`
- [x] Dodać pola: `companyName`, `employeeCount`, `averageSalary`, `highestSalary`, `topEarnerName`
- [x] Dodać domyślny konstruktor i konstruktor z wszystkimi parametrami
- [x] Dodać gettery i settery dla wszystkich pól

#### 3.3 ErrorResponse
- [x] Utworzyć klasę `ErrorResponse.java` w pakiecie `dto`
- [x] Dodać pola: `message`, `timestamp`, `status`, `path`
- [x] Dodać domyślny konstruktor i konstruktor z wszystkimi parametrami
- [x] Dodać gettery i settery dla wszystkich pól

### 4. Wyjątki biznesowe
**Priorytet: Średni**

#### 4.1 EmployeeNotFoundException
- [x] Utworzyć klasę `EmployeeNotFoundException.java` w pakiecie `exception`
- [x] Rozszerzyć klasę `RuntimeException` lub `Exception`
- [x] Dodać konstruktor przyjmujący komunikat błędu
- [x] Dodać konstruktor przyjmujący komunikat i przyczynę (throwable)

#### 4.2 DuplicateEmailException
- [x] Utworzyć klasę `DuplicateEmailException.java` w pakiecie `exception`
- [x] Rozszerzyć klasę `RuntimeException` lub `Exception`
- [x] Dodać konstruktor przyjmujący komunikat błędu
- [x] Dodać konstruktor przyjmujący komunikat i przyczynę (throwable)

#### 4.3 InvalidDataException
- [x] Utworzyć klasę `InvalidDataException.java` w pakiecie `exception`
- [x] Rozszerzyć klasę `RuntimeException` lub `Exception`
- [x] Dodać konstruktor przyjmujący komunikat błędu
- [x] Dodać konstruktor przyjmujący komunikat i przyczynę (throwable)

### 5. Globalna obsługa błędów
**Priorytet: Wysoki**

#### 5.1 GlobalExceptionHandler
- [x] Utworzyć klasę `GlobalExceptionHandler.java` w pakiecie `exception`
- [x] Dodać adnotację `@RestControllerAdvice`

#### 5.2 Obsługa EmployeeNotFoundException
- [x] Dodać metodę z adnotacją `@ExceptionHandler(EmployeeNotFoundException.class)`
- [x] Zwracać `ResponseEntity<ErrorResponse>` ze statusem `404 NOT_FOUND`
- [x] Ustawić poprawnie pola obiektu `ErrorResponse` (message, timestamp, status, path)

#### 5.3 Obsługa DuplicateEmailException
- [x] Dodać metodę z adnotacją `@ExceptionHandler(DuplicateEmailException.class)`
- [x] Zwracać `ResponseEntity<ErrorResponse>` ze statusem `409 CONFLICT`
- [x] Ustawić poprawnie pola obiektu `ErrorResponse`

#### 5.4 Obsługa InvalidDataException
- [x] Dodać metodę z adnotacją `@ExceptionHandler(InvalidDataException.class)`
- [x] Zwracać `ResponseEntity<ErrorResponse>` ze statusem `400 BAD_REQUEST`
- [x] Ustawić poprawnie pola obiektu `ErrorResponse` (z informacją o numerze linii)

#### 5.5 Obsługa IllegalArgumentException
- [x] Dodać metodę z adnotacją `@ExceptionHandler(IllegalArgumentException.class)`
- [x] Zwracać `ResponseEntity<ErrorResponse>` ze statusem `400 BAD_REQUEST`
- [x] Ustawić poprawnie pola obiektu `ErrorResponse`

#### 5.6 Obsługa ogólna (Exception)
- [x] Dodać metodę z adnotacją `@ExceptionHandler(Exception.class)`
- [x] Zwracać `ResponseEntity<ErrorResponse>` ze statusem `500 INTERNAL_SERVER_ERROR`
- [x] Ustawić poprawnie pola obiektu `ErrorResponse`

### 6. Kontroler REST dla pracowników
**Priorytet: Wysoki**

#### 6.1 Utworzenie EmployeeController
- [x] Utworzyć klasę `EmployeeController.java` w pakiecie `controller`
- [x] Dodać adnotację `@RestController`
- [x] Dodać adnotację `@RequestMapping("/api/employees")`
- [x] Wstrzyknąć `EmployeeService` przez konstruktor

#### 6.2 Metoda GET wszystkich pracowników
- [x] Dodać metodę z adnotacją `@GetMapping` bez parametrów
- [x] Zwracać `ResponseEntity<List<EmployeeDTO>>`
- [x] Mapować listę `Employee` na listę `EmployeeDTO`
- [x] Zwracać status `200 OK`

#### 6.3 Metoda GET pracownika po emailu
- [x] Dodać metodę z adnotacją `@GetMapping("/{email}")`
- [x] Przyjąć parametr `@PathVariable String email`
- [x] Zwracać `ResponseEntity<EmployeeDTO>`
- [x] Mapować `Employee` na `EmployeeDTO`
- [x] Zwracać status `200 OK` jeśli istnieje, `404 NOT_FOUND` jeśli nie

#### 6.4 Metoda GET z filtrowaniem po firmie
- [x] Dodać metodę z adnotacją `@GetMapping` z parametrem `@RequestParam(required = false)`
- [x] Zwracać `ResponseEntity<List<EmployeeDTO>>`
- [x] Obsłużyć przypadek gdy parametr nie jest podany (zwrócić wszystkich)
- [x] Zwracać status `200 OK`

#### 6.5 Metoda POST tworzenia pracownika
- [x] Dodać metodę z adnotacją `@PostMapping`
- [x] Przyjąć parametr `@RequestBody EmployeeDTO`
- [x] Mapować `EmployeeDTO` na `Employee`
- [x] Zwracać `ResponseEntity<EmployeeDTO>` ze statusem `201 CREATED`
- [x] Dodać nagłówek `Location` z URI nowego zasobu
- [x] Obsłużyć wyjątek `DuplicateEmailException`

#### 6.6 Metoda PUT aktualizacji pracownika
- [x] Dodać metodę z adnotacją `@PutMapping("/{email}")`
- [x] Przyjąć parametry `@PathVariable String email` i `@RequestBody EmployeeDTO`
- [x] Mapować `EmployeeDTO` na `Employee`
- [x] Zwracać `ResponseEntity<EmployeeDTO>` ze statusem `200 OK`
- [x] Obsłużyć przypadek braku pracownika (`404 NOT_FOUND`)

#### 6.7 Metoda DELETE usuwania pracownika
- [x] Dodać metodę z adnotacją `@DeleteMapping("/{email}")`
- [x] Przyjąć parametr `@PathVariable String email`
- [x] Zwracać `ResponseEntity<Void>` ze statusem `204 NO_CONTENT`
- [x] Obsłużyć przypadek braku pracownika (`404 NOT_FOUND`)

#### 6.8 Metoda PATCH zmiany statusu
- [x] Dodać metodę z adnotacją `@PatchMapping("/{email}/status")`
- [x] Przyjąć parametry `@PathVariable String email` i `@RequestBody Map<String, String>`
- [x] Zwracać `ResponseEntity<EmployeeDTO>` ze statusem `200 OK`
- [x] Obsłużyć przypadek braku pracownika (`404 NOT_FOUND`)

#### 6.9 Metoda GET pracowników po statusie
- [x] Dodać metodę z adnotacją `@GetMapping("/status/{status}")`
- [x] Przyjąć parametr `@PathVariable String status`
- [x] Zwracać `ResponseEntity<List<EmployeeDTO>>`
- [x] Zwracać status `200 OK`

### 7. Mapper między Employee a EmployeeDTO
**Priorytet: Średni**

#### 7.1 Klasa EmployeeMapper
- [x] Utworzyć klasę `EmployeeMapper.java` w pakiecie `controller` lub `dto`
- [x] Dodać metodę statyczną `toDTO(Employee employee)` - mapowanie Employee -> EmployeeDTO
- [x] Dodać metodę statyczną `toEntity(EmployeeDTO dto)` - mapowanie EmployeeDTO -> Employee
- [x] Dodać metodę statyczną `toDTOList(List<Employee> employees)` - mapowanie listy

### 8. Rozszerzenie serwisów
**Priorytet: Średni** ✅ **ZREALIZOWANE**

#### 8.1 Rozszerzenie EmployeeService
- [x] Dodać metodę `getEmployeesByStatus(EmploymentStatus status)` jeśli nie istnieje
- [x] Dodać metodę `updateEmployeeStatus(String email, EmploymentStatus status)` jeśli nie istnieje
- [x] Upewnić się, że metody rzucają odpowiednie wyjątki (`EmployeeNotFoundException`)

#### 8.2 Rozszerzenie CompanyStatistics
- [x] Upewnić się, że metoda zwracająca statystyki uwzględnia status zatrudnienia
- [x] Dodać metodę zwracającą rozkład statusów jeśli potrzebna

### 9. Kontroler REST dla statystyk
**Priorytet: Wysoki** ✅ **ZREALIZOWANE**

#### 9.1 Utworzenie StatisticsController
- [x] Utworzyć klasę `StatisticsController.java` w pakiecie `controller`
- [x] Dodać adnotację `@RestController`
- [x] Dodać adnotację `@RequestMapping("/api/statistics")`
- [x] Wstrzyknąć `EmployeeService` przez konstruktor

#### 9.2 Metoda GET średniego wynagrodzenia
- [x] Dodać metodę z adnotacją `@GetMapping("/salary/average")`
- [x] Przyjąć parametr `@RequestParam(required = false) String company`
- [x] Zwracać `ResponseEntity<Map<String, Double>>` z kluczem "averageSalary"
- [x] Zwracać status `200 OK`

#### 9.3 Metoda GET statystyk firmy
- [x] Dodać metodę z adnotacją `@GetMapping("/company/{companyName}")`
- [x] Przyjąć parametr `@PathVariable String companyName`
- [x] Zwracać `ResponseEntity<CompanyStatisticsDTO>`
- [x] Zwracać status `200 OK`

#### 9.4 Metoda GET liczby stanowisk
- [x] Dodać metodę z adnotacją `@GetMapping("/positions")`
- [x] Zwracać `ResponseEntity<Map<String, Integer>>`
- [x] Zwracać status `200 OK`

#### 9.5 Metoda GET rozkładu statusów
- [x] Dodać metodę z adnotacją `@GetMapping("/status")`
- [x] Zwracać `ResponseEntity<Map<String, Integer>>`
- [x] Zwracać status `200 OK`

### 10. Testy kontrolerów z MockMvc
**Priorytet: Wysoki**

#### 10.1 Utworzenie EmployeeControllerTest
- [x] Utworzyć klasę `EmployeeControllerTest.java` w pakiecie `controller` (katalog test)
- [x] Dodać adnotację `@WebMvcTest(EmployeeController.class)`
- [x] Dodać `@MockBean EmployeeService`
- [x] Dodać `@Autowired MockMvc`

#### 10.2 Test GET wszystkich pracowników
- [x] Dodać test weryfikujący status `200 OK`
- [x] Weryfikować zawartość JSON używając `jsonPath()`
- [x] Mockować odpowiedź serwisu

#### 10.3 Test GET pracownika po emailu
- [x] Dodać test weryfikujący status `200 OK` i zwrócone dane
- [x] Dodać test weryfikujący status `404 NOT_FOUND` dla nieistniejącego emaila
- [x] Używać `jsonPath()` do weryfikacji JSON

#### 10.4 Test GET z filtrowaniem po firmie
- [x] Dodać test weryfikujący filtrowanie z parametrem
- [x] Dodać test weryfikujący zwrócenie wszystkich bez parametru

#### 10.5 Test POST tworzenia pracownika
- [x] Dodać test weryfikujący status `201 CREATED`
- [x] Weryfikować nagłówek `Location`
- [x] Weryfikować zwrócony obiekt w ciele odpowiedzi

#### 10.6 Test POST z duplikatem emaila
- [x] Dodać test weryfikujący status `409 CONFLICT`
- [x] Weryfikować obiekt `ErrorResponse`

#### 10.7 Test PUT aktualizacji pracownika
- [x] Dodać test weryfikujący status `200 OK`
- [x] Dodać test weryfikujący status `404 NOT_FOUND`

#### 10.8 Test DELETE usuwania pracownika
- [x] Dodać test weryfikujący status `204 NO_CONTENT`
- [x] Dodać test weryfikujący status `404 NOT_FOUND`

#### 10.9 Test PATCH zmiany statusu
- [x] Dodać test weryfikujący status `200 OK`
- [x] Weryfikować zmianę statusu w zwróconym obiekcie

#### 10.10 Test GET pracowników po statusie
- [x] Dodać test weryfikujący filtrowanie po statusie
- [x] Weryfikować zawartość zwróconej listy

#### 10.11 Utworzenie StatisticsControllerTest
- [x] Utworzyć klasę `StatisticsControllerTest.java` w pakiecie `controller` (katalog test)
- [x] Dodać adnotację `@WebMvcTest(StatisticsController.class)`
- [x] Dodać `@MockBean EmployeeService`
- [x] Dodać `@Autowired MockMvc`

#### 10.12 Testy StatisticsController
- [x] Dodać testy dla wszystkich endpointów statystyk
- [x] Weryfikować status `200 OK`
- [x] Weryfikować format odpowiedzi JSON

### 11. Weryfikacja i testowanie
**Priorytet: Wysoki**

#### 11.1 Testy jednostkowe
- [x] Uruchomić wszystkie testy jednostkowe (`mvn test`)
- [x] Sprawdzić czy wszystkie testy przechodzą
- [x] Naprawić ewentualne błędy

#### 11.2 Testy integracyjne
- [x] Uruchomić aplikację komendą `mvn spring-boot:run`
- [x] Sprawdzić czy serwer startuje na porcie 8080
- [x] Sprawdzić logi aplikacji pod kątem błędów

#### 11.3 Testowanie endpointów z curl
- [x] Przetestować GET wszystkich pracowników
- [x] Przetestować POST nowego pracownika
- [x] Przetestować PUT aktualizacji pracownika
- [x] Przetestować PATCH zmiany statusu
- [x] Przetestować DELETE pracownika
- [x] Przetestować GET statystyk

### 12. Dokumentacja
**Priorytet: Średni**

#### 12.1 Aktualizacja README.md
- [x] Dodać sekcję o REST API
- [x] Opisać wszystkie endpointy z przykładami żądań curl
- [x] Dodać instrukcje uruchomienia aplikacji
- [x] Opisać format odpowiedzi JSON

#### 12.2 Dokumentacja endpointów
- [x] Opisać EmployeeController i wszystkie jego endpointy
- [x] Opisać StatisticsController i wszystkie jego endpointy
- [x] Opisać kody odpowiedzi HTTP (200, 201, 204, 400, 404, 409, 500)
- [x] Opisać strukturę DTO

#### 12.3 Dokumentacja obsługi błędów
- [x] Opisać GlobalExceptionHandler
- [x] Opisać wszystkie obsługiwane wyjątki i kody odpowiedzi
- [x] Dodać przykłady błędów

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