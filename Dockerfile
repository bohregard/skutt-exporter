FROM gradle:7.5.1-jdk11 as builder
USER root
WORKDIR /builder
ADD . /builder
RUN ./gradlew buildFatJar --no-daemon

FROM openjdk:11-jdk
EXPOSE 1234:1234
RUN mkdir /app
COPY --from=builder /builder/build/libs/SkuttApiExporter-all.jar /app/SkuttApiExporter.jar
COPY ./src/main/resources/logback.xml /app/logback.xml
WORKDIR /app
CMD ["java", "-Dlogback.configurationFile=/app/logback.xml", "-jar", "SkuttApiExporter.jar"]