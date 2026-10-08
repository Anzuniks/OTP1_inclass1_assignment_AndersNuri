# Temperature Converter

## 1. Assignment Description
This is an individual in-class assignment for the OTP1 course at Metropolia UAS.
The application is a JavaFX desktop app that converts temperatures between Celsius, Fahrenheit and Kelvin and saves each conversion to a MariaDB database, where the conversion history can be viewed and cleared.

**Key requirements:**
- Implement temperature conversion logic in Java with input validation
- Build a graphical user interface with JavaFX
- Store conversion results in a relational database (MariaDB)
- Write unit tests for all layers with JUnit
- Measure code coverage with JaCoCo
- Automate building and testing with a Jenkins pipeline

**Deliverables:**
- Source code and unit tests
- Jenkins pipeline (`Jenkinsfile`)
- This README

## 2. Technologies & Tools Used
- **Language:** Java 21
- **GUI:** JavaFX 21.0.5
- **Build tool:** Maven
- **Database:** MariaDB (accessed via JDBC, `mariadb-java-client`)
- **Test database:** H2 in-memory database, so tests and Jenkins run without a MariaDB server
- **Testing:** JUnit 5
- **Code coverage:** JaCoCo 0.8.14
- **CI/CD:** Jenkins
- **Version control:** Git & GitHub
- **IDE:** IntelliJ IDEA

## 3. Design Approach & Implementation Method
The application follows a **layered architecture**, with each layer in its own package under `fi.metropolia.tempconverter`:

| Package | Responsibility |
|---------|----------------|
| `model` | Data classes `TempRecord` (one conversion) and `TemperatureUnit` (C, F, K) |
| `dao` | Data access objects `TempRecordDAO` and `TemperatureUnitDAO` that handle all SQL queries |
| `db` | `DBConnection` manages the JDBC connection and creates the database schema |
| root | `TempCalculator` contains the conversion logic, `Main` is the JavaFX user interface |

**Database design:**
- `temperature_unit` – the three supported units (Celsius, Fahrenheit, Kelvin) with their code, name and symbol
- `temp_record` – saved conversions, linked to the source and target units with foreign keys, plus a timestamp

**Key decisions:**
- Conversion logic (`TempCalculator`) is separated from the GUI and database code, so it can be unit tested independently.
- The DAO pattern keeps SQL queries out of the rest of the application.
- `DBConnection` creates the schema and seeds the temperature units automatically on startup, only once.
- Database connection settings are read from environment variables, with default values as a fallback.
- Input validation: unit codes are case-insensitive, both dot and comma are accepted as decimal separators, and temperatures below absolute zero are rejected.
- Tests use an H2 in-memory database instead of MariaDB, so the test suite and the Jenkins pipeline run without an external database server.
- A Jenkins pipeline automatically builds the project, runs all tests and generates a JaCoCo coverage report.

## 4. Testing & Quality Assurance

**Automated testing:**
- 30 unit tests written with JUnit 5, covering every layer of the application
- Tests run with `mvn test` and automatically in the Jenkins pipeline
- Code coverage measured with JaCoCo (report in `target/site/jacoco/index.html`)

**Test cases & results:**

| Test class | Tests | Test scenarios | Result |
|------------|-------|----------------|--------|
| `TempCalculatorTest` | 10 | Conversions C→F, F→C, C→K and back, F→K; same unit returns same value; unit codes are case-insensitive; absolute zero is allowed; below absolute zero, unknown unit and null unit throw an exception | ✅ Passed |
| `TempRecordDAOTest` | 4 | Saving returns a generated ID; records are returned with joined units; newest record comes first; delete all removes every record | ✅ Passed |
| `TemperatureUnitDAOTest` | 3 | All three units returned in order; find by code finds an existing unit; unknown code returns empty | ✅ Passed |
| `DBConnectionTest` | 4 | Connection is valid; schema seeds units only once; invalid URL throws; environment settings fall back to defaults | ✅ Passed |
| `TempRecordTest` | 2 | New record has no ID or timestamp; full constructor and setting ID work | ✅ Passed |
| `TemperatureUnitTest` | 3 | Getters return constructor values; symbol has degree sign except Kelvin; `toString` shows name and symbol | ✅ Passed |
| `MainTest` | 4 | Parses dot decimals; parses comma decimals and trims whitespace; rejects empty and invalid input; formats value with unit symbol | ✅ Passed |
| **Total** | **30** | | **✅ 30 passed, 0 failed, 0 skipped** |

**Manual testing:**
- Ran the application and verified conversions with known values (e.g. 0 °C → 32 °F, 100 °C → 212 °F, 0 °C → 273.15 K).
- Tried invalid input (empty field, letters, temperature below absolute zero) and checked that an error message is shown.
- Checked that conversions are saved to MariaDB and appear in the history.

## 5. How to Run

**Prerequisites:**
- JDK 21
- Maven
- MariaDB server (only needed to run the application, not the tests)

**1. Clone the repository**
```bash
git clone [repo-URL]
cd OTP1_inclass1_assignment_AndersNuri
```

**2. Set up the database**

Create an empty database in MariaDB. The tables and temperature units are created automatically when the application starts. Connection settings are read from environment variables; if they are not set, the default values in `DBConnection.java` are used.

**3. Run the tests**
```bash
mvn clean test
```
Coverage report: `target/site/jacoco/index.html`

**4. Run the application**
```bash
mvn javafx:run
```