package com.dsi.demoMsgQueue.config;

import com.sun.messaging.ConnectionConfiguration;
import com.sun.messaging.ConnectionFactory;
import jakarta.jms.JMSException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jms.annotation.EnableJms;

@Configuration
@EnableJms
public class JmsConfig {

    @Value("${openmq.broker-host}")
    private String brokerHost;

    @Value("${openmq.broker-port}")
    private int brokerPort;

    @Bean
    public ConnectionFactory connectionFactory() throws JMSException {
        ConnectionFactory connectionFactory = new ConnectionFactory();
        connectionFactory.setProperty(ConnectionConfiguration.imqAddressList, brokerHost + ":" + brokerPort);
        return connectionFactory;
    }
}