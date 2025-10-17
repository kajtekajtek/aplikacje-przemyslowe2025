# Notatki techniczne

## 1. Różnice między JUnit 4 a JUnit 5 w kontekście konfiguracji

**JUnit 4:**
- Pojedynczy artefakt: `junit:junit:4.x`
- Monolityczna architektura - wszystko w jednym JAR
- Adnotacje z pakietu `org.junit.*`
- Runner do Mockito: `@RunWith(MockitoJUnitRunner.class)`
- Brak wsparcia dla parametryzowanych testów bez dodatkowych bibliotek

**JUnit 5 (Jupiter):**
- **Modularna architektura** - trzy komponenty:
  - **JUnit Platform** - fundament do uruchamiania testów na JVM
  - **JUnit Jupiter** - nowy model programowania (adnotacje, assertions)
  - **JUnit Vintage** - wsparcie dla JUnit 3/4 (kompatybilność wsteczna)
  
- **Konfiguracja w Maven** wymaga:
  ```xml
  <!-- BOM do zarządzania wersjami -->
  <dependencyManagement>
    <dependencies>
      <dependency>
        <groupId>org.junit</groupId>
        <artifactId>junit-bom</artifactId>
        <version>5.11.0</version>
        <type>pom</type>
        <scope>import</scope>
      </dependency>
    </dependencies>
  </dependencyManagement>
  
  <!-- API do pisania testów -->
  <dependency>
    <groupId>org.junit.jupiter</groupId>
    <artifactId>junit-jupiter-api</artifactId>
    <scope>test</scope>
  </dependency>
  ```

- **Integracja z Mockito:**
  - JUnit 4: `@RunWith(MockitoJUnitRunner.class)`
  - JUnit 5: `@ExtendWith(MockitoExtension.class)` + `mockito-junit-jupiter`

- **Nowe adnotacje:**
  - `@Test` → `org.junit.jupiter.api.Test`
  - `@Before` → `@BeforeEach`
  - `@After` → `@AfterEach`
  - `@BeforeClass` → `@BeforeAll`
  - `@AfterClass` → `@AfterAll`
  - `@Ignore` → `@Disabled`

- **Zalety JUnit 5:**
  - Natywne wsparcie dla testów parametryzowanych (`@ParameterizedTest`)
  - Lepsze zarządzanie cyklem życia testów
  - Extension model zamiast runners
  - Wsparcie dla Java 8+ (lambdy, streamy)

## 2. Rola dedykowanego pluginu do uruchamiania testów

**Maven Surefire Plugin** - kluczowy element ekosystemu testowego:

**Podstawowe zadania:**
- **Wykrywanie testów**: Automatyczne znajdowanie klas testowych według konwencji:
  - `**/Test*.java`
  - `**/*Test.java`
  - `**/*Tests.java`
  - `**/*TestCase.java`

- **Uruchamianie testów**: Wykonanie w fazie `test` cyklu życia Maven

- **Raportowanie**: Generowanie raportów w formatach:
  - TXT (czytelne dla człowieka)
  - XML (do integracji z CI/CD)

**Konfiguracja w projekcie:**
```xml
<plugin>
  <artifactId>maven-surefire-plugin</artifactId>
  <version>3.3.0</version>
</plugin>
```

**Dlaczego to ważne:**
- **Automatyzacja**: Nie trzeba ręcznie wywoływać testów
- **Integracja CI/CD**: Jenkins, GitLab CI mogą parsować raporty XML
- **Izolacja**: Każdy test uruchamiany w osobnym classloaderze
- **Kontrola**: Można filtrować testy, ustawiać timeout, parallel execution
- **Fail-fast**: Build zatrzymuje się przy pierwszym błędzie (domyślnie)

**Relacja z JaCoCo:**
Surefire uruchamia testy → JaCoCo agent monitoruje wykonanie → raport pokrycia

## 3. Mechanizm działania JaCoCo w kontekście śledzenia wykonanych linii kodu

**JaCoCo (Java Code Coverage)** - narzędzie do analizy pokrycia kodu:

**Mechanizm instrumentacji bytecode:**

1. **Faza `prepare-agent`** (przed uruchomieniem testów):
   - JaCoCo dodaje **Java Agent** do JVM
   - Agent modyfikuje bytecode klas podczas ładowania (on-the-fly instrumentation)
   - Wstrzykuje sondy (probes) w kluczowych punktach:
     - Na początku każdej linii kodu
     - W punktach rozgałęzienia (if, switch, ?:)
     - W wejściach/wyjściach metod

