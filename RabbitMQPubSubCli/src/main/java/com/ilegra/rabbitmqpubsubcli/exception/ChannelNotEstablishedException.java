package com.ilegra.rabbitmqpubsubcli.exception;

public class ChannelNotEstablishedException extends RuntimeException{
    public ChannelNotEstablishedException(String message, Throwable e){
        super(message, e);
    }
}
