package com.mq.demo;

import com.ibm.mq.jms.MQConnectionFactory;
import com.ibm.mq.jms.MQQueueConnectionFactory;
import com.ibm.msg.client.wmq.WMQConstants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.DependsOn;
import org.springframework.context.annotation.Primary;
import org.springframework.jms.JmsException;
import org.springframework.jms.annotation.EnableJms;
import org.springframework.jms.connection.UserCredentialsConnectionFactoryAdapter;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.jms.listener.DefaultMessageListenerContainer;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.jms.JMSException;

@SpringBootApplication
@RestController
@EnableJms
public class MqspringApplication {

    private static final String queueName = "DEV.QUEUE.1";

    @Value("${ibm.mq.queueManager}")
    private String qm;

    @Value("${ibm.mq.ip}")
    private String ip;

    @Value("${ibm.mq.port}")
    private int port;

    @Value("${ibm.mq.channel}")
    private String channel;

    @Value("${ibm.mq.user}")
    private String user;

    @Value("${ibm.mq.password}")
    private String password;


    @Autowired
     private JmsTemplate jmsTemplate;

     public static void main(String[] args) {
         SpringApplication.run(MqspringApplication.class, args);
     }

    @Bean
    public MQConnectionFactory mqConnectionFactory(){
        MQConnectionFactory connectionFactory = new MQConnectionFactory();
        try {

            connectionFactory.setHostName(ip); //mq host name
            connectionFactory.setPort(port); // mq port
            connectionFactory.setQueueManager(qm); //mq queue manager
            connectionFactory.setChannel(channel); //mq channel name
            connectionFactory.setTransportType(1);
            //connectionFactory.setSSLCipherSuite(); //tls cipher suite name

        } catch (JMSException e) {
            e.printStackTrace();
        }
        return connectionFactory;
    }

    @Primary
    @Bean
    @DependsOn(value = { "mqConnectionFactory" })
    UserCredentialsConnectionFactoryAdapter userCredentialsConnectionFactoryAdapter(
            MQConnectionFactory mqQueueConnectionFactory) {
        UserCredentialsConnectionFactoryAdapter userCredentialsConnectionFactoryAdapter = new UserCredentialsConnectionFactoryAdapter();
        userCredentialsConnectionFactoryAdapter.setUsername(user);
        userCredentialsConnectionFactoryAdapter.setPassword(password);
        userCredentialsConnectionFactoryAdapter.setTargetConnectionFactory(mqConnectionFactory());
        return userCredentialsConnectionFactoryAdapter;
    }


    @Bean
    public DefaultMessageListenerContainer myMessageEventContainer() {
        DefaultMessageListenerContainer container = new DefaultMessageListenerContainer();
        container.setAutoStartup(true);
        container.setConnectionFactory(userCredentialsConnectionFactoryAdapter(mqConnectionFactory()));
        container.setDestinationName(queueName);
        container.setMessageListener(new MyEventListener());
        return container;
    }


    @GetMapping("send")
    String send(){
        try{
            jmsTemplate.convertAndSend(queueName, "Hello World!");
            return "OK";
        }catch(JmsException ex){
            ex.printStackTrace();
            return "FAIL";
        }
    }

    @GetMapping("recv")
    String recv(){
        try{
            return jmsTemplate.receiveAndConvert(queueName ).toString();
        }catch(JmsException ex){
            ex.printStackTrace();
            return "FAIL";
        }
    }

}