2. **Podczas wykonania testów**:
   - Sondy zapisują informacje o wykonaniu do pamięci
   - Każda sonda ma unikalny identyfikator
   - Gdy linia kodu zostaje wykonana → sonda zaznacza to jako "visited"
   - Dane zbierane są w pliku binarnym `jacoco.exec`

3. **Faza `report`** (po testach):
   - JaCoCo czyta `jacoco.exec` + pliki `.class`
   - Analizuje które sondy zostały aktywowane
   - Generuje raporty w różnych formatach:
     - **HTML** - kolorowe wizualizacje (zielone = pokryte, czerwone = niepokryte)
     - **XML** - do integracji z SonarQube, CI/CD
     - **CSV** - do analizy danych

**Przykład bytecode instrumentation:**

Kod źródłowy:
```java
if (salary > 0) {
    employees.add(employee);
}
```

Bytecode po instrumentacji (konceptualnie):
```java
jacocoProbe[23] = true;  // Linia 1: warunek
if (salary > 0) {
    jacocoProbe[24] = true;  // Linia 2: gałąź true
    employees.add(employee);
} else {
    jacocoProbe[25] = true;  // Gałąź false (implicit)
}
```

**Zalety podejścia JaCoCo:**
- **Zero zmian w kodzie** - instrumentation on-the-fly
- **Dokładność** - śledzenie na poziomie instrukcji JVM
- **Wydajność** - minimalny overhead (~15-20%)
- **Kompatybilność** - działa z dowolnymi frameworkami testowymi

**Konfiguracja goals:**
- `jacoco:prepare-agent` - startuje agent przed testami
- `jacoco:report` - generuje raport po testach
- `jacoco:check` - weryfikuje minimum pokrycia (fail build jeśli < 70%)

## 4. Konwencja nazewnictwa metod testowych zapewniająca czytelność w długim okresie

**Cel:** Nazwa testu = dokumentacja zachowania systemu

**Rekomendowane konwencje:**

## A. **Given-When-Then** (BDD style)
```java
@Test
public void givenDuplicateEmail_whenAddEmployee_thenThrowsException() {
    // Arrange: employee with existing email
    // Act: add employee
    // Assert: exception thrown
}

@Test
public void givenEmptyList_whenGetAverageSalary_thenReturnsZero() {
    // Test implementation
}
```

**Format:** `given[Precondition]_when[Action]_then[ExpectedResult]`

## B. **Should-Style** (naturalne zdania)
```java
@Test
public void shouldThrowExceptionWhenEmployeeIsNull() { }

@Test
public void shouldReturnEmptyListWhenCompanyNotFound() { }

@Test
public void shouldCalculateAverageSalaryForMultipleEmployees() { }
```

**Format:** `should[ExpectedBehavior]When[Condition]`

## C. **Method_Scenario_ExpectedBehavior** (używane w projekcie)
```java
@Test
public void testAddEmployee() { }  // ❌ Za ogólne

@Test
public void testAddEmployeeNull() { }  // ✅ Lepsze

@Test
public void testAddEmployeeWithDuplicateEmail() { }  // ✅✅ Doskonałe

@Test
public void testGetEmployeesByCompanyNameNoMatches() { }  // ✅✅ Jasne
```

**Format:** `test[Method][Scenario][ExpectedResult]`

## Przykłady z projektu (najlepsze praktyki):
```java
// ✅ Doskonałe nazwy - od razu wiadomo co testujemy
testAddEmployeeWithDuplicateEmailCaseInsensitive()
testGetEmployeeWithHighestSalaryEmpty()
testImportFromCsvWithNegativeSalary()
testValidateSalaryConsistency_BoundaryCases()

// ❌ Złe nazwy - brak kontekstu
testAddEmployee()
testGetAverage()
testImport()
```

**Zasady ogólne:**
1. **Nie używaj skrótów** - `Emp` → `Employee`
2. **Opisz warunek wejściowy** - `Empty`, `Null`, `Duplicate`, `Invalid`
3. **Opisz oczekiwany rezultat** - `ThrowsException`, `ReturnsZero`, `Success`
4. **Nie używaj `test1`, `test2`** - zero informacji
5. **Długa nazwa OK** - lepiej 50 znaków czytelnych niż 10 niejasnych

