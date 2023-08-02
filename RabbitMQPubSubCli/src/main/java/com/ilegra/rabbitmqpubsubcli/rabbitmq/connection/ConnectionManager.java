package com.ilegra.rabbitmqpubsubcli.rabbitmq.connection;

import com.ilegra.rabbitmqpubsubcli.exception.ConnectionFailedException;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

public class ConnectionManager {

    private final ConnectionFactory connectionFactory;
    private static volatile Connection connection;

    public ConnectionManager() {
        connectionFactory = new ConnectionFactory();

        connectionFactory.setHost(System.getenv("RABBITMQ_HOST"));
        connectionFactory.setUsername(System.getenv("RABBITMQ_USER"));
        connectionFactory.setPassword(System.getenv("RABBITMQ_PASSWORD"));
        connectionFactory.setAutomaticRecoveryEnabled(true);
    }

    public Connection getConnection() throws ConnectionFailedException {
        if (connection == null || !connection.isOpen()) {
            synchronized (ConnectionManager.class) {
                if (connection == null || !connection.isOpen()) {

                    int maxRetries = 5;
                    int retryCount = 0;
                    boolean connected = false;

                    while (!connected && retryCount < maxRetries) {
                        try {
                            connection = connectionFactory.newConnection("ConsumerApplication");
                            connected = true;

                        } catch (IOException | TimeoutException e) {

                            retryCount++;
                            System.err.println("Failed to connect, retrying... Attempt: " + retryCount);
                            try {
                                Thread.sleep(5000);
                            } catch (InterruptedException interruptedException) {
                                Thread.currentThread().interrupt();
                            }
                        }
                    }

                    if (!connected) {
                        throw new ConnectionFailedException("Failed to establish a connection after " + maxRetries + " attempts");
                    }
                }
            }
        }
        return connection;
    }

}
