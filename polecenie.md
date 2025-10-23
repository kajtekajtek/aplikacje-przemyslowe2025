# Zadanie 4: Migracja do Spring Boot

## Kontekst

System zarządzania pracownikami wymaga refaktoryzacji do architektury Spring Boot z wykorzystaniem mechanizmu Dependency Injection. Dotychczasowa implementacja oparta na ręcznym tworzeniu obiektów i przekazywaniu zależności zostanie zastąpiona zarządzaniem przez kontener Spring. Dodatkowo system zostanie rozszerzony o możliwość definiowania pracowników bezpośrednio jako beany Spring w pliku konfiguracyjnym XML, co pozwoli poznać alternatywny sposób konfiguracji aplikacji Spring oprócz adnotacji.

## Wymagania funkcjonalne

### 1. Konfiguracja projektu Spring Boot

Należy dodać do pom.xml lub build.gradle zależności Spring Boot w wersji 3.x, które obejmują spring-boot-starter oraz spring-boot-starter-test. Dotychczasowa zależność Gson z poprzedniego zadania powinna zostać zachowana, ponieważ jest nadal wykorzystywana do parsowania odpowiedzi z REST API.

Należy utworzyć plik application.properties w katalogu src/main/resources zawierający kluczowe parametry konfiguracyjne aplikacji. Plik powinien definiować adres URL zewnętrznego API jako app.api.url=https://jsonplaceholder.typicode.com/users, ścieżkę do pliku CSV jako app.import.csv-file=employees.csv oraz poziom logowania ustawiony na logging.level.root=INFO.

### 2. Refaktoryzacja serwisów jako Spring Beany

Należy przekształcić wszystkie istniejące klasy serwisów w komponenty zarządzane przez Spring Container poprzez zastosowanie odpowiednich adnotacji stereotypowych.

Klasa EmployeeService powinna zostać oznaczona adnotacją @Service, co sprawi że Spring automatycznie utworzy jej instancję jako singleton bean. Należy usunąć wszelkie statyczne referencje oraz ręczne tworzenie instancji tej klasy w innych miejscach kodu, ponieważ Spring będzie teraz odpowiedzialny za zarządzanie jej cyklem życia.

Klasa ImportService również musi być oznaczona adnotacją @Service i powinna przyjmować EmployeeService jako zależność przez konstruktor. Spring automatycznie wstrzyknie odpowiednią instancję podczas tworzenia beana ImportService. Dzięki mechanizmowi autowiring nie jest konieczne jawne przekazywanie zależności ani używanie adnotacji @Autowired przy konstruktorze w nowszych wersjach Spring Boot.

Klasa ApiService powinna być oznaczona adnotacją @Service i przyjmować przez konstruktor dwa obiekty: HttpClient oraz Gson, które będą zdefiniowane jako beany w klasie konfiguracyjnej. Dodatkowo należy wstrzyknąć adres URL API używając adnotacji @Value("${app.api.url}") na odpowiednim polu lub parametrze konstruktora, co pozwoli na łatwą zmianę adresu bez modyfikacji kodu źródłowego.

### 3. Definicja pracowników jako beany w pliku XML

Należy utworzyć plik konfiguracyjny XML o nazwie employees-beans.xml w katalogu src/main/resources, który będzie definiował obiekty pracowników bezpośrednio jako beany Spring. Jest to alternatywny sposób konfiguracji aplikacji Spring oprócz używania adnotacji i klas konfiguracyjnych z adnotacją @Configuration. Plik XML powinien mieć następującą strukturę:

```
<?xml version="1.0" encoding="UTF-8"?>
<beans xmlns="http://www.springframework.org/schema/beans"
       xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
       xmlns:util="http://www.springframework.org/schema/util"
       xsi:schemaLocation="
           http://www.springframework.org/schema/beans
           http://www.springframework.org/schema/beans/spring-beans.xsd
           http://www.springframework.org/schema/util
           http://www.springframework.org/schema/util/spring-util.xsd">
    
    <bean id="employee1" class="com.techcorp.employee.model.Employee">
        <constructor-arg value="Jan"/>
        <constructor-arg value="Kowalski"/>
        <constructor-arg value="jan.kowalski@techcorp.com"/>
        <constructor-arg value="TechCorp"/>
        <constructor-arg value="MANAGER"/>
        <constructor-arg value="12500"/>
    </bean>
    
    <bean id="employee2" class="com.techcorp.employee.model.Employee">
        <constructor-arg value="Anna"/>
        <constructor-arg value="Nowak"/>
        <constructor-arg value="anna.nowak@techcorp.com"/>
        <constructor-arg value="TechCorp"/>
        <constructor-arg value="PROGRAMISTA"/>
        <constructor-arg value="8500"/>
    </bean>
    
    <!-- Definicja kolejnych pracowników -->
    
    <util:list id="xmlEmployees" value-type="com.techcorp.employee.model.Employee">
        <ref bean="employee1"/>
        <ref bean="employee2"/>
        <!-- Referencje do kolejnych beanów pracowników -->
    </util:list>
</beans>
```

