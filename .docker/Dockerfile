FROM eclipse-temurin:17-jre-alpine-3.21
ENV TZ="Europe/Oslo"
COPY init.sh /init-scripts/init.sh
COPY target/selvbetjening-fss-gateway.jar /app/app.jar

RUN chmod +x /init-scripts/init.sh

CMD ["./init-scripts/init.sh"]
