# Dockerfile
FROM maven:3.9-eclipse-temurin-17-alpine

WORKDIR /app

# Copy pom.xml first for dependency caching
COPY pom.xml .
RUN mvn dependency:go-offline

# Copy source code
COPY src ./src

# Run tests
CMD ["mvn", "clean", "test"]
