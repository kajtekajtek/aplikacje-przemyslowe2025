# Zadanie 7: Interfejs użytkownika webowego

## Kontekst

System zarządzania pracownikami posiada API REST umożliwiające programistyczną integrację, ale wymaga również interfejsu graficznego dla użytkowników końcowych. Zadaniem jest stworzenie aplikacji webowej z dynamicznymi widokami HTML, formularzami oraz kontrolerami MVC, które będą renderować strony zamiast zwracać JSON.

## Wymagania funkcjonalne

### 1. Konfiguracja Thymeleaf

- Dodać zależność spring-boot-starter-thymeleaf do projektu.

Konfiguracja w application.properties:

```
spring.thymeleaf.cache=false
spring.thymeleaf.prefix=classpath:/templates/
spring.thymeleaf.suffix=.html
spring.thymeleaf.mode=HTML
```

Wyłączenie cache pozwala na widoczność zmian w widokach bez restartu aplikacji podczas developmentu. W produkcji cache powinien być włączony dla wydajności.

### 2. Struktura widoków i wspólny layout

- Utworzyć katalog src/main/resources/templates/ zawierający szablony HTML. Wszystkie widoki powinny dziedziczyć ze wspólnego layoutu zawierającego nawigację i podstawową strukturę strony.
- Plik layout.html powinien zawierać sekcję <head> z linkami do stylów CSS, nawigację z linkami do głównych sekcji aplikacji (Pracownicy, Departamenty, Statystyki, Pliki) oraz sekcję <main> gdzie będzie wstawiany konkretny widok. Można użyć Thymeleaf Layout Dialect lub prostych fragmentów.
- Podstawowy plik style.css w katalogu src/main/resources/static/css/ powinien zawierać style dla tabel z obramowaniem i efektem hover, style dla formularzy z wyraźnymi polami input i przyciskami, style dla komunikatów sukcesu i błędów oraz podstawowy układ strony z centrowaniem contentu.

### 3. Kontroler MVC dla pracowników

- Klasa EmployeeViewController z adnotacją @Controller i @RequestMapping("/employees") obsługuje widoki dla pracowników. Różnica między @Controller a @RestController polega na tym, że metody zwracają nazwy widoków zamiast danych JSON, a Spring renderuje odpowiedni szablon Thymeleaf.
- Endpoint GET /employees zwraca widok z listą wszystkich pracowników. Metoda przyjmuje obiekt Model do którego dodaje listę pracowników przez model.addAttribute("employees", employeeService.getAllEmployees()) i zwraca String "employees/list" wskazujący na szablon templates/employees/list.html.
- Endpoint GET /employees/add zwraca formularz dodawania nowego pracownika. Metoda dodaje do modelu pusty obiekt Employee przez model.addAttribute("employee", new Employee()) który będzie bindowany do formularza, oraz listę wszystkich możliwych wartości enum Position i EmploymentStatus potrzebnych do wypełnienia selectów w formularzu.
- Endpoint POST /employees/add przetwarza formularz używając @ModelAttribute Employee employee co automatycznie binduje pola z formularza do obiektu. Metoda zapisuje pracownika przez serwis, dodaje komunikat sukcesu do RedirectAttributes używając redirectAttributes.addFlashAttribute("message", "Pracownik dodany pomyślnie") i wykonuje przekierowanie return "redirect:/employees". Wzorzec Post-Redirect-Get zapobiega ponownemu wysłaniu formularza przy odświeżeniu strony.
- Endpoint GET /employees/edit/{email} zwraca formularz edycji pracownika. Metoda pobiera pracownika po emailu, dodaje go do modelu oraz listy enumów i zwraca widok formularza edycji.
- Endpoint POST /employees/edit przetwarza aktualizację danych używając @ModelAttribute i przekierowuje po zapisie.
- Endpoint GET /employees/delete/{email} usuwa pracownika i przekierowuje do listy z komunikatem. Dla operacji DELETE można użyć formularza z ukrytym polem _method i filtra HiddenHttpMethodFilter, ale prostsza implementacja przez GET jest akceptowalna w tym zadaniu.
- Endpoint GET /employees/search wyświetla formularz wyszukiwania po firmie z polem tekstowym.
- Endpoint POST /employees/search przetwarza wyszukiwanie przyjmując @RequestParam("company") String company, filtruje pracowników i zwraca widok z wynikami.

### 4. Widoki Thymeleaf dla pracowników

