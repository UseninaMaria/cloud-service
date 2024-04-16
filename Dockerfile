FROM openjdk:17-jdk-alpine

EXPOSE 8089

ADD target/diplom-cloud-0.0.1-SNAPSHOT.jar diplom-cloud.jar

ENTRYPOINT ["java", "-jar", "diplom-cloud.jar"]