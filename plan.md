# Plan implementacji zadania 4: Migracja do Spring Boot

## Przegląd zadania
Migracja istniejącego systemu zarządzania pracownikami do architektury Spring Boot z wykorzystaniem Dependency Injection oraz definiowaniem pracowników jako beany Spring w pliku konfiguracyjnym XML.

## Struktura zadań

### 1. Konfiguracja projektu Spring Boot
**Priorytet: Wysoki**

#### 1.1 Dodanie zależności Spring Boot
- [x] Zaktualizować `pom.xml` w głównym katalogu projektu
- [x] Dodać `spring-boot-starter` w wersji 3.x
- [x] Dodać `spring-boot-starter-test` dla testów
- [x] Zachować istniejącą zależność Gson
- [x] Dodać `spring-boot-maven-plugin` dla uruchamiania aplikacji

#### 1.2 Konfiguracja application.properties
- [x] Utworzyć plik `src/main/resources/application.properties`
- [x] Zdefiniować `app.api.url=https://jsonplaceholder.typicode.com/users`
- [x] Zdefiniować `app.import.csv-file=employees.csv`
- [x] Ustawić `logging.level.root=INFO`

### 2. Refaktoryzacja serwisów jako Spring Beany
**Priorytet: Wysoki**

#### 2.1 Refaktoryzacja EmployeeService
- [x] Dodać adnotację `@Service` do klasy EmployeeService
- [x] Usunąć statyczne referencje do EmployeeService
- [x] Usunąć ręczne tworzenie instancji w innych miejscach kodu

#### 2.2 Refaktoryzacja ImportService
- [x] Dodać adnotację `@Service` do klasy ImportService
- [x] Dodać EmployeeService jako zależność przez konstruktor
- [x] Usunąć ręczne przekazywanie zależności

#### 2.3 Refaktoryzacja ApiService
- [x] Dodać adnotację `@Service` do klasy ApiService
- [ ] Dodać HttpClient jako zależność przez konstruktor
- [ ] Dodać Gson jako zależność przez konstruktor
- [x] Dodać pole z adnotacją `@Value("${app.api.url}")` dla URL API
- [ ] dostosować testy

### 3. Definicja pracowników jako beany w pliku XML
**Priorytet: Średni**

#### 3.1 Utworzenie pliku konfiguracyjnego XML
- [ ] Utworzyć plik `src/main/resources/employees-beans.xml`
- [ ] Zdefiniować strukturę XML z odpowiednimi namespace'ami
- [ ] Dodać definicje beanów dla pracowników (employee1, employee2, itp.)
- [ ] Utworzyć listę `xmlEmployees` z referencjami do wszystkich beanów pracowników

#### 3.2 Konfiguracja wstrzykiwania XML
- [ ] Dodać adnotację `@ImportResource("classpath:employees-beans.xml")` do głównej klasy aplikacji
- [ ] Sprawdzić dostępność beanów XML w kontekście Spring

### 4. Konfiguracja zewnętrznych zależności jako beany
**Priorytet: Średni**

#### 4.1 Utworzenie klasy konfiguracyjnej AppConfig
- [ ] Utworzyć klasę `AppConfig` w pakiecie `config`
- [ ] Dodać adnotację `@Configuration`
- [ ] Dodać metodę `httpClient()` z adnotacją `@Bean` zwracającą `HttpClient.newHttpClient()`
- [ ] Dodać metodę `gson()` z adnotacją `@Bean` zwracającą nową instancję `Gson`

#### 4.2 Weryfikacja wstrzykiwania zależności
- [ ] Sprawdzić poprawność wstrzykiwania HttpClient w ApiService
- [ ] Sprawdzić poprawność wstrzykiwania Gson w ApiService

### 5. Klasa startowa aplikacji
**Priorytet: Wysoki**

#### 5.1 Utworzenie głównej klasy aplikacji
- [ ] Utworzyć klasę `EmployeeManagementApplication` w głównym pakiecie
- [ ] Dodać adnotację `@SpringBootApplication`
- [ ] Dodać adnotację `@ImportResource("classpath:employees-beans.xml")`
- [ ] Zaimplementować interfejs `CommandLineRunner`

#### 5.2 Implementacja metody run()
- [ ] Wstrzyknąć wszystkie potrzebne serwisy przez konstruktor:
  - [ ] ImportService
  - [ ] EmployeeService
  - [ ] ApiService
  - [ ] List<Employee> xmlEmployees z adnotacją `@Qualifier("xmlEmployees")`
