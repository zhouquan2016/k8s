FROM openjdk:8u342-nanoserver
MAINTAINER zhqn
RUN mkdir /app /app/config -p
COPY /target/dashboard-1.0.0.jar /app
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "-Dspring.location=/app/config", "dashboard-1.0.0.jar"]