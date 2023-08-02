package com.ilegra.rabbitmqpubsubapi.controller;

import com.ilegra.rabbitmqpubsubapi.exception.InvalidInputMessageException;
import com.ilegra.rabbitmqpubsubapi.rabbitmq.producer.MessageProducer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/")
public class MainController {

    @Autowired
    private MessageProducer messageProducer;

    @Value("${rabbitmq.routing-key}")
    private String routingKey;
    @Value("${rabbitmq.exchange.name}")
    private String exchangeName;

    @GetMapping("/{message}")
    public String sendMessage(@PathVariable("message") String message) {
        if (message == null || message.isEmpty()) {
            throw new InvalidInputMessageException("The message can not be null or blank");
        }
        messageProducer.sendMessage(message, routingKey, exchangeName);
        return "Message sent: " + message;
    }
}
