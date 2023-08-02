package com.ilegra.rabbitmqpubsubcli.exception;

public class ConnectionFailedException extends RuntimeException {
    public ConnectionFailedException(String message){
        super(message);
    }
}