W tym podejściu każdy pracownik jest definiowany jako osobny bean z unikalnym identyfikatorem. Wartości dla pól obiektu Employee są przekazywane przez argumenty konstruktora wykorzystując element <constructor-arg>. Następnie wszystkie referencje do tych beanów są zbierane w liście o nazwie xmlEmployees przy użyciu mechanizmu util:list z przestrzeni nazw util, co pozwala na łatwe wstrzyknięcie całej kolekcji pracowników jako jednego beana.

Aby załadować tę konfigurację XML do aplikacji Spring Boot, należy użyć adnotacji @ImportResource w głównej klasie aplikacji lub w klasie konfiguracyjnej. Adnotacja powinna wskazywać na lokalizację pliku w classpath: @ImportResource("classpath:employees-beans.xml"). Spring automatycznie przetworzy ten plik podczas inicjalizacji kontekstu i utworzy wszystkie zdefiniowane w nim beany, które będą dostępne do wstrzykiwania w innych komponentach aplikacji.

### 4. Konfiguracja zewnętrznych zależności jako beany

Należy utworzyć klasę AppConfig oznaczoną adnotacją @Configuration, która będzie zawierała metody fabrykujące dla obiektów zewnętrznych bibliotek. Każda metoda powinna być oznaczona adnotacją @Bean, co sprawi że Spring zarejestruje zwracany obiekt jako bean dostępny do wstrzykiwania.

Pierwsza metoda powinna tworzyć i zwracać instancję HttpClient poprzez wywołanie HttpClient.newHttpClient(). Ta instancja będzie używana przez ApiService do wykonywania zapytań HTTP do zewnętrznego API.

Druga metoda powinna tworzyć i zwracać nową instancję Gson poprzez wywołanie jej konstruktora domyślnego. Ten obiekt będzie wykorzystywany do parsowania odpowiedzi JSON z zewnętrznego API w obiekty Java.

Dzięki definiowaniu tych obiektów jako beany możliwe jest ich łatwe wykorzystanie w różnych częściach aplikacji poprzez wstrzykiwanie zależności, a także potencjalna podmiana implementacji w środowisku testowym bez modyfikacji klas serwisów.

### 5. Klasa startowa aplikacji

Należy utworzyć klasę EmployeeManagementApplication oznaczoną adnotacjami @SpringBootApplication oraz @ImportResource("classpath:employees-beans.xml"), która będzie głównym punktem wejścia do aplikacji. Adnotacja @ImportResource jest kluczowa, ponieważ nakazuje Spring wczytać definicje beanów z pliku XML podczas inicjalizacji kontekstu. Klasa ta powinna implementować interfejs CommandLineRunner, co pozwoli na automatyczne wykonanie logiki biznesowej po pełnej inicjalizacji kontekstu Spring.

W metodzie run interfejsu CommandLineRunner należy zademonstrować działanie wszystkich kluczowych funkcjonalności systemu. Demonstracja powinna obejmować import pracowników z pliku CSV poprzez wywołanie odpowiedniej metody ImportService, następnie dodanie do systemu pracowników z beana xmlEmployeeswstrzykniętego z użyciem adnotacji @Qualifier("xmlEmployees") lub @Resource(name = "xmlEmployees"), pobranie danych z REST API przez ApiService oraz wyświetlenie statystyk dla wybranej firmy używając metod analitycznych z EmployeeService. Na koniec należy wywołać metodę walidacji spójności wynagrodzeń i wyświetlić pracowników, którzy zarabiają poniżej bazowej stawki dla swojego stanowiska.

Wszystkie niezbędne serwisy oraz bean xmlEmployees typu List<Employee> powinny zostać wstrzyknięte przez konstruktor klasy EmployeeManagementApplication, dzięki czemu Spring automatycznie dostarczy ich instancje podczas tworzenia beana. Warto zauważyć różnicę między definiowaniem beanów przez adnotacje w kodzie Java, przez metody z adnotacją @Bean w klasach konfiguracyjnych oraz przez pliki XML - wszystkie te podejścia prowadzą do tego samego rezultatu, czyli zarządzania obiektami przez kontener Spring, ale oferują różne poziomy elastyczności i czytelności konfiguracji.

Struktura projektu

