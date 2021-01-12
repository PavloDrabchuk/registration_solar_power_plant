package com.example.demo.service;

import com.example.demo.dao.ConfirmationCodeRepository;
import com.example.demo.model.ConfirmationCode;
import com.example.demo.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.UUID;

@Service
public class ConfirmationCodeService {
    private final ConfirmationCodeRepository confirmationCodeRepository;
    private final UsersService usersService;

    @Autowired
    public ConfirmationCodeService(ConfirmationCodeRepository confirmationCodeRepository,
                                   UsersService usersService){
        this.confirmationCodeRepository=confirmationCodeRepository;
        this.usersService=usersService;
    }

    public void saveConfirmationCode(ConfirmationCode confirmationCode){
        confirmationCodeRepository.save(confirmationCode);
    }

    public Optional<ConfirmationCode> findConfirmationCodeByConfirmationCode(String confirmationCode){
        return confirmationCodeRepository.findConfirmationCodeByConfirmationCode(confirmationCode);
    }

    public void sendConfirmationCode(User user){
        //відправляємо посилання активації
        UUID uuid = UUID.randomUUID();
        String stringConfirmationCode = uuid.toString();
        System.out.println("confirmationCode: " + stringConfirmationCode);

        ConfirmationCode confirmationCode = new ConfirmationCode(user, stringConfirmationCode);
        saveConfirmationCode(confirmationCode);

        usersService.sendMailWithConfirmationCode(user.getEmail(), confirmationCode.getConfirmationCode());
        System.out.println("----- - - -- - - - - - -- - - - - ");
    }

    public void deactivateConfirmationCodesByUser(User user){
        Iterable<ConfirmationCode> confirmationCodesByUser = confirmationCodeRepository.findAllByUser(user);
        for(ConfirmationCode confirmationCode : confirmationCodesByUser){
            System.out.println("   : "+confirmationCode.getConfirmationCode());
            confirmationCode.setValid(false);
            confirmationCodeRepository.save(confirmationCode);
        }

    }


}
