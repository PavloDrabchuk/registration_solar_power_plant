package com.example.demo.service;

import com.example.demo.dao.ConfirmationCodeRepository;
import com.example.demo.model.ConfirmationCode;
import com.example.demo.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class ConfirmationCodeService {
    private final ConfirmationCodeRepository confirmationCodeRepository;

    @Autowired
    public ConfirmationCodeService(ConfirmationCodeRepository confirmationCodeRepository){
        this.confirmationCodeRepository=confirmationCodeRepository;
    }

    public void saveConfirmationCode(ConfirmationCode confirmationCode){
        confirmationCodeRepository.save(confirmationCode);
    }

    public Optional<ConfirmationCode> findConfirmationCodeByConfirmationCode(String confirmationCode){
        return confirmationCodeRepository.findConfirmationCodeByConfirmationCode(confirmationCode);
    }


}