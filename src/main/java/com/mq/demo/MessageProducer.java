package com.mq.demo;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Component;
import org.springframework.util.concurrent.ListenableFuture;
import org.springframework.util.concurrent.ListenableFutureCallback;

@Component
public class MessageProducer {

    private static final Logger log
            = LoggerFactory.getLogger(MessageProducer.class);

    @Autowired
    private KafkaTemplate<String, String> kafkaTemplate;

    @Value(value = "${kafka.topic.name}")
    private String topicName;

//    @Value(value = "${partitioned.topic.name}")
//    private String partitionedTopicName;
//
//    @Value(value = "${filtered.topic.name}")
//    private String filteredTopicName;
//
//    @Value(value = "${greeting.topic.name}")
//    private String greetingTopicName;

    public void sendMessage(String message) {


//        throw(new RuntimeException("for test"));

      log.info("kafkaTemplate = {}", kafkaTemplate);
      ListenableFuture<SendResult<String, String>> future = kafkaTemplate.send(topicName, message);

      future.addCallback(new ListenableFutureCallback<SendResult<String, String>>() {

        @Override
        public void onSuccess(SendResult<String, String> result) {
          System.out.println("Sent message=[" + message + "] with offset=[" + result.getRecordMetadata()
                  .offset() + "]");
        }

        @Override
        public void onFailure(Throwable ex) {
          System.out.println("Unable to send message=[" + message + "] due to : " + ex.getMessage());
        }
      });

    }

//    public void sendMessageToPartition(String message, int partition) {
//      kafkaTemplate.send(partitionedTopicName, partition, null, message);
//    }
//
//    public void sendMessageToFiltered(String message) {
//      kafkaTemplate.send(filteredTopicName, message);
//    }

}