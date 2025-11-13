# Zadanie: Obsługa plików w aplikacji

## Kontekst

System zarządzania pracownikami wymaga możliwości przesyłania i pobierania plików przez API. Użytkownicy muszą móc importować dane z plików CSV i XML bez dostępu do serwera, generować raporty do pobrania oraz przesyłać dokumenty związane z pracownikami. Zadaniem jest rozszerzenie API o endpointy obsługujące upload i download plików z odpowiednią walidacją i obsługą błędów.

## Wymagania funkcjonalne

### 1. Konfiguracja obsługi plików

W pliku application.properties należy skonfigurować parametry związane z uploadem plików oraz określić katalog, w którym będą przechowywane przesłane pliki. Konfiguracja powinna obejmować maksymalny rozmiar pojedynczego pliku oraz maksymalny rozmiar całego żądania, aby zapobiec przeciążeniu serwera przez zbyt duże przesyłki. Należy również określić katalog roboczy, w którym aplikacja będzie zapisywać przesłane pliki oraz generować raporty do pobrania.

```
spring.servlet.multipart.max-file-size=10MB
spring.servlet.multipart.max-request-size=10MB
spring.servlet.multipart.enabled=true
app.upload.directory=uploads/
app.reports.directory=reports/
```

Katalogi uploads/ i reports/ powinny być tworzone automatycznie przy starcie aplikacji, jeśli nie istnieją. Można to zrealizować w klasie z adnotacją @Component implementującej interfejs CommandLineRunner lub w dedykowanej klasie konfiguracyjnej.

### 2. Serwis do zarządzania plikami

Należy utworzyć serwis FileStorageService z adnotacją @Service, który będzie odpowiedzialny za operacje na plikach. Serwis powinien enkapsulować logikę zapisywania plików na dysku, generowania unikalnych nazw plików aby uniknąć konfliktów, walidacji typów i rozmiarów plików oraz obsługi błędów związanych z operacjami na systemie plików.

Serwis powinien oferować metody do zapisywania pliku przyjmującego obiekt MultipartFile i zwracającego nazwę zapisanego pliku, metodę do odczytywania pliku z dysku zwracającą obiekt Resource, metodę do usuwania pliku oraz metodę walidującą czy plik spełnia wymagania dotyczące rozszerzenia i rozmiaru. Ścieżki do katalogów powinny być wstrzykiwane z application.properties używając adnotacji @Value.

### 3. Upload plików CSV i XML

Należy utworzyć kontroler FileUploadController z mapowaniem @RequestMapping("/api/files") obsługujący przesyłanie plików przez API. Kontroler powinien przyjmować pliki używając typu MultipartFile z adnotacją @RequestParam("file"), która automatycznie mapuje przesłany plik z formularza multipart.

Endpoint POST /api/files/import/csv przyjmuje plik CSV, waliduje jego rozszerzenie i rozmiar, zapisuje go w katalogu uploads, a następnie przekazuje ścieżkę do ImportService który wykonuje import danych. Metoda zwraca obiekt ImportSummary ze szczegółami importu oraz statusem 200 OK przy sukcesie lub odpowiedni kod błędu przy niepowodzeniu.

Analogiczny endpoint POST /api/files/import/xml obsługuje pliki XML. Oba endpointy powinny zwracać szczegółowe informacje o wyniku importu, włączając liczbę zaimportowanych rekordów, liczbę błędów oraz listę konkretnych błędów z numerami linii lub elementów, które nie zostały przetworzone.

### 4. Generowanie i download raportów

Kontroler powinien oferować endpointy do generowania i pobierania raportów w różnych formatach. Kluczowe jest prawidłowe ustawienie nagłówków HTTP, które informują przeglądarkę jak obsłużyć plik - czy wyświetlić go inline czy pobrać jako załącznik.

Endpoint GET /api/files/export/csv generuje plik CSV zawierający wszystkich pracowników i zwraca go jako ResponseEntity<Resource>. Należy ustawić nagłówek Content-Type: text/csv oraz Content-Disposition: attachment; filename="employees.csv", który wymusza pobranie pliku przez przeglądarkę zamiast wyświetlenia go w oknie.

