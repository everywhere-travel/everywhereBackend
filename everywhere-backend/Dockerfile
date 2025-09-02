FROM openjdk:21-jdk-slim

ARG JAR_FILE=target/everywhere-backend-0.0.1-SNAPSHOT.jar

COPY ${JAR_FILE} everywhere-backend.jar

EXPOSE 8080

ENTRYPOINT ["java","-jar","everywhere-backend.jar"]
