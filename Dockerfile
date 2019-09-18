FROM openjdk:8-jre-alpine
COPY build/libs/bot-all-0.1.jar /app.jar
CMD ["/usr/bin/java", "-jar", "-Dspring.profiles.active=default", "/app.jar"]