**Bonus - JUnit 5 @DisplayName:**
```java
@Test
@DisplayName("Should throw IllegalArgumentException when employee is null")
public void testAddEmployee_NullInput_ThrowsException() {
    // Kod testu
}
```

## 5. Przygotowanie danych testowych równoważące czytelność i kompletność

**Problem:** Testy wymagają danych, ale zbyt dużo danych = nieczytelny kod

**Rozwiązania zastosowane w projekcie:**

## A. **Stałe na poziomie klasy** (Test Fixtures)
```java
public class EmployeeServiceTest {
    // ✅ Dane zdefiniowane raz, używane wszędzie
    private final String LAST_NAME_1    = "Baggins";
    private final String FIRST_NAME_1   = "Frodo";
    private final String EMAIL_1        = "frodo.baggins@techcorp.com";
    private final String COMPANY_NAME_1 = "TechCorp";
    private final Role   ROLE_1         = Role.ENGINEER;
    private final int    SALARY_1       = 8500;
}
```

**Zalety:**
- Jedna definicja, wiele użyć
- Łatwa modyfikacja (zmiana w jednym miejscu)
- Czytelne nazwy (`EMPLOYEE_1`, `EMAIL_2`)
- Typ-safe (kompilator weryfikuje)

**Wady:**
- Może prowadzić do zbyt dużej liczby stałych
- Trudniej zrozumieć co jest istotne w konkretnym teście

## B. **Inline data** dla testów specyficznych
```java
@Test
public void testGetEmployeesAlphabetically() {
    // ✅ Dane inline - od razu widać że testujemy sortowanie
    Employee employee1 = new Employee("Smith", "John", ...);
    Employee employee2 = new Employee("Adams", "Alice", ...);
    Employee employee3 = new Employee("Brown", "Bob", ...);
    
    // Asercja: Adams, Brown, Smith (alfabetycznie)
}
```

**Kiedy używać:** Gdy dane są kluczowe dla zrozumienia testu

## C. **Factory methods / Builders**
```java
// ✅ Metoda pomocnicza
private Employee createEmployee(String lastName, String firstName) {
    return new Employee(
        lastName, firstName, 
        "test@example.com", 
        "TestCorp", 
        Role.ENGINEER, 
        8000
    );
}

@Test
public void testSorting() {
    Employee emp1 = createEmployee("Smith", "John");
    Employee emp2 = createEmployee("Adams", "Alice");
}
```

**Zalety:** Ukrywa nieistotne szczegóły, focus na tym co ważne

## D. **@TempDir dla plików** (ImportServiceTest)
```java
@TempDir
Path tempDir;  // ✅ JUnit 5 tworzy/usuwa automatycznie

@Test
public void testImportFromCsv() throws IOException {
    Path csvFile = tempDir.resolve("test.csv");
    Files.writeString(csvFile, csvContent);
    // Test...
}
```

**Zalety:**
- Automatyczne cleanup
- Izolacja testów
- Brak hardcoded paths

## E. **Parametryzowane testy** dla wielu scenariuszy
```java
@ParameterizedTest
@CsvSource({
    "ENGINEER, 8000",
    "MANAGER, 12000",
    "INTERN, 3000"
})
public void testBaseSalary(Role role, int expectedSalary) {
    assertEquals(expectedSalary, role.getBaseSalary());
}
```

**Równowaga:**
- **Czytelność > DRY** - lepiej powtórzyć dane niż wprowadzić magiczną abstrakcję
- **Istotne inline, nieistotne w stałych** - `EMAIL_1` jako stała OK, ale nazwisko w teście sortowania → inline
- **Jeden test = jeden scenariusz** - nie testuj 10 rzeczy w jednym teście

## 6. Zasadność użycia metody @BeforeEach do inicjalizacji wspólnych danych

**@BeforeEach** - metoda wywoływana przed każdym testem

**Użycie w projekcie:**
```java
@BeforeEach
public void setUp() {
    employeeService = new EmployeeService();  // ✅ Czysty stan przed każdym testem
}
```

**Kiedy używać @BeforeEach:**

## ✅ **TAK - Inicjalizacja obiektów testowanych**
```java
@BeforeEach
public void setUp() {
    employeeService = new EmployeeService();  // Nowa instancja = izolacja
    importService = new ImportService(employeeService);
}
```
**Dlaczego:** Gwarancja czystego stanu, brak side-effects między testami

