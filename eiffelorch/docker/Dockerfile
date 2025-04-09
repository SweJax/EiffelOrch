FROM eclipse-temurin:21-jdk-jammy

WORKDIR /app


COPY builds/latest/'eiffelorch-1.0-SNAPSHOT'.jar ./builds/app.jar

# Run EiffelOrchestrator
CMD ["java", "-jar", "builds/app.jar"]
