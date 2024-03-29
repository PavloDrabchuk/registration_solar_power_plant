package com.example.solar_power_plant.repository;

import com.example.solar_power_plant.model.Message;
import com.example.solar_power_plant.enums.MessageType;
import com.example.solar_power_plant.model.User;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface MessageRepository extends CrudRepository<Message, Long> {

    Optional<Message> findById(UUID id);

    List<Message> findAllByRecipientOrderByDateTimeDesc(User recipient);

    List<Message> findAllBySenderOrderByDateTimeDesc(User sender);

    List<Message> findAllByMessageTypeOrderByDateTimeDesc(MessageType messageType);

    List<Message> findAllByRecipientAndMessageType(User user, MessageType messageType);

    @Query(value = "select * from message m where m.recipient_id = ?1 order by  m.date_time desc, m.id limit ?2, ?3 ",
            nativeQuery = true)
    List<Message> getListMessagesByRecipientForPage(Long id, int offset, int row_count);

    @Query(value = "select * from message m where m.sender_id = ?1 order by m.date_time desc, m.id limit ?2, ?3 ",
            nativeQuery = true)
    List<Message> getListMessagesBySenderForPage(Long id, int offset, int row_count);

    long countByIsReadAndRecipient(boolean isRead, User recipient);
}
