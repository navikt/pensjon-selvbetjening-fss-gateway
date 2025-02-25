FROM eclipse-temurin:17-jre AS builder
COPY init.sh /init-scripts/init.sh
COPY target/selvbetjening-fss-gateway.jar /app/app.jar

FROM eclipse-temurin:17-jre-alpine
COPY --from=builder /app/app.jar /app/
COPY --from=builder /init-scripts/init.sh /init-scripts/

CMD ./init-scripts/init.sh
