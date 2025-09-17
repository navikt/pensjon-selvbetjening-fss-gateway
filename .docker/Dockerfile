FROM europe-north1-docker.pkg.dev/cgr-nav/pull-through/nav.no/jre:openjdk-17
ENV TZ="Europe/Oslo"
COPY target/selvbetjening-fss-gateway.jar /app/app.jar

ENTRYPOINT ["java", "-jar", "/app/app.jar"]
