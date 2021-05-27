package com.example.solar_power_plant.dao;

import com.example.solar_power_plant.model.Message;
import com.example.solar_power_plant.model.MessageType;
import com.example.solar_power_plant.model.SolarPowerPlant;
import com.example.solar_power_plant.model.User;
import org.springframework.data.jpa.repository.Query;
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

    @Query(value = "select * from message m where m.recipient_id = ?1 order by m.id limit ?2, ?3 ",
            nativeQuery = true)
    List<Message> getListMessagesByRecipientForPage(Long id, int offset, int row_count);

    @Query(value = "select * from message m where m.sender_id = ?1 order by m.id limit ?2, ?3 ",
            nativeQuery = true)
    List<Message> getListMessagesBySenderForPage(Long id, int offset, int row_count);
}