```
project-root/
├── pom.xml (lub build.gradle)
├── src/
│   ├── main/
│   │   ├── java/com.techcorp.employee/
│   │   │   ├── EmployeeManagementApplication.java
│   │   │   ├── config/
│   │   │   │   └── AppConfig.java
│   │   │   ├── model/
│   │   │   │   ├── Employee.java
│   │   │   │   ├── Position.java
│   │   │   │   ├── ImportSummary.java
│   │   │   │   └── CompanyStatistics.java
│   │   │   ├── service/
│   │   │   │   ├── EmployeeService.java
│   │   │   │   ├── ImportService.java
│   │   │   │   └── ApiService.java
│   │   │   └── exception/
│   │   │       ├── InvalidDataException.java
│   │   │       └── ApiException.java
│   │   └── resources/
│   │       ├── application.properties
│   │       ├── employees.csv
│   │       └── employees-beans.xml (plik z definicjami beanów)
│   └── test/
│       └── java/com.techcorp.employee/
│           └── service/
│               └── (testy z poprzedniego zadania bez zmian)
└── README.md
```

## Wymagania techniczne

Wszystkie klasy serwisów muszą być zarządzane przez Spring Container jako beany. Zabrania się używania operatora new do tworzenia instancji klas oznaczonych adnotacjami stereotypowymi takimi jak @Service czy @Component. Zależności między serwisami należy wstrzykiwać wyłącznie przez konstruktor, unikając wstrzykiwania przez pola czy settery, co jest zgodne z najlepszymi praktykami Spring.

Parametry konfiguracyjne takie jak adresy URL czy ścieżki do plików muszą być pobierane z pliku application.properties poprzez adnotację @Value, co umożliwia łatwą zmianę konfiguracji bez rekompilacji aplikacji. Klasa modelu Employee musi posiadać publiczny konstruktor przyjmujący wszystkie wymagane parametry w odpowiedniej kolejności, aby Spring mógł utworzyć jej instancje na podstawie definicji w pliku XML.

Pracownicy zdefiniowani w pliku employees-beans.xml muszą być dostępni jako bean typu List<Employee> o nazwie xmlEmployees, który można wstrzyknąć używając adnotacji @Qualifier lub @Resource. Aplikacja musi poprawnie startować komendą mvn spring-boot:run lub gradle bootRun i wykonywać wszystkie operacje demonstracyjne bez błędów. Testy jednostkowe z poprzedniego zadania powinny działać bez konieczności jakichkolwiek modyfikacji.

## Polecenia do uruchomienia

Dla użytkowników Maven dostępne są następujące komendy: mvn spring-boot:run uruchamia aplikację, mvn testwykonuje wszystkie testy, a mvn package buduje pakiet JAR wykonywalny.

Dla użytkowników Gradle analogiczne komendy to: gradle bootRun uruchamia aplikację, gradle test wykonuje testy, a gradle build buduje projekt wraz z pakietem wykonywalnym.

## Oddanie

Link do repozytorium z zaktualizowanym kodem zawierającym plik konfiguracyjny pom.xml lub build.gradle z zależnościami Spring Boot, plik application.properties z parametrami konfiguracyjnymi, plik employees-beans.xml z definicjami beanów pracowników w katalogu resources, przykładowy plik employees.csv oraz plik README dokumentujący proces migracji do Spring Boot, wyjaśniający różne sposoby definiowania i konfigurowania beanów (adnotacje, klasy konfiguracyjne, XML) oraz zawierający szczegółową instrukcję uruchomienia i testowania aplikacji.

## Kryteria recenzji:

- Konfiguracja Spring Boot i adnotacje serwisów (25%)
Projekt zawiera poprawne zależności Spring Boot w pom.xml lub build.gradle. Wszystkie klasy serwisów są oznaczone adnotacją @Service z wstrzykiwaniem zależności przez konstruktor bez użycia operatora new. Plik application.properties definiuje wszystkie wymagane parametry konfiguracyjne. Aplikacja poprawnie startuje i inicjalizuje kontekst Spring.
- Konfiguracja beanów w klasie AppConfig (20%)
Klasa AppConfig z adnotacją @Configuration zawiera metody fabrykujące oznaczone @Beandla HttpClient i Gson. Wszystkie beany są prawidłowo wstrzykiwane w serwisach, które ich potrzebują. Metody są poprawnie zaimplementowane i zwracają gotowe do użycia obiekty.
- Definicja pracowników jako beanów w XML (30%)
Klasa EmployeeManagementApplication ma adnotacje @SpringBootApplication i @ImportResource, implementuje CommandLineRunner i demonstruje import z CSV, wykorzystanie beana xmlEmployees, pobieranie z API oraz operacje analityczne. Wszystkie zależności włącznie z listą xmlEmployees są wstrzykiwane przez konstruktor z odpowiednim użyciem @Qualifier lub @Resource.
- Klasa startowa i demonstracja funkcjonalności (25%)
Klasa EmployeeManagementApplication ma adnotacje @SpringBootApplication i @ImportResource, implementuje CommandLineRunner i demonstruje import z CSV, wykorzystanie beana xmlEmployees, pobieranie z API oraz operacje analityczne. Wszystkie zależności włącznie z listą xmlEmployees są wstrzykiwane przez konstruktor z odpowiednim użyciem @Qualifier lub @Resource.