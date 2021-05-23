package com.example.solar_power_plant.controllers;

import com.example.solar_power_plant.model.Message;
import com.example.solar_power_plant.service.MessageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

import java.util.List;
import java.util.Optional;

@Controller
public class MessageController {

    private final MessageService messageService;

    @Autowired
    public MessageController(MessageService messageService) {
        this.messageService = messageService;
    }

    @GetMapping(path = "/messages")
    public List<Message> getAllMessage(){
        return messageService.getAllMessages();
    }
    @GetMapping(path = "/messages/{id}")
    public Optional<Message>

    @PostMapping(path = "")
}
