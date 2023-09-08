# Kafka - Clickhouse - Grafana

This is a demo project of connecting Apache Kafka, Clickhouse and Grafana together. It includes a test `KafkaProducer` called *RequestDataProducer*, which automatically sends data to Kafka, and a demo Grafana dashboard, called *Api Requests Test*, whith some visualization of the data.

## Usage with RequestDataProducer

If you want to use the test producer you have two choices:
  - Building an image using the included `Dockerfile` and run it inside the docker environment.
    ```
    $ mvn clean package
    $ docker build -t request-producer .
    $ docker compose --profile with-producer up -d
    $ .\create_kafka_topic.cmd <your_topic>
    ```
  - Running the `RequestDataProducer` from command line.
    ```
    $ mvn clean package
    $ set PRODUCER_BOOTSTRAP_SERVER=localhost:9094
    $ set PRODUCER_TOPIC=requests
    $ java -jar target\RequestDataProducer.jar
    $ docker compose up -d
    $ .\create_kafka_topic.cmd <your_topic>
    ```

## Usage with your producer

Adjust the Clickhouse table schemas according to your data, launch the containers like this
```
$ docker compose up -d
```
and create a kafka topic.

## Adjusting Clickhouse table schemas

You can set up your schemas and setting the *Kafka Table Engine* in `clickhouse/initdb/init.sql` file.

## Connecting to Kafka

Using your own producer you can connect to kafka through **localhost:9094** from your host machine OR **kafka:9092** from inside the docker network.

## Create kafka topic

Using the **create_kafka_topic.cmd** script:

```
$ .\create_kafka_topic.cmd <topic>
```

## Reaching Grafana

After you started the containers with the **compose.yaml** file, you can reach Grafana through **localhost:3000**
