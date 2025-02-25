FROM eclipse-temurin:17-jre-alpine
ENV TZ="Europe/Oslo"
COPY init.sh /init-scripts/init.sh
COPY target/selvbetjening-fss-gateway.jar /app/app.jar

CMD ./init-scripts/init.sh
