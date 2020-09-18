package com.mq.demo;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.TextMessage;

public class MyEventListener implements MessageListener {

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

      }
    } catch (Exception e) {
      e.printStackTrace();
    }
  }
}