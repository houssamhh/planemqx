FROM ubuntu:focal
USER root
RUN apt-get update
RUN apt-get install -y net-tools iproute2 iputils-ping
RUN apt-get install -y openjdk-11-jdk
RUN apt-get install -y maven
WORKDIR /planemqxPublishers
COPY pom.xml /planemqxPublishers
ADD . /planemqxPublishers
RUN mvn package
RUN mv target/planemqx-publishers-0.0.1-SNAPSHOT-jar-with-dependencies.jar publishers.jar
ENTRYPOINT ["java","-XX:+UseParallelGC","-XX:+AggressiveHeap","-server","-jar","publishers.jar"]
EXPOSE 1883
CMD /bin/bash
