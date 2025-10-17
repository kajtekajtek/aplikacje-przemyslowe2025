# Zadanie 3: Testy jednostkowe i automatyzacja buildu

## Kontekst

System zarządzania pracownikami wymaga profesjonalnego podejścia do jakości kodu. Zadaniem jest przygotowanie testów jednostkowych oraz skonfigurowanie wybranego narzędzia buildowego (Maven LUB Gradle) z raportowaniem pokrycia kodu.
Cele edukacyjne

Po wykonaniu tego zadania oczekiwane umiejętności obejmują:
- Konfigurację projektu z JUnit 5 w wybranym narzędziu buildowym
- Pisanie efektywnych testów jednostkowych z użyciem mockowania
- Generowanie i interpretację raportów pokrycia kodu
- Uruchamianie testów i budowanie projektu z linii komend

## Wymagania funkcjonalne

### 1. Wybór i konfiguracja narzędzia buildowego

Należy wybrać JEDNO narzędzie: Maven LUB Gradle

Wymagana konfiguracja obejmuje:
- JUnit 5 (Jupiter) - najnowszą stabilną wersję
- Mockito z integracją JUnit 5
- Plugin do uruchamiania testów
- Plugin JaCoCo do raportowania pokrycia kodu

Aspekty do rozważenia:
- Różnice między JUnit 4 a JUnit 5 w kontekście konfiguracji
- Rola dedykowanego pluginu do uruchamiania testów
- Mechanizm działania JaCoCo w kontekście śledzenia wykonanych linii kodu

### 2. Testy dla EmployeeService 

Analiza wymagana przed implementacją:
- Identyfikacja najbardziej krytycznych metod w EmployeeService
- Określenie potencjalnych problemów w każdej z tych metod
- Rozpoznanie danych wejściowych mogących spowodować nieoczekiwane zachowanie

Scenariusze wymagające pokrycia testami:
- Dodawanie pracownika - obsługa powtarzających się emaili i wartości null
- Wyszukiwanie po firmie - zachowanie przy nieistniejącej firmie
- Średnie wynagrodzenie - obsługa pustej listy pracowników
- Maksymalne wynagrodzenie - typ zwracany i jego znaczenie przy pustej liście
- Walidacja wynagrodzeń - identyfikacja nieprawidłowości

Zagadnienia projektowe:
- Konwencja nazewnictwa metod testowych zapewniająca czytelność w długim okresie
- Przygotowanie danych testowych równoważące czytelność i kompletność
- Zasadność użycia metody @BeforeEach do inicjalizacji wspólnych danych

### 3. Testy dla ImportService 

Kluczowe wyzwanie: Testowanie operacji na plikach

Aspekty wymagające uwagi:
- Podejście do tworzenia plików CSV dla celów testowych
- Zapewnienie przenośności testów między różnymi systemami operacyjnymi
- Gwarancja izolacji testów i braku wzajemnych zależności

Scenariusze do implementacji:
- Poprawny import - weryfikacja trafiania danych do systemu
- Niepoprawne stanowisko - decyzja o przerwaniu lub kontynuacji importu
- Ujemne wynagrodzenie - określenie sposobu obsługi przez system
- Weryfikacja podsumowania - zawartość obiektu ImportSummary

**Wskazówka techniczna**: JUnit 5 udostępnia mechanizm do pracy z tymczasowymi zasobami. Dokumentacja frameworka zawiera informacje o rozwiązaniach dotyczących katalogów tymczasowych.

### 4. Testy dla ApiService z mockowaniem

**Fundamentalne zagadnienie**: Uzasadnienie unikania prawdziwych żądań HTTP w testach jednostkowych

Punkty do analizy:
- Konsekwencje niedostępności API podczas uruchamiania testów
- Możliwość testowania obsługi błędów przy zawsze działającym API
- Zależność testów od zewnętrznych serwisów i jej implikacje

Koncepcja mockowania:
- Definicja mock object i różnice względem prawdziwego obiektu
- Identyfikacja obiektów w ApiService wymagających mockowania
- Mechanizm konfiguracji wartości zwracanych przez mocki

Scenariusze:
- Poprawna odpowiedź JSON - symulacja odpowiedzi bez prawdziwego API
- Błąd HTTP (404, 500) - weryfikacja rzucania wyjątków
- Parsowanie danych - poprawność mapowania z JSON do Employee

Aspekt weryfikacji: Mockito oferuje różne sposoby weryfikacji - analiza czy wystarczające jest sprawdzenie wartości zwracanej, czy konieczna jest również weryfikacja wywołań konkretnych metod.

### 5. Raportowanie pokrycia kodu z JaCoCo

Cel: Minimum 70% pokrycia dla pakietu service

Zagadnienia teoretyczne:
- Znaczenie metryki "70% pokrycia linii"
- Relacja między pokryciem 100% a idealnymi testami
- Różnice między pokryciem linii, gałęzi i ścieżek

Konfiguracja do ustalenia:
- Lokalizacja generowania raportu przez plugin JaCoCo
- Format raportu (HTML, XML, CSV)
- Automatyzacja generowania raportu po wykonaniu testów

### Struktura projektu

    project-root/
    ├── pom.xml (lub build.gradle)
    ├── src/
    │   ├── main/java/...
    │   └── test/
    │       ├── java/
    │       │   └── service/
    │       │       ├── EmployeeServiceTest.java
    │       │       ├── ImportServiceTest.java
    │       │       └── ApiServiceTest.java
    │       └── resources/
    │           └── (opcjonalne pliki testowe)
    └── README.md

### Polecenia do uruchomienia

Należy ustalić standardowe komendy dla wybranego narzędzia obejmujące:
- Uruchamianie wyłącznie testów
- Uruchamianie testów z jednoczesnym generowaniem raportu pokrycia
- Uruchamianie pełnego cyklu budowania z weryfikacją

## Kryteria recenzji:
- Poprawna konfiguracja narzędzia buildowego + JUnit 5 + JaCoCo (25%)
- Testy EmployeeService (30%)
- Testy ImportService (w tym obsługa błędów) (20%)
- Testy ApiService z użyciem mocków (15%)
- Pokrycie ≥70% (10%)