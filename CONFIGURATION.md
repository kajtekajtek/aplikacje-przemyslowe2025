# Dokumentacja konfiguracji

## Struktura konfiguracji Spring

### 1. application.properties

Plik konfiguracyjny zawiera ustawienia aplikacji:

```properties
app.api.url=https://jsonplaceholder.typicode.com/users
app.api.timeout=10000
```

**Parametry:**
- `app.api.url` - domyślny URL dla zewnętrznego API
- `app.api.timeout` - timeout dla żądań HTTP w milisekundach

### 2. employees-beans.xml

Plik XML definiuje pracowników jako beany Spring:

```xml
<?xml version="1.0" encoding="UTF-8"?>
<beans xmlns="http://www.springframework.org/schema/beans"
       xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
       xmlns:util="http://www.springframework.org/schema/util"
       xsi:schemaLocation="...">
    
    <bean id="xmlEmployee1" class="com.techcorp.Employee">
        <constructor-arg name="lastName" value="Anderson"/>
        <constructor-arg name="firstName" value="Thomas"/>
        <constructor-arg name="emailAddress" value="thomas.anderson@xmlcorp.com"/>
        <constructor-arg name="companyName" value="XMLCorp"/>
        <constructor-arg name="role" value="CEO"/>
        <constructor-arg name="salary" value="30000"/>
    </bean>
    
    <util:list id="xmlEmployees" value-type="com.techcorp.Employee">
        <ref bean="xmlEmployee1"/>
        <ref bean="xmlEmployee2"/>
        <!-- ... więcej pracowników ... -->
    </util:list>
</beans>
```

**Elementy:**
- Definicje 6 pracowników firmy XMLCorp
- Lista `xmlEmployees` z referencjami do wszystkich beanów
- Automatyczne ładowanie przy starcie aplikacji

### 3. AppConfig.java

Klasa konfiguracyjna definiuje beany dla zewnętrznych zależności:

```java
@Configuration
public class AppConfig {
    @Bean
    public HttpClient httpClient() {
        return HttpClient.newBuilder()
                .version(HttpClient.Version.HTTP_2)
                .connectTimeout(Duration.ofSeconds(10))
                .build();
    }

    @Bean
    public Gson gson() {
        return new GsonBuilder().create();
    }
}
```

**Beany:**
- `httpClient` - klient HTTP dla komunikacji z API
- `gson` - parser JSON dla deserializacji danych

### 4. EmployeeManagementApplicationConfig.java

Konfiguracja głównej aplikacji:

```java
@Configuration
public class EmployeeManagementApplicationConfig {
    @Bean
    public Scanner scanner() {
        return new Scanner(System.in);
    }
}
```

**Beany:**
- `scanner` - skaner do odczytu danych z konsoli

## Sposoby definiowania beanów

### 1. Adnotacje
- `@Component` - ogólne komponenty
- `@Service` - serwisy biznesowe
- `@Repository` - warstwa dostępu do danych
- `@Configuration` - klasy konfiguracyjne

### 2. Klasy konfiguracyjne
- Metody oznaczone `@Bean`
- Wstrzykiwanie zależności przez konstruktor
- Konfiguracja zewnętrznych bibliotek

### 3. Konfiguracja XML
- Definicje beanów w plikach XML
- Import przez `@ImportResource`
- Listy i kolekcje beanów

## Wstrzykiwanie zależności

### Konstruktor (preferowane)
```java
public EmployeeManagementApplication(
    EmployeeService employeeService,
    ImportService importService,
    ApiService apiService,
    Scanner scanner,
    List<Employee> xmlEmployees
) {
    // ...
}
```

### Adnotacje
```java
@Autowired
@Qualifier("xmlEmployees")
private List<Employee> xmlEmployees;
```

## Import konfiguracji XML

```java
@SpringBootApplication
@ImportResource("classpath:employees-beans.xml")
public class EmployeeManagementApplication {
    // ...
}
```

## Profile Spring

Aplikacja używa domyślnego profilu "default". Można dodać profile dla różnych środowisk:

```properties
spring.profiles.active=dev
```
