FROM openjdk:15-jdk-buster
COPY build/libs/bot-0.1.jar /app.jar
CMD ["java", "-jar", "-Dspring.profiles.active=default", "/app.jar"]
