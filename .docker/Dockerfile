FROM europe-north1-docker.pkg.dev/cgr-nav/pull-through/nav.no/jre:openjdk-25@sha256:9d01a2d663066dc143129c956e4ffde1a725bccb248fdad753b8bf3ba044455f
ENV TZ="Europe/Oslo"
COPY target/selvbetjening-fss-gateway.jar /app/app.jar

ENTRYPOINT ["java", "-jar", "/app/app.jar"]
