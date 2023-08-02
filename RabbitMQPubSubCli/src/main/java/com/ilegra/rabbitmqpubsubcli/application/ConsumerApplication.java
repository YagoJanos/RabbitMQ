package com.ilegra.rabbitmqpubsubcli.application;

import com.ilegra.rabbitmqpubsubcli.exception.ChannelNotEstablishedException;
import com.ilegra.rabbitmqpubsubcli.rabbitmq.configuration.SetupQueuesAndExchanges;
import com.ilegra.rabbitmqpubsubcli.rabbitmq.connection.ConnectionManager;
import com.ilegra.rabbitmqpubsubcli.rabbitmq.consumer.MessageConsumer;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

public class ConsumerApplication {

    private static final String queueName = "main.queue";
    private static final String deadLetterQueueName = "deadletter.queue";
    private static final String exchangeName = "main.exchange";
    private static final String deadLetterExchangeName = "deadletter.exchange";
    private static final Integer numConsumers = 5;


    private static Connection sharedConnection;

    private static ExecutorService executorService;
    
    private static List<MessageConsumer> messageConsumers;


    
    public static void main(String[] args) {

        sharedConnection = new ConnectionManager().getConnection();

        Channel channel = null;

        try {
            channel = sharedConnection.createChannel();
        } catch (IOException e) {
            throw new ChannelNotEstablishedException("RabbitMQ setup channel could not be established, closing application", e);
        }

        SetupQueuesAndExchanges setupQueuesAndExchanges = new SetupQueuesAndExchanges(channel, queueName, deadLetterQueueName, exchangeName, deadLetterExchangeName);

        setupQueuesAndExchanges.createResources();

        try {
            channel.close();
        } catch (IOException | TimeoutException e) {
            System.err.println("Failed to close the setup channel: " + e.getMessage());
            e.printStackTrace();
        }

        executorService = Executors.newFixedThreadPool(numConsumers);

        createAndSubmitConsumers();

        addShutdownHook();
    }



    private static void createAndSubmitConsumers() {

        messageConsumers = new ArrayList<>();

        for (int i = 0; i < numConsumers; i++) {
            MessageConsumer consumer = null;
            try {
                Channel channel = sharedConnection.createChannel();
                channel.basicQos(1);
                consumer = new MessageConsumer(channel, queueName);
            } catch (IOException e) {
                System.err.println("Failed to create MessageConsumer: " + e.getMessage());
                e.printStackTrace();
                continue;
            }
            executorService.submit(consumer);
            messageConsumers.add(consumer);
        }
    }



    private static void addShutdownHook() {
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            executorService.shutdown();
            try {
                executorService.awaitTermination(5, TimeUnit.SECONDS);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }

            for (MessageConsumer messageConsumer : messageConsumers) {
                messageConsumer.closeChannel();
            }

            if (sharedConnection != null && sharedConnection.isOpen()) {
                try {
                    sharedConnection.close();
                } catch (IOException e) {
                    System.err.println("Failed to close the connection to the RabbitMQ server.");
                }
            }

            System.out.println("ExecutorService has been shut down.");
        }));
    }

}

