#FROM gcr.io/distroless/java17-debian12
FROM ghcr.io/navikt/baseimages/temurin:17-appdynamics
ENV TZ="Europe/Oslo"
COPY target/selvbetjening-fss-gateway.jar /app/app.jar

ENTRYPOINT ["java", "-jar", "/app/app.jar"]
