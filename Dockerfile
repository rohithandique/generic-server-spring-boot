FROM eclipse-temurin:21-jre-alpine
WORKDIR /app

COPY build/libs/generic-server-spring-boot.jar /app/
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "generic-server-spring-boot.jar"]