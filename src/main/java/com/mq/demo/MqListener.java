package com.mq.demo;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Component;
import org.springframework.util.concurrent.ListenableFuture;
import org.springframework.util.concurrent.ListenableFutureCallback;

import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.TextMessage;

@Component
public class MqListener implements MessageListener {

  private static final Logger log
          = LoggerFactory.getLogger(MqListener.class);

  MessageProducer messageProducer;

  @Autowired
  public MqListener(MessageProducer messageProducer){
    this.messageProducer = messageProducer;
  }

//  @Bean
//  public MessageProducer messageProducer() {
//    return new MessageProducer();
//  }

  @Override
  public void onMessage(Message message) {
    try {
      if (message instanceof TextMessage) {
        TextMessage textMessage = (TextMessage) message;
        String stringMessage = textMessage.getText();
        System.out.println("onMessage receive: " + stringMessage  );

        XmlMapper xmlMapper = new XmlMapper();
        JsonNode node = xmlMapper.readTree(stringMessage.getBytes());
        ObjectMapper jsonMapper = new ObjectMapper();
        String json = jsonMapper.writeValueAsString(node);
        System.out.println("json: " + json);

        //send to kafka
        log.info("messageProducer = {}", messageProducer);
        messageProducer.sendMessage(json);

      }
    } catch (Exception e) {
      e.printStackTrace();
    }
  }
}