FROM ubuntu:focal
USER root
RUN apt-get update
RUN apt-get install -y net-tools iproute2 iputils-ping
RUN apt-get install -y openjdk-11-jdk
RUN apt-get install -y maven
WORKDIR /planemqxSubscribers
COPY pom.xml /planemqxSubscribers
ADD . /planemqxSubscribers
RUN mvn package
RUN mv target/planemqx-subscribers-0.0.1-SNAPSHOT-jar-with-dependencies.jar subscribers.jar
# ENTRYPOINT ["java","-XX:+UseParallelGC","-XX:+AggressiveHeap","-server","-jar","subscribers.jar"]
EXPOSE 1883
CMD /bin/bash
