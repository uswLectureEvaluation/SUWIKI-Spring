FROM openjdk:17-jdk
ENV APP_HOME=/home/ubuntu/suwiki
WORKDIR $APP_HOME
COPY build/libs/*.jar suwiki-server.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "-Dspring.profiles.active=dev", "suwiki-server.jar"]
