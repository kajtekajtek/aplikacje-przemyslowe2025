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
- [x] Dodać HttpClient jako zależność przez konstruktor
- [x] Dodać Gson jako zależność przez konstruktor
- [x] Dodać pole z adnotacją `@Value("${app.api.url}")` dla URL API
- [x] dostosować testy

### 3. Definicja pracowników jako beany w pliku XML
**Priorytet: Średni**

#### 3.1 Utworzenie pliku konfiguracyjnego XML
- [x] Utworzyć plik `src/main/resources/employees-beans.xml`
- [x] Zdefiniować strukturę XML z odpowiednimi namespace'ami
- [x] Dodać definicje beanów dla pracowników (employee1, employee2, itp.)
- [x] Utworzyć listę `xmlEmployees` z referencjami do wszystkich beanów pracowników

#### 3.2 Konfiguracja wstrzykiwania XML
- [x] Dodać adnotację `@ImportResource("classpath:employees-beans.xml")` do głównej klasy aplikacji
- [x] Sprawdzić dostępność beanów XML w kontekście Spring

### 4. Konfiguracja zewnętrznych zależności jako beany
**Priorytet: Średni**

#### 4.1 Utworzenie klasy konfiguracyjnej AppConfig
- [x] Utworzyć klasę `AppConfig` w pakiecie `config`
- [x] Dodać adnotację `@Configuration`
- [x] Dodać metodę `httpClient()` z adnotacją `@Bean` zwracającą `HttpClient.newHttpClient()`
- [x] Dodać metodę `gson()` z adnotacją `@Bean` zwracającą nową instancję `Gson`

#### 4.2 Weryfikacja wstrzykiwania zależności
- [x] Sprawdzić poprawność wstrzykiwania HttpClient w ApiService
- [x] Sprawdzić poprawność wstrzykiwania Gson w ApiService

### 5. Klasa startowa aplikacji
**Priorytet: Wysoki**

#### 5.1 Utworzenie głównej klasy aplikacji
- [x] Utworzyć klasę `EmployeeManagementApplication` w głównym pakiecie
- [x] Dodać adnotację `@SpringBootApplication`
- [x] Dodać adnotację `@ImportResource("classpath:employees-beans.xml")`
- [x] Zaimplementować interfejs `CommandLineRunner`

#### 5.2 Implementacja metody run()
- [x] Wstrzyknąć wszystkie potrzebne serwisy przez konstruktor:
  - [x] ImportService
  - [x] EmployeeService
  - [x] ApiService
  - [x] List<Employee> xmlEmployees z adnotacją `@Qualifier("xmlEmployees")`
- [x] Zaimplementować demonstrację funkcjonalności:
  - [x] Import pracowników z pliku CSV
  - [x] Dodanie pracowników z beana xmlEmployees
  - [x] Pobranie danych z REST API
  - [x] Wyświetlenie statystyk dla wybranej firmy
  - [x] Walidacja spójności wynagrodzeń
  - [x] Wyświetlenie pracowników z niskimi wynagrodzeniami

### 6. Weryfikacja i testy
**Priorytet: Wysoki**

#### 6.1 Testy jednostkowe
- [x] Sprawdzić czy wszystkie istniejące testy przechodzą bez modyfikacji
- [x] Uruchomić testy dla wszystkich modułów (model, service)
- [x] Naprawić ewentualne problemy z kontekstem Spring w testach

#### 6.2 Testy integracyjne
- [x] Uruchomić aplikację komendą `mvn spring-boot:run`
- [x] Sprawdzić poprawność inicjalizacji kontekstu Spring
- [x] Zweryfikować działanie wszystkich funkcjonalności demonstracyjnych
- [x] Sprawdzić logi aplikacji pod kątem błędów

#### 6.3 Budowanie aplikacji
- [x] Przetestować komendę `mvn package`
- [x] Sprawdzić czy generuje się wykonywalny JAR
- [x] Przetestować uruchamianie JAR-a

### 7. Dokumentacja
**Priorytet: Niski**

#### 7.1 Aktualizacja README.md
- [x] Dodać sekcję o migracji do Spring Boot
- [x] Wyjaśnić różne sposoby definiowania beanów (adnotacje, klasy konfiguracyjne, XML)
- [x] Dodać instrukcje uruchomienia aplikacji
- [x] Dodać informacje o testowaniu

#### 7.2 Dokumentacja konfiguracji
- [x] Opisać strukturę pliku `application.properties`
- [x] Wyjaśnić konfigurację XML w `employees-beans.xml`
- [x] Opisać klasę konfiguracyjną `AppConfig`

## Kryteria sukcesu

- [x] Aplikacja uruchamia się bez błędów komendą `mvn spring-boot:run`
- [x] Wszystkie istniejące testy przechodzą bez modyfikacji
- [x] Wszystkie serwisy są zarządzane przez Spring Container
- [x] Pracownicy z pliku XML są poprawnie wstrzykiwani
- [x] Demonstracja wszystkich funkcjonalności działa poprawnie
- [x] Dokumentacja jest aktualna i kompletna
