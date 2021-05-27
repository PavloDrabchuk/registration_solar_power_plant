package com.example.solar_power_plant.service;

import com.example.solar_power_plant.dao.MessageRepository;
import com.example.solar_power_plant.dao.UsersRepository;
import com.example.solar_power_plant.model.Message;
import com.example.solar_power_plant.model.MessageType;
import com.example.solar_power_plant.model.SolarPowerPlant;
import com.example.solar_power_plant.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

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

    public void save(Message message){
        messageRepository.save(message);
    }

    public List<Message> getAllMessages() {
        return (List<Message>) messageRepository.findAll();
    }

    public Optional<Message> getMessageById(UUID id){
        return messageRepository.findById(id);
    }

    public List<Message> getAllMessageByRecipient(User recipient){
        return messageRepository.findAllByRecipient(recipient);
    }

    public List<Message> getAllMessageBySender(User sender){
        return messageRepository.findAllBySender(sender);
    }

    public List<Message> getAllMessageByMessageType(MessageType messageType){
        return messageRepository.findAllByMessageType(messageType);
    }

    public List<Message> getMessagesByRecipientForPage(Long id, int offset, int limit) {
        return messageRepository.getListMessagesByRecipientForPage(id, offset, limit);
    }

    public List<Message> getMessagesBySenderForPage(Long id, int offset, int limit) {
        return messageRepository.getListMessagesBySenderForPage(id, offset, limit);
    }

    /**
     *
     * @param user
     * @param limit
     * @param type // 1 - recipient, 2 - sender
     * @return
     */
    public List<String> getNumPagesList(User user, double limit, int type) {
        //double limitTracksId = 2;

        //List<String> listTrackId = tracksRepository.getListTrackId();
        //List<String> listTrackId = tracksRepository.getListTrackIdForPage((Integer.parseInt(page) - 1) * (int) limitTracksId, (int) limitTracksId);
        List<String> pageNumList = new ArrayList<>();

        List<Message> messages;
        messages = (type==1)
                ? getAllMessageByRecipient(user)
                : getAllMessageBySender(user);


        for (int i = 1; i <= ((int) Math.ceil(messages.size() / limit)); i++) {
            pageNumList.add(Integer.toString(i));
        }
        return pageNumList;
    }

    /*public List<Message> getAllMessageByUserAndMessageType(User user, MessageType messageType){
        return  messageRepository.findAllByUserAndMessageType(user,messageType);
    }*/

    /*public List<SolarPowerPlant> getMessagesForPage(Long id, int offset, int limit) {
        return solarPowerPlantRepository.getListSolarPowerPlantForPage(id, offset, limit);
    }*/
}