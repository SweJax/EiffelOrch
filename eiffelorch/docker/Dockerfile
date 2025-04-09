FROM eclipse-temurin:21-jdk-jammy

WORKDIR /app

COPY eiffelorch/builds/latest/'eiffelorch-1.0-SNAPSHOT'.jar app.jar

# Run EiffelOrchestrator
CMD ["java", "-jar", "app.jar"]
