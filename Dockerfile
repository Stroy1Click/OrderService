FROM eclipse-temurin:21-jre-alpine
LABEL authors="egorm"

WORKDIR /app
COPY target/order-service-0.0.1-SNAPSHOT.jar /app/order.jar
EXPOSE 1010
ENTRYPOINT ["java", "-jar", "order.jar"]