Endpoint GET /api/files/export/csv?company=X generuje raport CSV tylko dla wybranej firmy, co pokazuje jak łączyć parametry query z generowaniem plików.

Endpoint GET /api/files/reports/statistics/{companyName} generuje raport PDF ze statystykami wybranej firmy. Do generowania PDF można użyć biblioteki takiej jak iText lub Apache PDFBox, którą należy dodać do zależności projektu. Raport powinien zawierać sformatowane tabele ze statystykami, wykresy lub inne wizualizacje danych. Nagłówek Content-Type ustawiony na application/pdf informuje przeglądarkę o typie pliku.

### 5. Upload dokumentów pracowniczych

System powinien umożliwiać przesyłanie i przechowywanie dokumentów związanych z konkretnym pracownikiem, takich jak umowy o pracę, certyfikaty, zaświadczenia czy dokumenty tożsamości. Każdy dokument powinien być powiązany z konkretnym pracownikiem przez email oraz posiadać metadane opisujące typ dokumentu i datę przesłania.

Należy utworzyć model EmployeeDocument z polami: id, employeeEmail, fileName, originalFileName, fileType(enum: CONTRACT, CERTIFICATE, ID_CARD, OTHER), uploadDate, filePath. Model powinien być prostą klasą Java bez adnotacji JPA, ponieważ na tym etapie nie używamy jeszcze bazy danych - metadane dokumentów będą przechowywane w pamięci w mapie w serwisie.

Endpoint POST /api/files/documents/{email} przyjmuje email pracownika w ścieżce, plik przez @RequestParam("file") oraz typ dokumentu przez @RequestParam("type"). Metoda waliduje czy pracownik istnieje, zapisuje plik w podkatalogu uploads/documents/{email}/ zachowując organizację plików, tworzy obiekt metadanych i zwraca go użytkownikowi ze statusem 201 Created.

Endpoint GET /api/files/documents/{email} zwraca listę wszystkich dokumentów przypisanych do pracownika jako List<EmployeeDocument> bez samych plików, tylko metadane.

Endpoint GET /api/files/documents/{email}/{documentId} umożliwia pobranie konkretnego dokumentu. Zwraca plik jako Resource z odpowiednimi nagłówkami Content-Type i Content-Disposition.

Endpoint DELETE /api/files/documents/{email}/{documentId} usuwa dokument z dysku i z rejestru metadanych, zwracając status 204 No Content.

### 6. Upload i wyświetlanie zdjęć pracowników

Pracownicy powinni mieć możliwość posiadania zdjęcia profilowego. Zdjęcia wymagają specjalnej walidacji - muszą być w formacie graficznym (JPG, PNG) oraz mieć ograniczony rozmiar aby nie zajmować zbyt dużo miejsca na serwerze.

Endpoint POST /api/files/photos/{email} przyjmuje zdjęcie i zapisuje je w katalogu uploads/photos/ z nazwą odpowiadającą emailowi pracownika. Należy przeprowadzić walidację sprawdzającą czy plik jest obrazem poprzez weryfikację rozszerzenia oraz opcjonalnie analizę nagłówków pliku. Maksymalny rozmiar zdjęcia powinien być ograniczony do 2MB.

Do modelu Employee należy dodać pole photoFileName przechowujące nazwę pliku ze zdjęciem. Po przesłaniu zdjęcia, pole to powinno być aktualizowane w obiekcie pracownika.

Endpoint GET /api/files/photos/{email} zwraca zdjęcie pracownika jako Resource z nagłówkiem Content-Type: image/jpeg lub image/png. Jeśli pracownik nie ma zdjęcia, można zwrócić domyślny placeholder lub status 404 Not Found.

### 7. Walidacja i obsługa błędów plików

