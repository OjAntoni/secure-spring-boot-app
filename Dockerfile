FROM maven:3.8.3-openjdk-17-slim AS build
COPY src /home/app/src
COPY pom.xml /home/app
RUN mvn -f /home/app/pom.xml clean package -DskipTests

FROM openjdk:17
COPY --from=build /home/app/target/ochrona-0.0.1-SNAPSHOT.jar ochrona-0.0.1-SNAPSHOT.jar
#COPY ./target/ochrona-0.0.1-SNAPSHOT.jar ochrona-0.0.1-SNAPSHOT.jar
CMD ["java","-jar","ochrona-0.0.1-SNAPSHOT.jar"]
EXPOSE 8443