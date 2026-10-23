
# Stage 1: Build the Spring Boot application
FROM eclipse-temurin:21-jdk AS build

WORKDIR /app

COPY . .

RUN chmod +x mvnw && ./mvnw -DskipTests package

# Stage 2: Run the application
FROM eclipse-temurin:21-jre

WORKDIR /app

COPY --from=build /app/target/hotel-backend-0.0.1-SNAPSHOT.jar app.jar

EXPOSE 8082

USER 10001

ENTRYPOINT ["java", "-jar", "app.jar"]
