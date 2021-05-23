package com.example.solar_power_plant.dao;

import com.example.solar_power_plant.model.Message;
import org.springframework.data.repository.CrudRepository;

import java.util.Optional;
import java.util.UUID;

public interface MessageRepository extends CrudRepository<Message,Long> {

    Optional<Message> findById(UUID id);
}