- [ ] Zaimplementować demonstrację funkcjonalności:
  - [ ] Import pracowników z pliku CSV
  - [ ] Dodanie pracowników z beana xmlEmployees
  - [ ] Pobranie danych z REST API
  - [ ] Wyświetlenie statystyk dla wybranej firmy
  - [ ] Walidacja spójności wynagrodzeń
  - [ ] Wyświetlenie pracowników z niskimi wynagrodzeniami

### 6. Weryfikacja i testy
**Priorytet: Wysoki**

#### 6.1 Testy jednostkowe
- [ ] Sprawdzić czy wszystkie istniejące testy przechodzą bez modyfikacji
- [ ] Uruchomić testy dla wszystkich modułów (model, service)
- [ ] Naprawić ewentualne problemy z kontekstem Spring w testach

#### 6.2 Testy integracyjne
- [ ] Uruchomić aplikację komendą `mvn spring-boot:run`
- [ ] Sprawdzić poprawność inicjalizacji kontekstu Spring
- [ ] Zweryfikować działanie wszystkich funkcjonalności demonstracyjnych
- [ ] Sprawdzić logi aplikacji pod kątem błędów

#### 6.3 Budowanie aplikacji
- [ ] Przetestować komendę `mvn package`
- [ ] Sprawdzić czy generuje się wykonywalny JAR
- [ ] Przetestować uruchamianie JAR-a

### 7. Dokumentacja
**Priorytet: Niski**

#### 7.1 Aktualizacja README.md
- [ ] Dodać sekcję o migracji do Spring Boot
- [ ] Wyjaśnić różne sposoby definiowania beanów (adnotacje, klasy konfiguracyjne, XML)
- [ ] Dodać instrukcje uruchomienia aplikacji
- [ ] Dodać informacje o testowaniu

#### 7.2 Dokumentacja konfiguracji
- [ ] Opisać strukturę pliku `application.properties`
- [ ] Wyjaśnić konfigurację XML w `employees-beans.xml`
- [ ] Opisać klasę konfiguracyjną `AppConfig`

## Harmonogram realizacji

### Faza 1: Konfiguracja podstawowa (1-2 dni)
- Konfiguracja projektu Spring Boot
- Dodanie zależności i application.properties

### Faza 2: Refaktoryzacja serwisów (2-3 dni)
- Refaktoryzacja wszystkich serwisów jako Spring Beany
- Weryfikacja wstrzykiwania zależności

### Faza 3: Konfiguracja XML i beanów (1-2 dni)
- Utworzenie pliku employees-beans.xml
- Konfiguracja zewnętrznych zależności w AppConfig

### Faza 4: Klasa startowa i demonstracja (1-2 dni)
- Implementacja EmployeeManagementApplication
- Demonstracja wszystkich funkcjonalności

### Faza 5: Testy i weryfikacja (1 dzień)
- Uruchomienie i weryfikacja wszystkich testów
- Testowanie aplikacji end-to-end

### Faza 6: Dokumentacja (0.5 dnia)
- Aktualizacja dokumentacji
- Finalne sprawdzenie

## Potencjalne problemy i rozwiązania

### Problem 1: Konflikty z istniejącymi testami
**Rozwiązanie**: Sprawdzić czy testy wymagają kontekstu Spring i ewentualnie dodać odpowiednie adnotacje testowe

### Problem 2: Problemy z wstrzykiwaniem zależności
**Rozwiązanie**: Upewnić się, że wszystkie beany są poprawnie zdefiniowane i że nie ma cyklicznych zależności

### Problem 3: Problemy z konfiguracją XML
**Rozwiązanie**: Sprawdzić poprawność składni XML i czy wszystkie klasy modelu mają odpowiednie konstruktory

### Problem 4: Problemy z uruchomieniem aplikacji
**Rozwiązanie**: Sprawdzić logi aplikacji i czy wszystkie wymagane pliki konfiguracyjne są dostępne

## Kryteria sukcesu

- [ ] Aplikacja uruchamia się bez błędów komendą `mvn spring-boot:run`
- [ ] Wszystkie istniejące testy przechodzą bez modyfikacji
- [ ] Wszystkie serwisy są zarządzane przez Spring Container
- [ ] Pracownicy z pliku XML są poprawnie wstrzykiwani
- [ ] Demonstracja wszystkich funkcjonalności działa poprawnie
- [ ] Dokumentacja jest aktualna i kompletna
