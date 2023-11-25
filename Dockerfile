FROM openjdk:17-oracle

EXPOSE 8080

WORKDIR /app

COPY build/libs/*.jar app.jar

ENTRYPOINT ["java","-Djava.security.egd=file:/dev/./urandom","-jar","app.jar"]
