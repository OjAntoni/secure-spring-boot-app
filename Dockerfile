FROM openjdk:17
COPY ./target/ochrona-0.0.1-SNAPSHOT.jar ochrona-0.0.1-SNAPSHOT.jar
CMD ["java","-jar","ochrona-0.0.1-SNAPSHOT.jar"]
EXPOSE 8443