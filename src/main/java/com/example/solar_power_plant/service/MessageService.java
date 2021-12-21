package com.example.solar_power_plant.service;

import com.example.solar_power_plant.repository.MessageRepository;
import com.example.solar_power_plant.model.Message;
import com.example.solar_power_plant.enums.MessageType;
import com.example.solar_power_plant.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
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

    public void save(Message message) {
        messageRepository.save(message);
    }

    public List<Message> getAllMessages() {
        return (List<Message>) messageRepository.findAll();
    }

    public Optional<Message> getMessageById(UUID id) {
        return messageRepository.findById(id);
    }

    public List<Message> getAllMessageByRecipient(User recipient) {
        return messageRepository.findAllByRecipientOrderByDateTimeDesc(recipient);
    }

    public List<Message> getAllMessageBySender(User sender) {
        return messageRepository.findAllBySenderOrderByDateTimeDesc(sender);
    }

    public List<Message> getAllMessageByMessageType(MessageType messageType) {
        return messageRepository.findAllByMessageTypeOrderByDateTimeDesc(messageType);
    }

    public List<Message> getMessagesByRecipientForPage(Long id, int offset, int limit) {
        return messageRepository.getListMessagesByRecipientForPage(id, offset, limit);
    }

    public List<Message> getMessagesBySenderForPage(Long id, int offset, int limit) {
        return messageRepository.getListMessagesBySenderForPage(id, offset, limit);
    }

    public void deleteMessage(Message message) {
        messageRepository.delete(message);
    }

    public long getCountUnreadMessagesByUser(User user) {
        return messageRepository.countByIsReadAndRecipient(false, user);
    }

    public Message prepareMessage(String title, String text, Optional<User> sender, User recipient, String type) {
        Message message = new Message();

        message.setTitle(title);
        message.setText(text);

        sender.ifPresent(message::setSender);
        message.setRecipient(recipient);
        message.setRead(false);

        message.setMessageType(MessageType.valueOf(type));
        message.setDateTime(LocalDateTime.now());

        return message;
    }
}
