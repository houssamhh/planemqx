FROM ubuntu:focal
USER root
RUN apt-get update
RUN apt-get install -y net-tools iproute2 iputils-ping
RUN apt-get install -y openjdk-11-jdk
RUN apt-get install -y maven
WORKDIR /appPlaniot
COPY pom.xml /appPlaniot
#RUN mvn verify --fail-never
ADD . /appPlaniot
RUN mvn package
RUN mv ./target/planemqx-agent-0.0.1-SNAPSHOT-jar-with-dependencies.jar agent.jar
ENTRYPOINT ["java","-jar","agent.jar"]
#EXPOSE 8080
CMD /bin/bash
