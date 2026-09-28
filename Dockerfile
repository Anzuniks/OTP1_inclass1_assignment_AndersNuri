FROM eclipse-temurin:17-jre
WORKDIR /app
COPY target/temperature-converter.jar app.jar
ENTRYPOINT ["java", "-jar", "app.jar"]
