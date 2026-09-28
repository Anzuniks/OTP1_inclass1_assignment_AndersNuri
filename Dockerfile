# ---------- Build stage ----------
FROM maven:3.9-eclipse-temurin-21 AS build
WORKDIR /app
COPY pom.xml .
RUN mvn -B -q dependency:go-offline
COPY src ./src
# Tests are run by Jenkins (mvn verify), so they are skipped here
RUN mvn -B -q -DskipTests package

# ---------- Runtime stage ----------
FROM eclipse-temurin:21-jre-jammy

# Native libraries JavaFX needs to open a window on an X server
RUN apt-get update && apt-get install -y --no-install-recommends \
        libgtk-3-0 libxtst6 libxxf86vm1 libgl1 fonts-dejavu-core \
    && rm -rf /var/lib/apt/lists/*

WORKDIR /app
COPY --from=build /app/target/temperature-converter.jar app.jar

ENV DB_URL=jdbc:mariadb://db:3306/tempdb \
    DB_USER=tempuser \
    DB_PASSWORD=temppass

# prism.order=sw = software rendering, avoids GPU/OpenGL problems over X11
CMD ["java", "-Dprism.order=sw", "-jar", "app.jar"]
