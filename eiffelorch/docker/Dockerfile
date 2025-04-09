FROM eclipse-temurin:21-jdk-jammy

WORKDIR /app

# Copy the rest of the project
COPY eiffelorch/builds/ ./eiffelorch/builds/

COPY ./eiffelorch/builds/latest/'eiffelorch-1.0-SNAPSHOT'.jar app.jar

# Run EiffelOrchestrator
CMD ["java", "-jar", "app.jar"]
