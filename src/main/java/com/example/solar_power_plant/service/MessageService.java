package com.example.solar_power_plant.service;

import com.example.solar_power_plant.dao.MessageRepository;
import com.example.solar_power_plant.dao.UsersRepository;
import com.example.solar_power_plant.model.Message;
import com.example.solar_power_plant.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class MessageService {

    private final MessageRepository messageRepository;

    @Autowired
    public MessageService(MessageRepository messageRepository) {
        this.messageRepository = messageRepository;
    }

    public void save(Message message){
        messageRepository.save(message);
    }

    public List<Message> getAllMessages() {
        return (List<Message>) messageRepository.findAll();
    }

    public Optional<Message> getMessageById(UUID id){
        return messageRepository.findById(id);

    }
}