## ✅ **TAK - Mockowanie zależności**
```java
@BeforeEach
public void setUp() {
    httpClient = mock(HttpClient.class);  // Nowy mock przed każdym testem
    apiService = new ApiService(httpClient);
}
```

## ✅ **TAK - Wspólne dane używane w WIELU testach**
```java
@BeforeEach
public void setUp() {
    service = new EmployeeService();
    // Dodaj 3 pracowników używanych w 10+ testach
    service.addEmployee(employee1);
    service.addEmployee(employee2);
    service.addEmployee(employee3);
}
```

## ❌ **NIE - Dane specyficzne dla jednego testu**
```java
@BeforeEach
public void setUp() {
    // ❌ ZŁE - ten pracownik używany tylko w 1 teście
    specialEmployee = new Employee("Special", ...);
}
```
**Lepiej:** Stworzyć w konkretnym teście

## ❌ **NIE - Skomplikowana logika**
```java
@BeforeEach
public void setUp() {
    // ❌ ZŁE - zbyt dużo logiki, trudno debugować
    if (condition) {
        // 50 linii kodu
    } else {
        // 30 linii kodu
    }
}
```
**Lepiej:** Przenieść do metod pomocniczych, wywołać w testach

**Alternatywy:**

1. **@BeforeAll** - raz przed wszystkimi testami (metoda static)
   - Użycie: Ciężkie zasoby (połączenia DB, serwery testowe)
   - Problem: Stan współdzielony = ryzyko side-effects

2. **Metody pomocnicze** - wywoływane ręcznie w testach
   ```java
   private void givenThreeEmployees() {
       service.addEmployee(...);
       service.addEmployee(...);
       service.addEmployee(...);
   }
   
   @Test
   public void test() {
       givenThreeEmployees();  // Jawne wywołanie = czytelność
   }
   ```

**Zasada złotego środka:**
- **@BeforeEach** dla obiektu testowanego + podstawowych zależności
- **Inline** dla danych specyficznych dla testu
- **Metody pomocnicze** dla złożonych scenariuszy setup

**Benefit w projekcie:**
```java
// ✅ Każdy test ma czystą instancję EmployeeService
// = brak konfliktu emaili między testami
// = deterministyczne wyniki
```

## 7. Znaczenie metryki "70% pokrycia linii"

**Definicja:** 70% linii kodu zostało wykonanych podczas testów

**Co to oznacza w praktyce:**

Jeśli klasa ma 100 linii kodu:
- **70+ linii** zostało wykonanych przez testy → ✅ Próg spełniony
- **<70 linii** wykonanych → ❌ Build fail (jeśli skonfigurowane)

**Przykład konkretny:**

```java
public class Calculator {
    public int add(int a, int b) {           // Linia 1 ✅
        return a + b;                        // Linia 2 ✅
    }                                        // Linia 3
    
    public int subtract(int a, int b) {      // Linia 4 ❌
        return a - b;                        // Linia 5 ❌
    }                                        // Linia 6
    
    public int multiply(int a, int b) {      // Linia 7 ✅
        return a * b;                        // Linia 8 ✅
    }                                        // Linia 9
}
```

Jeśli testy wywołują tylko `add()` i `multiply()`:
- **Pokrycie linii: 4/6 = 67%** ❌
- Brakuje testów dla `subtract()`

**Dlaczego akurat 70%?**

1. **Pragmatyzm:**
   - 50% = za mało, dużo kodu nietestowanego
   - 90% = trudne do osiągnięcia, szczególnie z legacy code
   - **70% = balans** między jakością a wysiłkiem

2. **Standardy branżowe:**
   - Open source: 70-80%
   - Projekty enterprise: 80-90%
   - Critical systems (medyczne, lotnicze): 95-100%

3. **Co nie jest pokryte przy 70%:**
   - Metody pomocnicze (getters/setters, toString)
   - Kod obsługi wyjątków (rzadkie scenariusze)
   - Deprecated code
   - Edge cases

**Interpretacja w kontekście projektu:**

```
service/
├── EmployeeService.java   → oczekiwane pokrycie: 95%+
│   (Logika biznesowa - KRYTYCZNA)
│
├── ImportService.java     → oczekiwane pokrycie: 90%+
│   (Parsowanie, walidacja - WAŻNA)
│
├── ApiService.java        → oczekiwane pokrycie: 75%+
│   (I/O, networking - można niżej)
│
└── EmployeeMapper.java    → pokrycie automatyczne przez testy API
```

