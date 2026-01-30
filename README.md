# Restful Booker Java - API Test Automation

API test automation framework using Java, RestAssured, Cucumber, and TestNG.

## Tech Stack

- **Java 17**
- **RestAssured 5.4.0** - API testing
- **Cucumber 7.15.0** - BDD
- **TestNG 7.9.0** - Test execution
- **Allure 2.25.0** - Reporting
- **Lombok 1.18.38** - Boilerplate reduction
- **JavaFaker 1.0.2** - Test data
- **Owner 1.0.12** - Configuration
- **Jackson 2.16.1** - JSON (serialization)
- **Log4j2 2.22.1** - Logging

## Project Structure

```
src/
├── main/
│   ├── java/com/restfulbooker/
│   │   ├── config/           # Configuration
│   │   ├── model/
│   │   │   ├── request/      # Request DTOs
│   │   │   └── response/     # Response DTOs
│   │   ├── client/           # API clients
│   │   └── specs/            # Request/response specs
│   └── resources/
│       └── config/
│           └── api.properties
└── test/
    ├── java/com/restfulbooker/
    │   ├── context/          # Scenario context
    │   ├── hooks/            # Cucumber hooks
    │   ├── parameters/       # Cucumber parameter types
    │   ├── runners/          # TestNG/Cucumber runners
    │   └── stepdefs/         # Step definitions
    └── resources/
        ├── features/         # Gherkin features
        ├── schemas/          # JSON schemas
        ├── config/
        │   └── api.properties
        ├── testng.xml
        ├── log4j2.xml
        └── allure.properties
```

## Getting Started

### Prerequisites

- Java JDK 17 or higher
- Maven 3.6+ (or use Maven Wrapper)

### Running Tests

```bash
# Run all tests
mvn clean test

# Run with Allure report
mvn clean test allure:serve
```

## Configuration

API configuration can be managed through `src/test/resources/config/api.properties` or via system properties:

```bash
mvn test -Dapi.base.uri=http://localhost -Dapi.port=3001
```

## License

MIT
