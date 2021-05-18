package com.example.solar_power_plant.service;

import com.example.solar_power_plant.dao.ConfirmationCodeRepository;
import com.example.solar_power_plant.model.ConfirmationCode;
import com.example.solar_power_plant.model.TypesConfirmationCode;
import com.example.solar_power_plant.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
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

    public void sendConfirmationCode(User user, TypesConfirmationCode typeConfirmationCode){
        //відправляємо посилання активації
        UUID uuid = UUID.randomUUID();
        String stringConfirmationCode = uuid.toString();
        System.out.println("confirmationCode: " + stringConfirmationCode);

        ConfirmationCode confirmationCode = new ConfirmationCode(user, stringConfirmationCode,typeConfirmationCode);
        saveConfirmationCode(confirmationCode);

        usersService.sendMailWithConfirmationCode(user.getEmail(), confirmationCode.getConfirmationCode(), typeConfirmationCode);
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

    @Async
    public void deactivateOverdueCodes(){

        List<ConfirmationCode> confirmationCodeList=confirmationCodeRepository.findByDateTimeOfCreationBefore(LocalDateTime.now().plusHours(1));
        System.out.println("local date time + 1 hour: "+LocalDateTime.now().plusMinutes(20));
        /*for(int i=0;i<100;i++) {
            System.out.println("deactivate");
        }*/
        for(ConfirmationCode confirmationCode:confirmationCodeList){
            System.out.println("code: "+confirmationCode.getConfirmationCode());
        }
    }


}
