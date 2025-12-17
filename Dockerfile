FROM eclipse-temurin:21-jre-alpine
WORKDIR /app

COPY build/libs/generic-server-spring-boot.jar /app/
COPY start.sh /app/
COPY src/main/resources/supabase.crt /app/certs/supabase.crt
RUN chmod +x /app/start.sh
EXPOSE 8080
EXPOSE 9010
ENTRYPOINT ["/app/start.sh"]