Należy utworzyć dedykowane wyjątki dla operacji na plikach: FileStorageException rzucany przy problemach z zapisem pliku na dysku, InvalidFileException rzucany gdy plik nie spełnia wymagań dotyczących typu lub rozmiaru, FileNotFoundException rzucany gdy żądany plik nie istnieje. Wszystkie te wyjątki powinny dziedziczyć po RuntimeException.

W klasie GlobalExceptionHandler należy dodać handlery dla tych wyjątków zwracające odpowiednie kody HTTP. FileStorageException zwraca 500 Internal Server Error ponieważ wskazuje na problem po stronie serwera, InvalidFileException zwraca 400 Bad Request informując że żądanie było niepoprawne, FileNotFoundExceptionzwraca 404 Not Found.

Dodatkowo należy obsłużyć wyjątek MaxUploadSizeExceededException rzucany przez Spring gdy plik przekracza skonfigurowany limit. Handler powinien zwracać 413 Payload Too Large z komunikatem informującym o maksymalnym dozwolonym rozmiarze.

Metoda walidująca pliki w FileStorageService powinna sprawdzać rozszerzenie pliku porównując je z dozwoloną listą, sprawdzać rozmiar pliku przez MultipartFile.getSize() oraz opcjonalnie weryfikować typ MIME przez MultipartFile.getContentType() ponieważ rozszerzenie można łatwo sfałszować ale typ MIME jest trudniejszy do manipulacji.

### 8. Testy z MockMultipartFile

Testy kontrolera obsługującego pliki wymagają specjalnego podejścia ponieważ musimy symulować przesyłanie plików multipart. Spring dostarcza klasę MockMultipartFile która pozwala tworzyć symulowane pliki do testów.

Test uploadu CSV powinien tworzyć MockMultipartFile z przykładową zawartością CSV, wykonywać żądanie multipart() przez MockMvc, mockować metodę serwisu importującego dane i weryfikować status 200 OK oraz zawartość zwróconego ImportSummary.

Test uploadu zbyt dużego pliku powinien tworzyć MockMultipartFile z rozmiarem przekraczającym limit, wykonywać żądanie i weryfikować status 413 Payload Too Large.

Test uploadu pliku z nieprawidłowym rozszerzeniem powinien tworzyć plik z rozszerzeniem .txt podczas gdy endpoint oczekuje .csv, wykonywać żądanie i weryfikować status 400 Bad Request oraz odpowiedni komunikat błędu.

Test downloadu raportu CSV powinien mockować metodę generującą raport, wykonywać żądanie GET, weryfikować status 200 OK, nagłówek Content-Type zawierający text/csv oraz że odpowiedź zawiera spodziewaną zawartość CSV.

Test uploadu dokumentu pracownika powinien weryfikować czy plik jest zapisywany z właściwymi metadanymi oraz czy zwracany jest status 201 Created z obiektem EmployeeDocument.

## Struktura projektu

```
src/
├── main/
│   ├── java/com.techcorp.employee/
│   │   ├── EmployeeManagementApplication.java
│   │   ├── controller/
│   │   │   ├── EmployeeController.java
│   │   │   ├── StatisticsController.java
│   │   │   └── FileUploadController.java (nowy)
│   │   ├── service/
│   │   │   ├── EmployeeService.java
│   │   │   ├── ImportService.java
│   │   │   ├── FileStorageService.java (nowy)
│   │   │   └── ReportGeneratorService.java (nowy)
│   │   ├── model/
│   │   │   ├── Employee.java (z nowym polem photoFileName)
│   │   │   ├── EmployeeDocument.java (nowy)
│   │   │   └── DocumentType.java (nowy enum)
│   │   ├── exception/
│   │   │   ├── GlobalExceptionHandler.java
│   │   │   ├── FileStorageException.java (nowy)
│   │   │   ├── InvalidFileException.java (nowy)
│   │   │   └── FileNotFoundException.java (nowy)
│   │   └── dto/
│   │       └── (DTO z poprzedniego zadania)
│   └── resources/
│       └── application.properties
└── test/
    └── java/com.techcorp.employee/
        ├── controller/
        │   └── FileUploadControllerTest.java (nowy)
        └── service/
            └── FileStorageServiceTest.java (nowy)
```

