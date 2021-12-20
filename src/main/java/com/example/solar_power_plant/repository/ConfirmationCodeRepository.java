package com.example.solar_power_plant.repository;

import com.example.solar_power_plant.model.ConfirmationCode;
import com.example.solar_power_plant.model.User;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface ConfirmationCodeRepository extends CrudRepository<ConfirmationCode, Long> {
    Optional<ConfirmationCode> findConfirmationCodeByConfirmationCode(String confirmationCode);

    @Override
    Iterable<ConfirmationCode> findAll();

    Iterable<ConfirmationCode> findAllByUser(User user);

    List<ConfirmationCode> findAllByDateTimeOfCreationBeforeAndValidIs(LocalDateTime dateTime, Boolean valid);

    // TODO: 20.12.2021 Use this function.
    @Query(value = "update ConfirmationCode c set c.valid = :valid WHERE c.dateTimeOfCreation < :time")
    void deactivateConfirmationCodes(Boolean valid, LocalDateTime time);

}
