package com.example.solar_power_plant.dao;

import com.example.solar_power_plant.model.Message;
import com.example.solar_power_plant.model.MessageType;
import com.example.solar_power_plant.model.User;
import org.springframework.data.repository.CrudRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface MessageRepository extends CrudRepository<Message,Long> {

    Optional<Message> findById(UUID id);

    List<Message> findAllByRecipient(User recipient);

    List<Message> findAllBySender(User sender);

    List<Message> findAllByMessageType(MessageType messageType);

    List<Message> findAllByRecipientAndMessageType(User user,MessageType messageType);
}
