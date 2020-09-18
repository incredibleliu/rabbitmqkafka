package com.mq.demo;

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
        //do something with your message from queue
      }
    } catch (JMSException e) {
      //catch error
    }
  }
}