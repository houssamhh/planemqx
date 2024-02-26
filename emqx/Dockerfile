FROM emqx/emqx:5.0.9
USER root
RUN apt-get update
RUN apt-get install -y bash tcpdump iperf net-tools iproute2 iputils-ping
EXPOSE 1883 18083 8880 8083 8084 8780
CMD /bin/bash