# CSV and API Integration Implementation Plan

## 1. Refactor Employee Model

Rename fields in `Employee.java`:

- `name` → `firstName`
- `surname` → `lastName`

Update all references in `Employee.java`, `EmployeeService.java`, and `App.java`.

## 2. Create New Model Classes

**In `/home/kajtek/Code/aplikacje-przemyslowe2025/zad2/model/src/main/java/com/techcorp/`:**

- `ImportSummary.java`: Contains `int successCount`, `Map<Integer, String> errors`, constructor, getters
- `CompanyStatistics.java`: Contains `long employeeCount`, `double averageSalary`, `String highestPaidEmployee`, constructor, getters, and `toString()`

## 3. Create Exception Classes

**Create new package:** `/home/kajtek/Code/aplikacje-przemyslowe2025/zad2/model/src/main/java/com/techcorp/exception/`

- `InvalidDataException.java`: Checked exception extending `Exception`
- `ApiException.java`: Checked exception extending `Exception`

## 4. Add Gson Dependency

Update `/home/kajtek/Code/aplikacje-przemyslowe2025/zad2/service/pom.xml` to add:

```xml
<dependency>
    <groupId>com.google.code.gson</groupId>
    <artifactId>gson</artifactId>
    <version>2.10.1</version>
</dependency>
```

## 5. Implement ImportService

**In `/home/kajtek/Code/aplikacje-przemyslowe2025/zad2/service/src/main/java/com/techcorp/`:**

Create `ImportService.java`:

- Constructor accepts `EmployeeService`
- `importFromCsv(String filePath)` method:
  - Use `BufferedReader` with try-with-resources
  - Skip header and empty lines
  - Parse CSV: firstName, lastName, email, company, position, salary
  - Validate: Role exists (map "PROGRAMISTA" → ENGINEER), salary > 0
  - Collect errors with line numbers, continue on errors
  - Return `ImportSummary`

## 6. Implement ApiService

**In `/home/kajtek/Code/aplikacje-przemyslowe2025/zad2/service/src/main/java/com/techcorp/`:**

Create `ApiService.java`:

- Constructor accepts `EmployeeService`
- `fetchEmployeesFromApi(String apiUrl)` method:
  - Use `HttpClient` (Java 11+) to GET from API
  - Parse JSON response with Gson (`JsonArray`)
  - Extract: `name` (split to firstName/lastName), `email`, `company.name`
  - Assign all to ENGINEER role with base salary
  - Return `List<Employee>`
  - Throw `ApiException` on HTTP/parsing errors

## 7. Extend EmployeeService

Update existing methods in `EmployeeService.java`:

- Modify `addEmployee(Employee employee)` to:
  - Check for null argument and handle appropriately
  - Return `int` (1 for success, 0 for failure/duplicate)
  - Update App.java accordingly
  
- Modify `removeEmployee(Employee employee)` to:
  - Check for null argument and handle appropriately
  - Return `int` (1 if removed, 0 if not found/null)
  - Update App.java accordingly

Add new methods to `EmployeeService.java`:

- `validateSalaryConsistency()`: Stream API to filter employees where `salary < role.getBaseSalary()`
- `getCompanyStatistics()`: Use `Collectors.groupingBy(Employee::getCompanyName)` with downstream collectors for count, average salary, and max salary employee

## 8. Update Application

Modify `App.java` to add menu options:

- Import from CSV
- Fetch from API
- Show salary validation issues
- Show company statistics (already showing some stats, extend it)

## 9. Create Sample Data

Create `/home/kajtek/Code/aplikacje-przemyslowe2025/zad2/employees.csv`:

```csv
firstName,lastName,email,company,position,salary
John,Doe,john.doe@example.com,TechCorp,ENGINEER,8500
...
```

Include valid rows, invalid role, invalid salary for testing.

## 10. Update Documentation

Update `/home/kajtek/Code/aplikacje-przemyslowe2025/README.md` with:

- Task 2 description
- Build instructions: `mvn clean install`
- Run instructions
- CSV format specification
- API integration details

### To-dos

- [x] Rename name/surname to firstName/lastName in Employee.java and update all references
- [x] Create ImportSummary and CompanyStatistics model classes
- [x] Create exception package with InvalidDataException and ApiException
- [x] Add Gson dependency to service/pom.xml
- [x] Update addEmployee and removeEmployee methods with null handling and int return values
- [x] Implement ImportService with CSV parsing and validation
- [ ] Implement ApiService with HTTP client and JSON parsing
- [ ] Add validateSalaryConsistency and getCompanyStatistics methods
- [ ] Add menu options for CSV import, API fetch, and new analytics
- [ ] Create employees.csv with sample data including test cases
- [ ] Document Task 2 implementation in README.md