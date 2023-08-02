package com.ilegra.rabbitmqpubsubapi.exception;

public class ChannelNotEstablishedException extends RuntimeException {
    public ChannelNotEstablishedException(String message, Throwable cause){
        super(message, cause);
    }
}
