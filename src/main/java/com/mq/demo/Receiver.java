package com.mq.demo;

import java.sql.Timestamp;
import java.util.Properties;
import java.util.concurrent.CountDownLatch;

import org.apache.kafka.clients.producer.*;
import org.springframework.stereotype.Component;

@Component
public class Receiver {

    private CountDownLatch latch = new CountDownLatch(1);
    Properties props;

    public Receiver(){
        props = new Properties();
        props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092");
        props.put(ProducerConfig.ACKS_CONFIG, "all");
        props.put(ProducerConfig.RETRIES_CONFIG, 0);
        props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, "org.apache.kafka.common.serialization.StringSerializer");
        props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, "org.apache.kafka.common.serialization.StringSerializer");
    }

    private static class TestCallback implements Callback {

        public void onCompletion(RecordMetadata recordMetadata, Exception e) {

            if (e != null) {
                System.out.println("Error while producing message to topic :" + recordMetadata);
                e.printStackTrace();

            } else {
                String message = String.format("sent message to topic:%s partition:%s  offset:%s",
                        recordMetadata.topic(), recordMetadata.partition(), recordMetadata.offset());
                System.out.println(message);

            }

        }

    }

    public void receiveMessage(String message) {
        System.out.println("Received <" + message + "> @" + new Timestamp(System.currentTimeMillis()));
        latch.countDown();

        KafkaProducer<String, String> producer = new KafkaProducer<>(props);
        TestCallback callback = new TestCallback();

        ProducerRecord<String, String> data = new ProducerRecord<String, String>
                ("rabbitmqkafka", "rabbitmqkafka", message );

        producer.send(data, callback);

    }

    public CountDownLatch getLatch() {
        return latch;
    }

}