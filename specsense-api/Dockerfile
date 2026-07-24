FROM maven:3.8.6-eclipse-temurin-8 AS builder
WORKDIR /app
COPY pom.xml .
COPY src ./src
RUN mvn package -DskipTests

FROM eclipse-temurin:8-jre
WORKDIR /app
COPY --from=builder /app/target/*.jar app.jar
COPY --from=builder /app/src/main/resources /app/resources
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "-Dspring.config.location=/app/resources/", "app.jar"]
