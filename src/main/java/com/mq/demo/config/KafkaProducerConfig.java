package com.mq.demo.config;

import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;

import java.util.HashMap;
import java.util.Map;

@Configuration
public class KafkaProducerConfig {

    @Value(value = "${kafka.bootstrap.address}")
    private String bootstrapAddress;
 
    @Bean
    public ProducerFactory<String, String> producerFactory() {
        Map<String, Object> configProps = new HashMap<>();
//        configProps.put(
//          ProducerConfig.BOOTSTRAP_SERVERS_CONFIG,
//          bootstrapAddress);
//        configProps.put(
//          ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG,
//          StringSerializer.class);
//        configProps.put(
//          ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG,
//          StringSerializer.class);
        //
        configProps.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapAddress);
        configProps.put(ProducerConfig.ACKS_CONFIG, "all");
        configProps.put(ProducerConfig.RETRIES_CONFIG, 0);
        configProps.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, "org.apache.kafka.common.serialization.StringSerializer");
        configProps.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, "org.apache.kafka.common.serialization.StringSerializer");
        configProps.put(ProducerConfig.CLIENT_ID_CONFIG, "MqAdaptor");
        //Only one in-flight messages per Kafka broker connection
        // - max.in.flight.requests.per.connection (default 5)
        configProps.put(ProducerConfig.MAX_IN_FLIGHT_REQUESTS_PER_CONNECTION, 1);
        //Set the number of retries - retries
        configProps.put(ProducerConfig.RETRIES_CONFIG, 3);
        //Request timeout - request.timeout.ms
        configProps.put(ProducerConfig.REQUEST_TIMEOUT_MS_CONFIG, 15_000);
        //Only retry after one second.
        configProps.put(ProducerConfig.RETRY_BACKOFF_MS_CONFIG, 1_000);

        return new DefaultKafkaProducerFactory<>(configProps);
    }
 
    @Bean
    public KafkaTemplate<String, String> kafkaTemplate() {

        return new KafkaTemplate<>(producerFactory());
    }
}