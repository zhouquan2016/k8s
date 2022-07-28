#FROM openjdk:8u342-nanoserver
FROM java:8
MAINTAINER zhqn
RUN mkdir /app /app/config -p
COPY /target/dashboard-1.0.0.jar /app
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "-Dspring.config.location=/app/config/", "/app/dashboard-1.0.0.jar"]
