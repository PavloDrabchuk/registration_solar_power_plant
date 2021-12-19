package com.example.solar_power_plant.dao;

import com.example.solar_power_plant.model.ConfirmationCode;
import com.example.solar_power_plant.model.User;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface ConfirmationCodeRepository extends CrudRepository<ConfirmationCode, Long> {
    Optional<ConfirmationCode> findConfirmationCodeByConfirmationCode(String confirmationCode);

    @Override
    Iterable<ConfirmationCode> findAll();

    Iterable<ConfirmationCode> findAllByUser(User user);

    List<ConfirmationCode> findAllByDateTimeOfCreationBeforeAndValidIs(LocalDateTime dateTime,Boolean valid);

    //@Query(value = "update confirmation_code c set c.valid = ?1 WHERE c.date_time_of_creation < ?2",nativeQuery = true)
    @Query(value = "update ConfirmationCode c set c.valid = :valid WHERE c.dateTimeOfCreation < :time")
    void deactivateConfirmationCodes(Boolean valid, LocalDateTime time);

}
