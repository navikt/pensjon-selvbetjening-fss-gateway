FROM eclipse-temurin:17-jre-alpine-3.20
ENV TZ="Europe/Oslo"
COPY init.sh /init-scripts/init.sh
COPY target/selvbetjening-fss-gateway.jar /app/app.jar

RUN chmod +x /init-scripts/init.sh

ENTRYPOINT ["sh", "-c", "./init-scripts/init.sh && exec java -jar /app/app.jar"]
