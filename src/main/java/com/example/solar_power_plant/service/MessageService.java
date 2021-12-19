package com.example.solar_power_plant.service;

import com.example.solar_power_plant.dao.MessageRepository;
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

    /**
     * @param user
     * @param limit
     * @param type  // 1 - recipient, 2 - sender
     * @return
     */
    public List<String> getNumPagesList(User user, double limit, int type) {
        //double limitTracksId = 2;

        //List<String> listTrackId = tracksRepository.getListTrackId();
        /*List<String> listTrackId = tracksRepository.getListTrackIdForPage(
                (Integer.parseInt(page) - 1) * (int) limitTracksId, (int) limitTracksId);*/

        List<String> pageNumList = new ArrayList<>();

        List<Message> messages;
        messages = (type == 1)
                ? getAllMessageByRecipient(user)
                : getAllMessageBySender(user);


        for (int i = 1; i <= ((int) Math.ceil(messages.size() / limit)); i++) {
            pageNumList.add(Integer.toString(i));
        }
        return pageNumList;
    }

    public void deleteMessage(Message message) {
        messageRepository.delete(message);
    }

    /*public List<Message> getAllMessageByUserAndMessageType(User user, MessageType messageType){
        return  messageRepository.findAllByUserAndMessageType(user,messageType);
    }*/

    /*public List<SolarPowerPlant> getMessagesForPage(Long id, int offset, int limit) {
        return solarPowerPlantRepository.getListSolarPowerPlantForPage(id, offset, limit);
    }*/

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
