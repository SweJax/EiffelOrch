FROM eclipse-temurin:21-jdk-jammy

WORKDIR /app

COPY builds/latest/'eiffelorch-latest'.jar app.jar

ENTRYPOINT ["java", "-jar", "app.jar"]

