package com.example.solar_power_plant.service;

import com.example.solar_power_plant.dao.ConfirmationCodeRepository;
import com.example.solar_power_plant.model.ConfirmationCode;
import com.example.solar_power_plant.enums.TypesConfirmationCode;
import com.example.solar_power_plant.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.PropertySource;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@PropertySource("classpath:project.properties")
public class ConfirmationCodeService {

    //private final int CODE_DEACTIVATION_CODE = 5 * 1000; // 5 секунд
    //private final int CODE_DEACTIVATION_CODE = 1000 * 60 * 30; // 30 хвилин

    private final ConfirmationCodeRepository confirmationCodeRepository;
    private final UsersService usersService;

    @Autowired
    public ConfirmationCodeService(ConfirmationCodeRepository confirmationCodeRepository,
                                   UsersService usersService) {
        this.confirmationCodeRepository = confirmationCodeRepository;
        this.usersService = usersService;
    }

    public void saveConfirmationCode(ConfirmationCode confirmationCode) {
        confirmationCodeRepository.save(confirmationCode);
    }

    public Optional<ConfirmationCode> findConfirmationCodeByConfirmationCode(String confirmationCode) {
        return confirmationCodeRepository.findConfirmationCodeByConfirmationCode(confirmationCode);
    }

    public void sendConfirmationCode(User user, TypesConfirmationCode typeConfirmationCode) {
        //відправляємо посилання активації
        UUID uuid = UUID.randomUUID();
        String stringConfirmationCode = uuid.toString();
        System.out.println("confirmationCode: " + stringConfirmationCode);

        ConfirmationCode confirmationCode = new ConfirmationCode(user, stringConfirmationCode, typeConfirmationCode);
        saveConfirmationCode(confirmationCode);

        usersService.sendMailWithConfirmationCode(user.getEmail(), confirmationCode.getConfirmationCode(), typeConfirmationCode);
        System.out.println("----- - - -- - - - - - -- - - - - ");
    }

    public void deactivateConfirmationCodesByUser(User user) {
        Iterable<ConfirmationCode> confirmationCodesByUser = confirmationCodeRepository.findAllByUser(user);

        for (ConfirmationCode confirmationCode : confirmationCodesByUser) {
            System.out.println("   : " + confirmationCode.getConfirmationCode());
            confirmationCode.setValid(false);
            confirmationCodeRepository.save(confirmationCode);
        }
    }

    @Async
    @Scheduled(fixedRateString = "${CODE_DEACTIVATION_CODE}" /*1000*60*1000*/ /*5 * 1000*/ /*2 * 60 * 1000*/)
    public void deactivateOverdueCodes() {

        List<ConfirmationCode> confirmationCodeList = confirmationCodeRepository.findAllByDateTimeOfCreationBeforeAndValidIs(LocalDateTime.now().minusHours(1), true);
        System.out.println("local date time - 1 hour: " + LocalDateTime.now().minusHours(1));

        /*for(int i=0;i<10;i++) {
            System.out.println("deactivate");
        }*/

        for (ConfirmationCode confirmationCode : confirmationCodeList) {
            System.out.println("code: " + confirmationCode.getConfirmationCode());
            confirmationCode.setValid(false);
            confirmationCodeRepository.save(confirmationCode);
        }
        //confirmationCodeRepository.deactivateConfirmationCodes(false,LocalDateTime.now().minusHours(1));
        //confirmationCodeRepository.deactivateConfirmationCodes(false,LocalDateTime.now());
    }


}
