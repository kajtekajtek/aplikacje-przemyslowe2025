# Zadanie 3: Testy jednostkowe i automatyzacja buildu

## Plan implementacji

### 1. Konfiguracja JaCoCo

**Lokalizacja:** `pom.xml` (główny pom projektu)

**Do zrobienia:**
- Dodać JaCoCo plugin w sekcji `<build><pluginManagement><plugins>`
- Skonfigurować goals: `prepare-agent`, `report`
- Ustawić minimalne pokrycie na 70% dla pakietu `com.techcorp` w module `service`
- Raport HTML w: `target/site/jacoco/index.html`

**Komendy po implementacji:**
```bash
mvn clean test                    # Tylko testy
mvn clean test jacoco:report      # Testy + raport pokrycia
mvn clean verify                  # Pełny cykl z weryfikacją
```

### 2. Testy ApiService z mockowaniem

**Lokalizacja:** `service/src/test/java/com/techcorp/ApiServiceTest.java`

**Wymagane testy:**

#### 2.1 Test poprawnej odpowiedzi JSON
- **Cel:** Symulacja udanej odpowiedzi HTTP 200 bez prawdziwego API
- **Mock:** `HttpClient.send()` zwraca mockowany `HttpResponse<String>`
- **Dane testowe:** JSON z tablicą pracowników
- **Weryfikacja:**
  - Lista nie jest pusta
  - Dane są poprawnie zmapowane (imię, nazwisko, email, firma)
  - Rola to ENGINEER (zgodnie z EmployeeMapper)

#### 2.2 Test błędu HTTP 404
- **Cel:** Weryfikacja rzucania ApiException przy błędach HTTP
- **Mock:** `HttpResponse.statusCode()` zwraca 404
- **Weryfikacja:**
  - Rzucony wyjątek `ApiException`
  - Komunikat zawiera "HTTP error" i "404"

#### 2.3 Test błędu HTTP 500
- **Cel:** Weryfikacja obsługi błędów serwera
- **Mock:** `HttpResponse.statusCode()` zwraca 500
- **Weryfikacja:**
  - Rzucony wyjątek `ApiException`
  - Komunikat zawiera "HTTP error" i "500"

#### 2.4 Test nieprawidłowego JSON
- **Cel:** Weryfikacja obsługi błędów parsowania
- **Mock:** `HttpResponse.body()` zwraca nieprawidłowy JSON
- **Weryfikacja:**
  - Rzucony wyjątek `ApiException`
  - Komunikat zawiera "Failed to parse JSON"

#### 2.5 Test błędu sieci (IOException)
- **Cel:** Weryfikacja obsługi problemów z połączeniem
- **Mock:** `HttpClient.send()` rzuca `IOException`
- **Weryfikacja:**
  - Rzucony wyjątek `ApiException`
  - Komunikat zawiera "Network error"

#### 2.6 Test przerwania (InterruptedException)
- **Cel:** Weryfikacja obsługi przerwania żądania
- **Mock:** `HttpClient.send()` rzuca `InterruptedException`
- **Weryfikacja:**
  - Rzucony wyjątek `ApiException`
  - Komunikat zawiera "Request interrupted"
  - Flaga przerwania wątku jest ustawiona

#### 2.7 Test parsowania pełnej nazwy
- **Cel:** Weryfikacja poprawności mapowania z JSON
- **Mock:** JSON z różnymi formatami nazw (pojedyncze słowo, wiele słów)
- **Weryfikacja:**
  - Poprawny split imienia i nazwiska
  - Obsługa edge cases (brak nazwiska, wiele nazwisk)

**Techniki mockowania:**
```java
@ExtendWith(MockitoExtension.class)
public class ApiServiceTest {
    @Mock
    private HttpClient httpClient;
    
    @Mock
    private HttpResponse<String> httpResponse;
    
    private ApiService apiService;
    
    @BeforeEach
    void setUp() {
        apiService = new ApiService(httpClient);
    }
    
    // Mockowanie odpowiedzi:
    when(httpClient.send(any(HttpRequest.class), any()))
        .thenReturn(httpResponse);
    
    when(httpResponse.statusCode()).thenReturn(200);
    when(httpResponse.body()).thenReturn(jsonString);
}
```

### 3. Weryfikacja pokrycia kodu

**Po implementacji testów ApiService:**
1. Uruchomić `mvn clean test jacoco:report`
2. Otworzyć raport: `service/target/site/jacoco/index.html`
3. Sprawdzić pokrycie dla pakietu `com.techcorp`:
   - `EmployeeService` - powinno być ~95-100%
   - `ImportService` - powinno być ~90-95%
   - `ApiService` - cel: >70%
   - `EmployeeMapper` - zostanie pokryty przez testy ApiService

**Metryki pokrycia:**
- **Line coverage**: % wykonanych linii kodu
- **Branch coverage**: % wykonanych gałęzi (if/else, switch)
- **Instruction coverage**: % wykonanych instrukcji JVM

### 4. Dokumentacja w README

**Aktualizacja:** `README.md`

**Dodać sekcję:**
```markdown
### Testy z pokryciem kodu

Uruchomienie testów:
mvn clean test

Uruchomienie testów z raportem pokrycia:
mvn clean test jacoco:report

Raport HTML:
service/target/site/jacoco/index.html

Wymagane pokrycie:
- Pakiet service: ≥70%
```

## TODO
- [x] Review testów EmployeeService i ImportService
- [x] Konfiguracja pom.xml
- [x] Testy ApiService
- [x] Test coverage >70%