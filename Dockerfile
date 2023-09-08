FROM ubuntu:focal
RUN apt-get update
RUN apt-get -y install openjdk-17-jre
WORKDIR /opt/metric-producer
COPY ./target/RequestDataProducer.jar .
CMD ["java", "-jar", "RequestDataProducer.jar"]