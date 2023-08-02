package com.ilegra.rabbitmqpubsubapi.rabbitmq.producer;

import com.ilegra.rabbitmqpubsubapi.exception.MessageNotSendException;
import com.ilegra.rabbitmqpubsubapi.exception.ChannelNotEstablishedException;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.ConfirmCallback;
import jakarta.annotation.PreDestroy;

import java.io.IOException;
import java.util.NoSuchElementException;
import java.util.concurrent.TimeoutException;

public class MessageProducer {
    private final Channel channel;

    public MessageProducer(Channel channel) throws ChannelNotEstablishedException {
        this.channel = channel;

        ConfirmCallback cleanCallback = (sequenceNumber, multiple) -> {
            System.out.println("Message confirmed: " + sequenceNumber);
        };

        ConfirmCallback nackCallBack = (sequenceNumber, multiple) -> {
            System.out.println("Message not confirmed: " + sequenceNumber);
        };

        try {
            channel.confirmSelect();
        } catch (IOException e) {
            System.err.println("Failed to enable publisher confirms: " + e.getMessage());
            e.printStackTrace();
        }
        
        channel.addConfirmListener(cleanCallback, nackCallBack);
    }

    public void sendMessage(String message, String routingKey, String exchangeName) throws MessageNotSendException {
        try {
            channel.basicPublish(exchangeName, routingKey, null, message.getBytes());
        } catch (IOException | NoSuchElementException e){
            throw new MessageNotSendException("Failed to send the message due to an internal error", e);
        }
    }

    @PreDestroy
    public void cleanUp() throws IOException, TimeoutException {
        if (channel != null && !channel.isOpen()) {
            channel.close();
        }
    }
}
