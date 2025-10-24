# Employee Management System - Spring Boot

System zarządzania pracownikami zbudowany w Spring Boot z wykorzystaniem różnych sposobów konfiguracji beanów.

## Architektura

Aplikacja składa się z trzech modułów:
- **model** - klasy domenowe (Employee, Role, CompanyStatistics)
- **service** - logika biznesowa i serwisy
- **app** - aplikacja Spring Boot z interfejsem użytkownika

## Konfiguracja Spring

### 1. Adnotacje (@Component, @Service, @Configuration)
- Serwisy oznaczone adnotacjami Spring
- Automatyczne skanowanie komponentów
- Konfiguracja w klasie `AppConfig`

### 2. Klasy konfiguracyjne
- `AppConfig` - definicje beanów dla zewnętrznych zależności
- `EmployeeManagementApplicationConfig` - konfiguracja głównej aplikacji

### 3. Konfiguracja XML
- `employees-beans.xml` - definicje pracowników jako beany
- Lista `xmlEmployees` z referencjami do wszystkich beanów
- Import przez `@ImportResource`

## Kompilacja i uruchomienie

### Uruchomienie przez Maven
```bash
cd app
mvn spring-boot:run
```

### Budowanie wykonywalnego JAR
```bash
mvn clean package -DskipTests
java -jar app/target/app-1.0-SNAPSHOT.jar
```

## Konfiguracja

### application.properties
- `app.api.url` - URL dla zewnętrznego API
- `app.api.timeout` - timeout dla żądań HTTP

### employees-beans.xml
Definicje pracowników w XML:
- 6 pracowników firmy XMLCorp
- Różne role: CEO, VP, Manager, Engineer, Intern
- Automatyczne ładowanie przy starcie aplikacji

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

## Funkcjonalności

1. **Zarządzanie pracownikami**
   - Dodawanie/usuwaanie pracowników
   - Listowanie według różnych kryteriów
   - Statystyki i analizy

2. **Import danych**
   - Z pliku CSV
   - Z zewnętrznego API
   - Z konfiguracji XML

3. **Walidacja**
   - Sprawdzanie spójności wynagrodzeń
   - Statystyki firmowe
