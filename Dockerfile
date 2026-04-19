# --- STAGE 1: Build ---
# Use a Maven image with Java 17 to build the project
FROM maven:3.8.5-openjdk-17 AS build

# Set the working directory inside the container
WORKDIR /app

# Copy the project definition files (pom.xml)
COPY pom.xml .

# Download dependencies. This is cached to speed up future builds
RUN mvn dependency:go-offline

# Copy the rest of the source code
COPY src ./src

# Compile the application and package it into a .jar
RUN mvn package -DskipTests


# --- STAGE 2: Run ---
# Use a lightweight Java 17 image to run the application
FROM eclipse-temurin:17-jre-alpine

# Set the working directory
WORKDIR /app

# Expose the port on which the Spring Boot application runs (8082)
EXPOSE 8082

# Copy the compiled .jar from the 'build' stage to the current stage
COPY --from=build /app/target/pokedex-0.0.1-SNAPSHOT.jar pokedex.jar

# The command to run the application when the container starts
ENTRYPOINT ["java", "-jar", "pokedex.jar"]
