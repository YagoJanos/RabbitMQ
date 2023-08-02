package com.ilegra.rabbitmqpubsubapi.exception;

public class InvalidInputMessageException extends RuntimeException{

    public InvalidInputMessageException(String message){
        super(message);
    }
}
