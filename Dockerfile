FROM gradle:7.5.1-jdk11 as builder
RUN mkdir /app
WORKDIR /app
ADD . /app
RUN ./gradlew buildFatJar --no-daemon
EXPOSE 8080
CMD ["java", "-Dlogback.configurationFile=/app/logback.xml", "-jar", "./build/libs/SkuttApiExporter-all.jar"]
