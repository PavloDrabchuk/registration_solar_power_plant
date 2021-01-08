package com.example.demo.dao;

import com.example.demo.model.ConfirmationCode;
import org.springframework.data.repository.CrudRepository;

import java.util.Optional;

public interface ConfirmationCodeRepository extends CrudRepository<ConfirmationCode,Long> {
Optional<ConfirmationCode> findConfirmationCodeByConfirmationCode(String confirmationCode);
}
