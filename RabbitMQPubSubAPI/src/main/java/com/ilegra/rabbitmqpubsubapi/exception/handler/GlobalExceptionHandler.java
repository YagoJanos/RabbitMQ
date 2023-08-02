package com.ilegra.rabbitmqpubsubapi.exception.handler;

import com.ilegra.rabbitmqpubsubapi.exception.ConnectionFailedException;
import com.ilegra.rabbitmqpubsubapi.exception.InvalidInputMessageException;
import com.ilegra.rabbitmqpubsubapi.exception.MessageNotSendException;
import com.ilegra.rabbitmqpubsubapi.exception.ChannelNotEstablishedException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@ControllerAdvice
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {

    @ExceptionHandler(InvalidInputMessageException.class)
    public ResponseEntity<String> handleInvalidInputMessageException(InvalidInputMessageException e) {
        System.err.println("InvalidInputMessageException caught: " + e.getMessage() + ", caused by:\n" + e.getStackTrace());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Provide a suitable request. " + e.getMessage());
    }

    @ExceptionHandler(MessageNotSendException.class)
    public ResponseEntity<String> messageSendException(MessageNotSendException e) {
        System.err.println("MessageSendException caught: "+e.getMessage()+", caused by:\n"+e.getStackTrace());
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Internal server error");
    }

    @ExceptionHandler(ConnectionFailedException.class)
    public ResponseEntity<String> connectionFailedException(MessageNotSendException e) {
        System.err.println("ConnectionFailedException caught: "+e.getMessage()+", caused by:\n"+e.getStackTrace());
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Internal server error");
    }

    @ExceptionHandler(ChannelNotEstablishedException.class)
    public ResponseEntity<String> handleNoChannelException(ChannelNotEstablishedException e) {
        System.err.println("ChannelNotEstablishedException caught: "+e.getMessage()+", caused by:\n"+e.getStackTrace());
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Internal server error");
    }

    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<String> handleRuntimeException(RuntimeException e) {
        System.err.println("RuntimeException caught: "+e.getMessage()+", caused by:\n"+e.getStackTrace());
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Internal server error");
    }
}