- Plik templates/employees/list.html wyświetla tabelę z pracownikami. Nagłówek zawiera link do formularza dodawania oraz ewentualny komunikat flash. Tabela używa th:each="employee : ${employees}" do iteracji po kolekcji pracowników, th:text="${employee.firstName}" do wyświetlania wartości pól oraz th:href="@{/employees/edit/{email}(email=${employee.email})}" do generowania dynamicznych linków. Kolumna akcji zawiera linki do edycji i usuwania.
- Plik templates/employees/add-form.html zawiera formularz z atrybutami th:action="@{/employees/add}", th:object="${employee}" bindującym formularz do obiektu oraz th:field="*{firstName}" dla każdego pola co automatycznie generuje atrybuty name, id i value oraz binduje wartości w obie strony. Select dla Position używa th:each="pos : ${positions}" do iteracji po enumie i th:value="${pos}" oraz th:text="${pos}" do wypełnienia opcji.
- Plik templates/employees/edit-form.html jest analogiczny do add-form ale action wskazuje na /employees/edit a pola są wypełnione wartościami edytowanego pracownika.
- Plik templates/employees/search-form.html zawiera prosty formularz z jednym polem tekstowym dla nazwy firmy.
- Plik templates/employees/search-results.html wyświetla wyniki wyszukiwania w analogicznej tabeli jak list.html ale z dodatkową informacją o kryterium wyszukiwania.

### 5. Nowa funkcjonalność: Departamenty

- Wprowadzić nową encję domenową reprezentującą departament w firmie. Klasa Department powinna zawierać pola: id jako Long, name jako String, location jako String opisujący lokalizację biura departamentu, budget jako double reprezentujący budżet departamentu oraz managerEmail jako String wskazujący na pracownika będącego managerem departamentu.
- Serwis DepartmentService zarządza departamentami przechowując je w pamięci w mapie gdzie kluczem jest id. Serwis oferuje metody CRUD analogiczne do EmployeeService: dodawanie nowego departamentu z automatycznym generowaniem id, pobieranie wszystkich departamentów, pobieranie po id, aktualizację oraz usuwanie.
- Kontroler DepartmentViewController z mapowaniem /departments obsługuje widoki dla departamentów implementując analogiczne endpointy jak dla pracowników: listę, formularz dodawania, formularz edycji oraz usuwanie. Formularz departamentu zawiera select do wyboru managera z listy wszystkich pracowników na stanowisku MANAGER lub wyższym.
- Widoki w katalogu templates/departments/ zawierają list.html z tabelą departamentów pokazującą nazwę, lokalizację, budżet i nazwisko managera (pobrane z EmployeeService po emailu), form.html z polami dla wszystkich atrybutów departamentu oraz selectem dla managera oraz details.html pokazujący szczegóły departamentu wraz z listą pracowników przypisanych do tego departamentu (rozszerzenie modelu Employee o pole departmentId).

### 6. Upload plików przez formularz HTML

- Rozszerzyć kontroler o endpoint GET /employees/import zwracający formularz z polem input typu file do wyboru pliku CSV lub XML oraz radio buttons lub select do wyboru typu pliku. Atrybut enctype="multipart/form-data" w tagu form jest wymagany dla uploadów plików.
- Endpoint POST /employees/import przyjmuje @RequestParam("file") MultipartFile file oraz @RequestParam("fileType") String fileType, wywołuje odpowiednią metodę ImportService i przekierowuje do listy pracowników z komunikatem zawierającym ImportSummary (liczba zaimportowanych, liczba błędów). W przypadku błędów można wyświetlić szczegółową listę błędów w osobnym widoku.
- Analogicznie endpoint GET /departments/documents/{id} wyświetla listę dokumentów związanych z departamentem z możliwością uploadu nowych plików oraz downloadu istniejących. Upload wykorzystuje endpoint z poprzedniego zadania ale wywołany z formularza HTML zamiast przez API.

### 7. Wyświetlanie statystyk

- Kontroler StatisticsViewController z mapowaniem /statistics oferuje endpoint GET /statistics wyświetlający dashboard ze statystykami. Widok zawiera sekcję z ogólnymi statystykami (liczba pracowników, średnie wynagrodzenie, liczba departamentów), sekcję z tabelą statystyk per firma (wykorzystanie CompanyStatistics z serwisu) oraz sekcję z rozkładem pracowników po stanowiskach wyświetlaną jako lista lub prosta wizualizacja tekstowa.
- Można dodać endpoint GET /statistics/company/{name} wyświetlający szczegółową stronę ze statystykami konkretnej firmy zawierającą wykresy lub bardziej rozbudowane analizy. Na tym etapie wystarczą proste tabele bez JavaScript, ale można wspomnieć w README o możliwości rozszerzenia o wykresy używając bibliotek jak Chart.js.

### 8. Walidacja formularzy i wyświetlanie błędów

- Kontrolery powinny sprawdzać poprawność danych z formularzy. W metodach POST można używać BindingResult zaraz po parametrze @ModelAttribute do przechwytywania błędów bindowania. Jeśli bindingResult.hasErrors() jest true, metoda zwraca ponownie widok formularza zachowując wprowadzone dane i wyświetlając błędy.
- W widokach Thymeleaf błędy wyświetlane są przez th:if="${#fields.hasErrors('firstName')}" sprawdzający czy dane pole ma błąd oraz th:errors="*{firstName}" wyświetlający komunikat błędu. Ogólne komunikaty błędów wyświetlane są przez th:if="${#fields.hasAnyErrors()}".
- Flash messages przekazywane przez RedirectAttributes wyświetlane są w layoutcie lub na początku każdej strony używając th:if="${message}" i th:text="${message}" z odpowiednim stylowaniem CSS dla komunikatów sukcesu (zielone tło) i błędów (czerwone tło).

