# Use Java 17
FROM eclipse-temurin:17-jdk-alpine

# Set working directory
WORKDIR /app

# Copy maven files first
COPY pom.xml .
COPY src ./src

# Install Maven and build
RUN apk add --no-cache maven
RUN mvn clean package -DskipTests

# Run the jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "target/finance-backend-pgvector-1.0.0.jar"]