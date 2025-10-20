# Zadanie 2

## Uruchomienie

### Kompilacja i uruchomienie

Skryptem:

```bash
./scripts/compile_and_run.sh
```

Lub manualnie:

```bash
mvn clean install
java -jar app/target/app-1.0-SNAPSHOT.jar
```

### Uruchomienie

Skryptem:
```bash
./scripts/run.sh
```

Lub manualnie:

```bash
java -jar app/target/app-1.0-SNAPSHOT.jar
```

### Testy z pokryciem kodu

Uruchomienie testów:
```bash
mvn clean test
```

Uruchomienie testów z raportem pokrycia:
```bash
mvn clean test jacoco:report
```

Raport HTML:
```bash
service/target/site/jacoco/index.html
model/target/site/jacoco/index.html
```
