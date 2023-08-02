package com.ilegra.rabbitmqpubsubcli.rabbitmq.configuration;

import com.ilegra.rabbitmqpubsubcli.exception.RabbitMQSetupFailedException;
import com.rabbitmq.client.Channel;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class SetupQueuesAndExchanges {

    private Channel channel;
    private final String queueName;
    private final String deadLetterQueueName;
    private final String exchangeName;
    private final String deadLetterExchangeName;


    public SetupQueuesAndExchanges(Channel channel, String queueName, String deadLetterQueueName, String exchangeName, String deadLetterExchangeName){

        this.channel = channel;
        this.queueName = queueName;
        this.deadLetterQueueName = deadLetterQueueName;
        this.exchangeName = exchangeName;
        this.deadLetterExchangeName = deadLetterExchangeName;
    }

    public void createResources() throws RabbitMQSetupFailedException {

        try{
            channel.exchangeDeclare(exchangeName, "direct");
            channel.exchangeDeclare(deadLetterExchangeName, "direct");

            Map<String, Object> args = new HashMap<>();
            args.put("x-dead-letter-exchange", deadLetterExchangeName);
            args.put("x-dead-letter-routing-key", deadLetterQueueName);

            channel.queueDeclare(queueName, false, false, false, args);
            channel.queueDeclare(deadLetterQueueName, false, false, false, null);

            channel.queueBind(queueName, exchangeName, queueName);
            channel.queueBind(deadLetterQueueName, deadLetterExchangeName, deadLetterQueueName);
        } catch (IOException e){
            throw new RabbitMQSetupFailedException("Failed to setup RabbitMQ", e);
        }

    }
}
