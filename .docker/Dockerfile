FROM ghcr.io/navikt/baseimages/temurin:17-appdynamics
ENV TZ="Europe/Oslo"
COPY init.sh /init-scripts/init.sh
COPY target/selvbetjening-fss-gateway.jar /app/app.jar

CMD ./init-scripts/init.sh
