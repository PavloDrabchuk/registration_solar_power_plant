package com.example.solar_power_plant.dao;

import com.example.solar_power_plant.model.ConfirmationCode;
import com.example.solar_power_plant.model.User;
import org.springframework.data.repository.CrudRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface ConfirmationCodeRepository extends CrudRepository<ConfirmationCode,Long> {
Optional<ConfirmationCode> findConfirmationCodeByConfirmationCode(String confirmationCode);

    @Override
    Iterable<ConfirmationCode> findAll();

    Iterable<ConfirmationCode> findAllByUser(User user);

    List<ConfirmationCode> findByDateTimeOfCreationBefore(LocalDateTime dateTime);

}
