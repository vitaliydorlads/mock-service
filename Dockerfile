FROM eclipse-temurin:21-jdk-jammy

WORKDIR /app

COPY target/mock-service-1.0-SNAPSHOT.jar app.jar

EXPOSE 8080

CMD ["java", "-jar", "app.jar"]