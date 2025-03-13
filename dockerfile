# Use OpenJDK 23 base image
FROM openjdk:23-jdk-slim AS build

# Set the working directory inside the container
WORKDIR /app

# Copy the jar file from the target directory into the container
COPY target/wrappedup-backend-0.0.1-SNAPSHOT.jar /app/wrappedup-backend.jar

# Run the application
ENTRYPOINT ["java", "-jar", "/app/wrappedup-backend.jar"]
