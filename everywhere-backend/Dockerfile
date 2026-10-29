FROM eclipse-temurin:21-jre-jammy
WORKDIR /app
COPY target/everywhere-backend-0.0.1-SNAPSHOT.jar everywhere-backend.jar
EXPOSE 8080
ENTRYPOINT ["java","-jar","everywhere-backend.jar"]