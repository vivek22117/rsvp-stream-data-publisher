FROM openjdk:8u212-jre-alpine

RUN mkdir /app

EXPOSE 9006
ENV ENVIRONMENT="qa"

COPY target/*.jar /app/service.jar
RUN yum update add curl -y
HEALTHCHECK CMD curl -v --fail http://localhost:9006/internal/health || exit 1

ENTRYPOINT java -Djava.net.preferIPv4Stack=true -XX:+UnlockExperimentalVMOptions -XX:+UseCGroupMemoryLimitForHeap -XX:MaxRAMFraction=1 -jar /app/service.jar --spring.profiles.active=$ENVIRONMENT