## Struktura projektu

```
src/
├── main/
│   ├── java/com.techcorp.employee/
│   │   ├── controller/
│   │   │   ├── EmployeeController.java (REST z zadania 5)
│   │   │   ├── FileUploadController.java (z zadania 6)
│   │   │   ├── EmployeeViewController.java (nowy MVC)
│   │   │   ├── DepartmentViewController.java (nowy MVC)
│   │   │   └── StatisticsViewController.java (nowy MVC)
│   │   ├── service/
│   │   │   ├── EmployeeService.java
│   │   │   └── DepartmentService.java (nowy)
│   │   └── model/
│   │       ├── Employee.java (z polem departmentId)
│   │       └── Department.java (nowy)
│   └── resources/
│       ├── templates/
│       │   ├── layout.html
│       │   ├── index.html (strona główna)
│       │   ├── employees/
│       │   │   ├── list.html
│       │   │   ├── add-form.html
│       │   │   ├── edit-form.html
│       │   │   ├── search-form.html
│       │   │   ├── search-results.html
│       │   │   └── import-form.html
│       │   ├── departments/
│       │   │   ├── list.html
│       │   │   ├── form.html
│       │   │   └── details.html
│       │   └── statistics/
│       │       └── index.html
│       ├── static/
│       │   └── css/
│       │       └── style.css
│       └── application.properties
└── test/
    └── java/com.techcorp.employee/
        └── controller/
            ├── EmployeeViewControllerTest.java (nowy)
            └── DepartmentViewControllerTest.java (nowy)
```

## Wymagania techniczne

- Kontrolery MVC używają adnotacji @Controller zamiast @RestController. Metody zwracają nazwy widoków jako String co powoduje renderowanie szablonu Thymeleaf zamiast zwracanie danych JSON. Parametr Model wstrzykiwany do metod służy do przekazywania danych do widoku przez model.addAttribute().
- Formularze bindowane do obiektów używając th:object i th:field. Atrybuty th:field automatycznie generują name, id oraz bindują wartości w obie strony (wyświetlanie i przechwytywanie). Przekierowania po POST używają wzorca Post-Redirect-Get dla uniknięcia ponownego wysłania formularza przy refresh.
- Flash messages przekazywane przez RedirectAttributes.addFlashAttribute() przetrwają jedno przekierowanie i będą dostępne w widoku docelowym. URL-e generowane przez @{/path} co automatycznie dodaje context path aplikacji.
- Testy kontrolerów MVC używają @WebMvcTest i MockMvc analogicznie jak dla REST API, ale weryfikują zwracane nazwy widoków używając andExpect(view().name("employees/list")) oraz obecność atrybutów w modelu przez andExpect(model().attributeExists("employees")).

## Przykłady dostępu

Po uruchomieniu aplikacji otwórz przeglądarkę i przejdź do:

```
    http://localhost:8080/ - strona główna
    http://localhost:8080/employees - lista pracowników
    http://localhost:8080/employees/add - formularz dodawania
    http://localhost:8080/departments - lista departamentów
    http://localhost:8080/statistics - dashboard statystyk
    http://localhost:8080/employees/import - formularz importu plików
```

## Oddanie

- Link do repozytorium z kodem kontrolerów MVC, widoków Thymeleaf, pliku CSS oraz README zawierającym screenshoty głównych stron aplikacji, opis nawigacji między sekcjami oraz instrukcję uruchomienia i testowania przez przeglądarkę.

## Kryteria recenzji:

- **Kontrolery MVC i routing (25%)**
Wszystkie kontrolery używają @Controller i zwracają nazwy widoków. Przekazywanie danych przez Model. Obsługa formularzy z @ModelAttribute. Poprawne przekierowania po POST z flash messages. Wzorzec Post-Redirect-Get.
- **Widoki Thymeleaf (30%)**
Wszystkie wymagane widoki utworzone z prawidłową składnią Thymeleaf. Używanie th:each, th:text, th:href, th:object, th:field. Wspólny layout z nawigacją. Dynamiczne generowanie URL przez @{...}. Wyświetlanie komunikatów i błędów.
- **Formularze z bindowaniem (20%)**
Formularze poprawnie bindowane do obiektów. Pola th:field generują atrybuty i wartości. Selecty dla enumów. Walidacja danych i wyświetlanie błędów. Upload plików przez formularz HTML z enctype="multipart/form-data".
- **Departamenty jako nowa funkcjonalność (15%)**
Model Department i serwis DepartmentService utworzone. Widoki CRUD dla departamentów. Select do wyboru managera z listy pracowników. Szczegóły departamentu z listą przypisanych pracowników. Pole departmentId w Employee.
- **Style CSS i UX (10%)**
Podstawowy plik CSS ze stylami dla tabel, formularzy, komunikatów. Efekt hover na tabelach. Wyraźne przyciski i pola. Nawigacja między sekcjami. Komunikaty sukcesu i błędów z odpowiednimi kolorami. Responsywny układ.
