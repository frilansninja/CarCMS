# Multi-stage build for Spring Boot application with frontend

# Stage 1: Build stage
FROM maven:3.9-eclipse-temurin-21 AS build

# Set working directory
WORKDIR /app

# Copy pom.xml and download dependencies (cached layer)
COPY pom.xml .
RUN mvn dependency:go-offline -B

# Copy source code
COPY src ./src
COPY frontend ./frontend

# Build the application (includes frontend build via frontend-maven-plugin)
RUN mvn clean package -DskipTests

# Stage 2: Runtime stage
FROM eclipse-temurin:21-jre

# Set working directory
WORKDIR /app

# Copy the built JAR from build stage
COPY --from=build /app/target/cms-workshop-*.jar app.jar

# Expose port 8080
EXPOSE 8080

# Set environment variables (can be overridden by docker-compose)
ENV SPRING_PROFILES_ACTIVE=prod
ENV JAVA_OPTS="-Xmx512m -Xms256m"

# Run the application
ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar app.jar"]
