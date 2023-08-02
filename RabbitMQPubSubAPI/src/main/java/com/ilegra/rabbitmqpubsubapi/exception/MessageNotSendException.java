package com.ilegra.rabbitmqpubsubapi.exception;

public class MessageNotSendException extends RuntimeException{
    public MessageNotSendException(String message, Throwable cause){
        super(message, cause);
    }
}