**Średnia ważona ≥ 70%** = cel spełniony

**Uwaga:** 70% to **minimum**, nie **cel**. Lepiej 70% dobrych testów niż 100% słabych.

## 8. Relacja między pokryciem 100% a idealnymi testami

**MIT POKRYCIA 100%:**

> "100% pokrycia linii = idealne testy = kod bez bugów" ❌ **FAŁSZ**

**Dlaczego 100% pokrycia NIE gwarantuje jakości:**

## Przykład 1: Kod wykonany, ale bez asercji
```java
public int divide(int a, int b) {
    return a / b;  // ⚠️ Bug: brak obsługi dzielenia przez 0
}

@Test
public void testDivide() {
    calculator.divide(10, 2);  // ✅ 100% pokrycia
    // ❌ Brak asercji! Test nic nie weryfikuje
}
```
**Pokrycie: 100% | Jakość testu: 0%**

## Przykład 2: Brak testów scenariuszy brzegowych
```java
public void addEmployee(Employee employee) {
    if (employee == null) {              // Linia 1
        throw new Exception("Null");     // Linia 2
    }
    employees.add(employee);             // Linia 3
}

@Test
public void testAddEmployee() {
    service.addEmployee(new Employee(...));  // ✅ Linię 1, 3 wykonane
    assertEquals(1, service.getEmployees().size());
}
```
**Pokrycie: 66% (linia 2 nie wykonana) | Jakość: średnia**

Ale czy to znaczy że **100% pokrycia** jest bez wartości? **NIE!**

## Co daje 100% pokrycia:
1. ✅ **Świadomość kodu** - każda linia była przynajmniej raz wykonana
2. ✅ **Wykrycie dead code** - kod z 0% pokrycia = może być usunięty
3. ✅ **Baza do refaktoryzacji** - można zmieniać kod z pewnością że testy go używają
4. ✅ **Dokumentacja** - testy pokazują jak używać API

## Czego NIE daje 100% pokrycia:
1. ❌ **Brak gwarancji poprawności logiki** - można wykonać kod z błędnymi danymi
2. ❌ **Brak pokrycia wszystkich scenariuszy** - edge cases, race conditions
3. ❌ **Brak testów integracyjnych** - komponenty mogą nie współpracować
4. ❌ **Brak testów wydajnościowych** - kod wolny ale pokryty w 100%

**Idealne testy = 100% pokrycia + właściwe asercje + scenariusze brzegowe + testy integracyjne**

**Przykład idealnego testu:**
```java
@Test
public void shouldThrowExceptionWhenDividingByZero() {
    // ✅ Scenariusz brzegowy
    ArithmeticException ex = assertThrows(
        ArithmeticException.class,
        () -> calculator.divide(10, 0)
    );
    // ✅ Weryfikacja komunikatu
    assertTrue(ex.getMessage().contains("by zero"));
}

@Test
public void shouldCorrectlyDividePositiveNumbers() {
    // ✅ Happy path
    assertEquals(5, calculator.divide(10, 2));
}

@Test
public void shouldHandleNegativeNumbers() {
    // ✅ Inny scenariusz
    assertEquals(-5, calculator.divide(-10, 2));
}
```

**Wniosek:**
- **70% pokrycia** z dobrymi testami > **100% pokrycia** bez asercji
- **Pokrycie = narzędzie**, nie cel sam w sobie
- **Jakość testów ≠ metryka pokrycia**

## 9. Różnice między pokryciem linii, gałęzi i ścieżek

**Trzy poziomy granularności analizy pokrycia:**

## A. **Line Coverage (pokrycie linii)**

**Definicja:** % linii kodu wykonanych przynajmniej raz

**Przykład:**
```java
public String checkAge(int age) {
    if (age >= 18) {           // Linia 1 ✅
        return "Adult";        // Linia 2 ✅
    }
    return "Minor";            // Linia 3 ❌
}

@Test
public void test() {
    assertEquals("Adult", checkAge(25));
}
```

**Pokrycie linii: 2/3 = 66%**

**Zalety:**
- Najprostsza metryka
- Szybka do obliczenia
- Dobra jako baseline

**Wady:**
- Nie wykrywa nieprzetestowanych gałęzi
- Można "oszukać" wykonując kod bez weryfikacji

