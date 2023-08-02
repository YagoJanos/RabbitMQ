package com.ilegra.rabbitmqpubsubapi.config;

import com.ilegra.rabbitmqpubsubapi.exception.ChannelNotEstablishedException;
import com.ilegra.rabbitmqpubsubapi.rabbitmq.producer.MessageProducer;
import com.ilegra.rabbitmqpubsubapi.rabbitmq.connection.ConnectionManager;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.IOException;

@Configuration
public class ConfigBean {

    @Autowired
    ConnectionManager connectionManager;

    @Bean
    public MessageProducer messageProducerRabbitMQ(){
        Connection connection = connectionManager.getConnection();
        Channel channel = null;
        try {
            channel = connection.createChannel();
        } catch (IOException e) {
            throw new ChannelNotEstablishedException("Channel could not be established, something went wrong", e);
        }
        
        MessageProducer messageProducer = new MessageProducer(channel);
        return messageProducer;
    }
}
