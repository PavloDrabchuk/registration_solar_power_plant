package com.example.solar_power_plant.service;

import com.example.solar_power_plant.repository.ConfirmationCodeRepository;
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
        //відправлення посилання активації
        UUID uuid = UUID.randomUUID();
        String stringConfirmationCode = uuid.toString();

        ConfirmationCode confirmationCode = new ConfirmationCode(user, stringConfirmationCode, typeConfirmationCode);
        saveConfirmationCode(confirmationCode);

        usersService.sendMailWithConfirmationCode(user.getEmail(), confirmationCode.getConfirmationCode(), typeConfirmationCode);
    }

    public void deactivateConfirmationCodesByUser(User user) {
        Iterable<ConfirmationCode> confirmationCodesByUser = confirmationCodeRepository.findAllByUser(user);

        for (ConfirmationCode confirmationCode : confirmationCodesByUser) {
            confirmationCode.setValid(false);
            confirmationCodeRepository.save(confirmationCode);
        }
    }

    @Async
    @Scheduled(fixedRateString = "${CODE_DEACTIVATION_CODE}" /*1000*60*1000*/ /*5 * 1000*/ /*2 * 60 * 1000*/)
    public void deactivateOverdueCodes() {

        List<ConfirmationCode> confirmationCodeList = confirmationCodeRepository
                .findAllByDateTimeOfCreationBeforeAndValidIs(LocalDateTime.now().minusHours(1), true);

        for (ConfirmationCode confirmationCode : confirmationCodeList) {
            confirmationCode.setValid(false);
            confirmationCodeRepository.save(confirmationCode);
        }
    }


}
