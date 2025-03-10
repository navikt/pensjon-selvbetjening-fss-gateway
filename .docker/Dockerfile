FROM gcr.io/distroless/java17-debian12
ENV TZ="Europe/Oslo"
COPY target/selvbetjening-fss-gateway.jar /app/app.jar

ENTRYPOINT ["java", "-jar", "/app/app.jar"]
