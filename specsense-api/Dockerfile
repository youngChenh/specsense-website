FROM maven:3.8.6-openjdk-8-slim AS builder
WORKDIR /app
COPY pom.xml .
COPY src ./src
RUN mvn package -DskipTests

FROM openjdk:8-jre-slim
WORKDIR /app
COPY --from=builder /app/target/*.jar app.jar
COPY --from=builder /app/src/main/resources /app/resources
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "-Dspring.config.location=/app/resources/", "app.jar"]
