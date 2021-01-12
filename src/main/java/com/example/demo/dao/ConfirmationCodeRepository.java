package com.example.demo.dao;

import com.example.demo.model.ConfirmationCode;
import com.example.demo.model.User;
import org.springframework.data.repository.CrudRepository;

import java.util.Optional;

public interface ConfirmationCodeRepository extends CrudRepository<ConfirmationCode,Long> {
Optional<ConfirmationCode> findConfirmationCodeByConfirmationCode(String confirmationCode);

    @Override
    Iterable<ConfirmationCode> findAll();

    Iterable<ConfirmationCode> findAllByUser(User user);
}
