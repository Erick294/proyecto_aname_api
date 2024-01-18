FROM maven:3.8.4-openjdk-17 AS build
COPY . .
RUN mvn clean package -DskipTests

FROM openjdk:17-jdk
COPY --from=build /target/Aname-API-0.0.1-SNAPSHOT.jar Aname-API.jar
EXPOSE 8080
ENTRYPOINT [ "java", "-jar", "Aname-API.jar"]