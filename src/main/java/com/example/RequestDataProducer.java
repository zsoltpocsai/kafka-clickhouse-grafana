package com.example;

import java.util.Collections;
import java.util.Properties;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import org.apache.kafka.clients.admin.Admin;
import org.apache.kafka.clients.admin.AdminClientConfig;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;

import com.fasterxml.jackson.databind.ObjectMapper;

public class RequestDataProducer {
    public static void main(String[] args) throws Exception {

        String bootstrapServer = System.getenv("PRODUCER_BOOTSTRAP_SERVER");
        String topic = System.getenv("PRODUCER_TOPIC");

        if (bootstrapServer == null || topic == null) {
            throw new IllegalArgumentException("Missing environment variables");
        }

        // create admin

        Properties adminProps = new Properties();
        adminProps.setProperty(AdminClientConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServer);

        Admin admin = Admin.create(adminProps);
        
        // list topics

        System.out.println("Connecting to " + bootstrapServer + " ...");

        Set<String> topics = Collections.emptySet();
        boolean topicExists = false;

        while (!topicExists) {

            try {
                topics = admin.listTopics().names().get(10, TimeUnit.SECONDS);
            } catch (TimeoutException e) {
                System.out.println("Couldn't connect to server.");
                continue;
            }

            if (topics.isEmpty() || !topics.stream().anyMatch(t -> topic.equals(t))) {
                System.out.println("There is no '" + topic + "' topic available. Will try later.");
                Thread.sleep(5_000);
            } else {
                System.out.println("Topic '" + topic + "' has been found.");
                topicExists = true;
            }

        }
        
        System.out.println();

        admin.close();

        // create producer

        Properties producerProps = new Properties();
        producerProps.setProperty(
            ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServer);
        producerProps.setProperty(
            ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, "org.apache.kafka.common.serialization.StringSerializer");
        producerProps.setProperty(
            ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, "org.apache.kafka.common.serialization.StringSerializer");

        Producer<String, String> producer = new KafkaProducer<>(producerProps);

        Runtime.getRuntime().addShutdownHook(new Thread(() -> producer.close()));

        // start sending messages

        String[] paths = { "/devices", "/device", "/device/status", "/jobs", "/job", "/job/data" };

        ObjectMapper objectMapper = new ObjectMapper();

        while (true) {

            Thread.sleep(randomInt(500, 2000));

            RequestDto dto = new RequestDto();
            dto.accountId = randomInt(100, 110);
            dto.path = paths[randomInt(0, paths.length - 1)];

            ProducerRecord<String, String> record = new ProducerRecord<>(topic, objectMapper.writeValueAsString(dto));

            System.out.print(String.format("Sending message %s ... ", record.value()));
            producer.send(record, (metadata, error) -> {
                if (error == null) {
                    System.out.println("Done.");
                } else {
                    System.out.println(String.format("\nError: %s", error.getMessage()));
                }
            });

        }
    }

    public static int randomInt(int min, int max) {
        return (int) Math.round(((Math.random() * (max - min)) + min));
    }

    public static class RequestDto {
        public int accountId;
        public String path;
    }
}