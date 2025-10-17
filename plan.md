# Zadanie 3: Testy jednostkowe i automatyzacja buildu

## Analiza stanu projektu

### ✅ Co już jest zrobione:

1. **Konfiguracja Maven (25%)**
   - ✅ JUnit 5 (Jupiter) 5.11.0 skonfigurowany w `dependencyManagement`
   - ✅ Mockito 5.7.0 z integracją JUnit Jupiter
   - ✅ Maven Surefire Plugin 3.3.0 do uruchamiania testów
   - ❌ **BRAKUJE: JaCoCo plugin do raportowania pokrycia kodu**

2. **Testy EmployeeService (30%)**
   - ✅ **KOMPLETNE** - 778 linii testów
   - ✅ Wszystkie scenariusze pokryte:
     - Dodawanie pracownika (null, duplikaty emaili, case insensitive)
     - Wyszukiwanie po firmie (nieistniejąca firma, case insensitive)
     - Średnie wynagrodzenie (pusta lista, pojedynczy pracownik, wiele)
     - Maksymalne wynagrodzenie (Optional przy pustej liście)
     - Walidacja wynagrodzeń (granice, wszystkie role)
     - Statystyki firm (pojedyncza, wiele, pusta lista)

3. **Testy ImportService (20%)**
   - ✅ **KOMPLETNE** - 492 linie testów
   - ✅ Wszystkie scenariusze pokryte:
     - Poprawny import z weryfikacją danych w systemie
     - Niepoprawne stanowisko (kontynuacja importu)
     - Ujemne wynagrodzenie (odrzucenie linii)
     - Weryfikacja ImportSummary
     - Użycie `@TempDir` do izolacji testów
     - Obsługa błędów (puste pola, nieprawidłowe formaty)

4. **Testy ApiService (15%)**
   - ❌ **BRAKUJE CAŁKOWICIE** - `ApiServiceTest.java` nie istnieje
   - Potrzebne scenariusze:
     - Mockowanie HttpClient
     - Poprawna odpowiedź JSON
     - Błędy HTTP (404, 500)
     - Parsowanie danych z JSON do Employee

5. **Pokrycie kodu ≥70% (10%)**
   - ❌ **BRAKUJE** - brak JaCoCo do generowania raportów
   - ❌ Brak możliwości weryfikacji pokrycia

### ⚠️ Co wymaga implementacji:

## Plan implementacji

### 1. Konfiguracja JaCoCo (PRIORYTET: WYSOKI)

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

### 2. Testy ApiService z mockowaniem (PRIORYTET: KRYTYCZNY)

**Lokalizacja:** `service/src/test/java/com/techcorp/ApiServiceTest.java` (NOWY PLIK)

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

## Podsumowanie

### Kryteria recenzji - status:
- [❌] Konfiguracja Maven + JUnit 5 + JaCoCo (25%) - **BRAKUJE JaCoCo**
- [✅] Testy EmployeeService (30%) - **GOTOWE**
- [✅] Testy ImportService (20%) - **GOTOWE**
- [❌] Testy ApiService z mockami (15%) - **DO ZROBIENIA**
- [❌] Pokrycie ≥70% (10%) - **DO WERYFIKACJI po JaCoCo**

### Kolejność implementacji:
1. **KROK 1:** Dodać JaCoCo plugin do `pom.xml`
2. **KROK 2:** Utworzyć `ApiServiceTest.java` z 7+ testami
3. **KROK 3:** Uruchomić `mvn clean test jacoco:report`
4. **KROK 4:** Zweryfikować pokrycie ≥70% w raporcie
5. **KROK 5:** Zaktualizować `README.md`

### Szacowany czas:
- JaCoCo config: 15 min
- ApiServiceTest: 45-60 min
- Weryfikacja + dokumentacja: 15 min
- **RAZEM: ~1.5h**

## Notatki techniczne

### Różnice JUnit 4 vs JUnit 5:
- JUnit 5: `@Test` z `org.junit.jupiter.api.Test`
- Mockito: używamy `@ExtendWith(MockitoExtension.class)` zamiast `@RunWith`
- Assertions: `org.junit.jupiter.api.Assertions`

### JaCoCo - mechanizm działania:
- Instrumentacja bytecode podczas testów
- Śledzenie wykonanych linii poprzez Java agent
- Generowanie raportów w różnych formatach (HTML, XML, CSV)

### Dlaczego mockowanie w testach jednostkowych?
- **Izolacja:** Testujemy tylko ApiService, nie HttpClient
- **Szybkość:** Brak prawdziwych połączeń sieciowych
- **Powtarzalność:** Zawsze te same wyniki
- **Testowanie błędów:** Możliwość symulacji dowolnych scenariuszy
- **Brak zależności:** Testy działają offline