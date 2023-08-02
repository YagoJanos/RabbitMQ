package com.ilegra.rabbitmqpubsubcli.exception;

public class RabbitMQSetupFailedException extends RuntimeException{
    public RabbitMQSetupFailedException(String message, Throwable e){
        super(message, e);
    }
}
