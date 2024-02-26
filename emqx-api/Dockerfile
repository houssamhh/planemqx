FROM ubuntu:focal
USER root
RUN apt-get update
RUN apt-get install -y net-tools iproute2 iputils-ping
RUN apt-get install -y openjdk-11-jdk
RUN apt-get install -y maven
#ENV APP_HOME /appPlaniotAPI
WORKDIR /appPlaniotAPI
#RUN groupadd -r spring && useradd -r spring -g spring
#USER spring:spring
COPY pom.xml /appPlaniotAPI
ADD . /appPlaniotAPI
RUN mvn package
#RUN mv ./target/planiot-0.0.1-SNAPSHOT-jar-with-dependencies.jar planiotapi.jar
ENTRYPOINT ["java","-XX:+UseParallelGC","-XX:+AggressiveHeap","-server","-cp","./target/dependency/*","-jar","./target/planiot-api-0.0.1-SNAPSHOT.jar"]
EXPOSE 8080
CMD /bin/bash
