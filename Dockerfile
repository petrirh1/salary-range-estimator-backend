# --------- Build Stage ---------
FROM maven:3.9.8-eclipse-temurin-21 AS builder

# Set working directory
WORKDIR /app

# Copy pom.xml and download dependencies first (layer caching)
COPY pom.xml .
# RUN mvn dependency:go-offline -B

# Copy the source code and build the app
COPY src ./src
RUN mvn clean package -DskipTests

# --------- Runtime Stage ---------
FROM eclipse-temurin:21-jre

# Set working directory
WORKDIR /app

# Copy the fat JAR from the build stage
COPY --from=builder /app/target/*.jar app.jar

# Render provides PORT, so expose it
EXPOSE 8080

# Run the Spring Boot app
CMD ["sh", "-c", "java -jar app.jar --server.port=$PORT"]
