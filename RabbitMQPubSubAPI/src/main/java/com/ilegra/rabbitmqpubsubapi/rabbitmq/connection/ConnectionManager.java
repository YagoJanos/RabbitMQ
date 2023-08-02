package com.ilegra.rabbitmqpubsubapi.rabbitmq.connection;

import com.ilegra.rabbitmqpubsubapi.exception.ConnectionFailedException;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import jakarta.annotation.PreDestroy;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

import static java.lang.Thread.sleep;

@Service
public class ConnectionManager {

    private final ConnectionFactory connectionFactory;
    private static volatile Connection connection;

    public ConnectionManager(@Value("${rabbitmq.auth.host}") String rabbitHost,
                             @Value("${rabbitmq.auth.user}") String rabbitUser,
                             @Value("${rabbitmq.auth.password}") String rabbitPassword) {

        connectionFactory = new ConnectionFactory();

        connectionFactory.setHost(rabbitHost);
        connectionFactory.setUsername(rabbitUser);
        connectionFactory.setPassword(rabbitPassword);
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
                            connection = connectionFactory.newConnection("PublisherApplication");
                            connected = true;

                        } catch (IOException | TimeoutException e) {

                            retryCount++;
                            System.err.println("Failed to connect, retrying... Attempt: " + retryCount);
                            try {
                                sleep(5000);
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

    @PreDestroy
    public void cleanUp() throws IOException {
        if (connection != null && !connection.isOpen()) {
            try {connection.close();
            } catch (IOException e){
                System.err.println("Connection has not been closed properly");
            }
        }
    }

}
