FROM openjdk:21
LABEL authors="egorm"

WORKDIR /app
ADD maven/Stroy1Click-OrderService-0.0.1-SNAPSHOT.jar /app/order.jar
EXPOSE 1010
ENTRYPOINT ["java", "-jar", "order.jar"]