## B. **Branch Coverage (pokrycie gałęzi)**

**Definicja:** % gałęzi decyzyjnych (if/else, switch, ?:) wykonanych

**Ten sam przykład:**
```java
public String checkAge(int age) {
    if (age >= 18) {           // Decyzja: TRUE ✅ / FALSE ❌
        return "Adult";        
    }
    return "Minor";            
}

@Test
public void test() {
    assertEquals("Adult", checkAge(25));  // Tylko gałąź TRUE
}
```

**Pokrycie gałęzi: 1/2 = 50%**

Brakuje testu dla gałęzi FALSE:
```java
@Test
public void testMinor() {
    assertEquals("Minor", checkAge(15));  // Gałąź FALSE ✅
}
```

**Teraz: Branch coverage = 2/2 = 100%**

**Przykład złożony:**
```java
public String validate(int a, int b) {
    if (a > 0 && b > 0) {      // Decyzja złożona: 4 kombinacje
        return "Both positive";
    }
    return "Not both positive";
}
```

Gałęzie do przetestowania:
1. `a > 0` = TRUE, `b > 0` = TRUE → "Both positive" ✅
2. `a > 0` = TRUE, `b > 0` = FALSE → "Not both positive"
3. `a > 0` = FALSE, `b > 0` = TRUE → "Not both positive"
4. `a > 0` = FALSE, `b > 0` = FALSE → "Not both positive"

**Branch coverage = pokrycie wszystkich kombinacji AND/OR**

## C. **Path Coverage (pokrycie ścieżek)**

**Definicja:** % wszystkich możliwych ścieżek wykonania przez kod

**Przykład:**
```java
public String classify(int age, boolean premium) {
    String result = "";
    
    if (age >= 18) {           // Decyzja 1: 2 opcje
        result = "Adult";
    } else {
        result = "Minor";
    }
    
    if (premium) {             // Decyzja 2: 2 opcje
        result += " Premium";
    }
    
    return result;
}
```

**Możliwe ścieżki: 2 × 2 = 4**

1. age >= 18, premium = true → "Adult Premium" ✅
2. age >= 18, premium = false → "Adult"
3. age < 18, premium = true → "Minor Premium"
4. age < 18, premium = false → "Minor"

**Path coverage = 100% dopiero gdy wszystkie 4 ścieżki przetestowane**

**Problem z path coverage:**

```java
for (int i = 0; i < n; i++) {
    if (condition) {
        // ...
    }
}
```

Jeśli `n = 10` i każda iteracja ma 2 ścieżki:
**Liczba ścieżek = 2^10 = 1024** 🤯

Przy 3 zagnieżdżonych if:
**2^3 = 8 ścieżek**

→ **Path coverage jest nieosągnięty dla dużych systemów!**

## Porównanie metryk:

| Metryka | Granularność | Trudność | Użyteczność |
|---------|--------------|----------|-------------|
| **Line** | Niska | Łatwa | Podstawowa |
| **Branch** | Średnia | Średnia | **Rekomendowana** ⭐ |
| **Path** | Wysoka | Bardzo trudna | Teoretyczna |

**W praktyce (projekt):**

JaCoCo raportuje:
- ✅ **Line coverage** - podstawowa metryka (cel: 70%)
- ✅ **Branch coverage** - pokazuje niepokryte gałęzie
- ✅ **Instruction coverage** - pokrycie instrukcji JVM (najbardziej precyzyjna)
- ❌ Path coverage - zbyt kosztowne

**Przykład z projektu:**
```java
// EmployeeService.validateSalaryConsistency()
public List<Employee> validateSalaryConsistency() {
    return this.employees.stream()
        .filter(e -> e.getSalary() < e.getRole().getBaseSalary())  // Branch!
        .collect(Collectors.toList());
}
```

Testy pokrywają:
1. Gałąź TRUE: pracownicy z salariami poniżej bazy ✅
2. Gałąź FALSE: pracownicy z salariami powyżej bazy ✅
3. Edge case: pusta lista ✅
4. Edge case: salary = baseSalary (granica) ✅

**Branch coverage: 100% + edge cases = doskonałe testy** ⭐

**Wniosek:**
- **70% line coverage** = minimum do zaliczenia
- **90%+ branch coverage** = cel dla krytycznego kodu
- **Path coverage** = zbyt kosztowne, focus na branch + edge cases

