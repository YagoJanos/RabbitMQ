package com.ilegra.rabbitmqpubsubapi.exception;

public class ConnectionFailedException extends RuntimeException{
    public ConnectionFailedException(String message){
        super(message);
    }
}
