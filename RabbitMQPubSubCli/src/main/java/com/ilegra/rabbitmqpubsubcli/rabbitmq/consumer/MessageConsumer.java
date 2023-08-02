package com.ilegra.rabbitmqpubsubcli.rabbitmq.consumer;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.DeliverCallback;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Random;
import java.util.concurrent.TimeoutException;

public class MessageConsumer implements Runnable {

    private final Channel channel;
    private final String queueName;
    private final Random random;

    public MessageConsumer(Channel channel, String queueName) {
        this.channel = channel;
        this.queueName = queueName;
        this.random = new Random();
    }

    @Override
    public void run() {

        DeliverCallback deliverCallback = (consumerTag, delivery) -> {

            String message = new String(delivery.getBody(), StandardCharsets.UTF_8);

            try {
                if (random.nextDouble() < 0.3) {
                    throw new RuntimeException("Simulated processing failure");
                }
                System.out.println("Consumer " + Thread.currentThread().getName() + " Received '" + message + "'");
                channel.basicAck(delivery.getEnvelope().getDeliveryTag(), false);
            } catch (Exception e) {
                System.err.println("Failed to process the message: " + message);
                channel.basicNack(delivery.getEnvelope().getDeliveryTag(), false, false);
            }

        };

        try {
            channel.basicConsume(queueName, false, deliverCallback, consumerTag -> {});
        } catch (IOException e) {
            System.err.println("Failed to consume messages: " + e.getMessage());
            e.printStackTrace();
        }

    }

    public void closeChannel() {
        if (channel != null && channel.isOpen()) {
            try {
                channel.close();
                System.out.println("Channel has been closed");
            } catch (IOException | TimeoutException e) {
                System.err.println("Failed to close the channel: " + e.getMessage());
                e.printStackTrace();
            }
        }
    }
}
