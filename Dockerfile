FROM openjdk:18-alpine
LABEL authors="Max"
WORKDIR /app
VOLUME /app/tmp
EXPOSE 8080
ARG JAR_FILE=target/Amethyst_dev-0.0.1-SNAPSHOT.jar
COPY ${JAR_FILE} /app/app.jar
ENTRYPOINT ["java", "-jar", "/app/app.jar"]