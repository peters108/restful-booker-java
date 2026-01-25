# Restful Booker Java - API Test Automation

A complete API Test Automation framework using Java, RestAssured, Cucumber, and TestNG.

## Tech Stack

- **Java 17**
- **RestAssured 5.4.0** - API testing library
- **Cucumber 7.15.0** - BDD framework
- **TestNG 7.9.0** - Test execution framework
- **Allure 2.25.0** - Test reporting
- **Lombok 1.18.30** - Reduce boilerplate code
- **JavaFaker 1.0.2** - Test data generation
- **Owner 1.0.12** - Configuration management
- **Log4j2 2.22.1** - Logging

## Project Structure

```
src/
├── main/
│   └── java/
│       └── com/
│           └── restfulbooker/
│               ├── config/        # Configuration classes
│               ├── model/          # Data models
│               ├── client/         # API client classes
│               ├── specs/          # Request/Response specifications
│               └── data/           # Data factories and providers
└── test/
    ├── java/
    │   └── com/
    │       └── restfulbooker/
    │           ├── runners/        # Test runners
    │           ├── stepdefs/       # Cucumber step definitions
    │           └── hooks/          # Cucumber hooks
    └── resources/
        ├── features/               # Cucumber feature files
        ├── schemas/                # JSON schemas for validation
        ├── config/
        │   └── api.properties      # API configuration
        ├── testng.xml              # TestNG configuration
        ├── log4j2.xml              # Logging configuration
        └── allure.properties       # Allure configuration
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