## Wymagania techniczne

- Kontroler przyjmuje pliki przez parametr metody z adnotacją @RequestParam("file") MultipartFile file, gdzie Spring automatycznie mapuje przesłany plik z żądania multipart. Metody zwracające pliki używają typu ResponseEntity<Resource> gdzie Resource jest interfejsem reprezentującym źródło danych, najczęściej implementowanym przez UrlResource lub ByteArrayResource.
- Nagłówki HTTP ustawiane przez HttpHeaders obiekt dodawany do ResponseEntity. Nagłówek Content-Dispositionokreśla czy plik ma być wyświetlony inline czy pobrany jako attachment. Nagłówek Content-Type informuje przeglądarkę o typie MIME pliku, co jest kluczowe dla prawidłowego wyświetlania lub pobierania.
- Zapis plików na dysku przez Files.copy(file.getInputStream(), targetPath, StandardCopyOption.REPLACE_EXISTING) z obsługą IOException. Odczyt plików przez new UrlResource(filePath.toUri()) który tworzy zasób wskazujący na plik w systemie plików.
- Testy używają MockMultipartFile do tworzenia symulowanych plików oraz metody multipart() zamiast post() w MockMvc do wykonywania żądań multipart. Przy testowaniu downloadów należy weryfikować zarówno status HTTP jak i zawartość oraz nagłówki odpowiedzi.

## Przykłady testowania

Upload pliku CSV:

```
curl -X POST http://localhost:8080/api/files/import/csv \
  -F "file=@employees.csv"
```

Download raportu CSV:

```
curl http://localhost:8080/api/files/export/csv \
  --output employees_export.csv
```

Upload dokumentu pracownika:

```
curl -X POST http://localhost:8080/api/files/documents/jan@example.com \
  -F "file=@contract.pdf" \
  -F "type=CONTRACT"
```

Lista dokumentów pracownika:

```
curl http://localhost:8080/api/files/documents/jan@example.com
```

Upload zdjęcia:

```
curl -X POST http://localhost:8080/api/files/photos/jan@example.com \
  -F "file=@photo.jpg"
```

Pobranie zdjęcia:

```
curl http://localhost:8080/api/files/photos/jan@example.com \
  --output photo.jpg
```

## Oddanie

Link do repozytorium z kodem, testami, przykładowymi plikami CSV i XML oraz README zawierającym listę endpointów do obsługi plików z przykładami curl, instrukcję konfiguracji katalogów oraz opis architektury przechowywania plików.

## Kryteria recenzji:

- Upload plików CSV/XML i import danych (25%)
Endpointy POST /api/files/import/csv i /api/files/import/xml przyjmują MultipartFile, zapisują plik, wywołują import i zwracają ImportSummary. Walidacja formatu i rozmiaru pliku. Obsługa błędów importu.
- Generowanie i download raportów (25%)
Endpointy generujące raporty CSV i PDF. Poprawne ustawienie nagłówków Content-Type i Content-Disposition. Zwracanie ResponseEntity. Parametryzacja raportów (filtrowanie po firmie).
- Dokumenty pracownicze (20%)
System uploadowania, przechowywania i pobierania dokumentów przypisanych do pracowników. Model EmployeeDocument z metadanymi. Endpointy CRUD dla dokumentów. Organizacja plików w katalogach per pracownik.
- Zdjęcia pracowników i walidacja (15%)
Upload zdjęć z walidacją formatu (JPG, PNG) i rozmiaru (max 2MB). Pole photoFileName w modelu Employee. Endpoint do pobierania zdjęć. Obsługa braku zdjęcia. Dedykowane wyjątki (FileStorageException, InvalidFileException) z odpowiednimi handlerami.
- Testy z MockMultipartFile (15%)
Testy używające MockMultipartFile do symulowania uploadów. Testy downloadu raportów z weryfikacją nagłówków i zawartości. Testy błędów (zbyt duży plik, nieprawidłowe rozszerzenie). Mockowanie serwisów.