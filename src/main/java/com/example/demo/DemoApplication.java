package com.example.demo;

import com.example.demo.dao.ConfirmationCodeRepository;
import com.example.demo.dao.UsersRepository;
import com.example.demo.model.DynamicData;
import com.example.demo.service.ConfirmationCodeService;
import com.example.demo.service.DynamicDataService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;

@SpringBootApplication
@EnableAsync
public class DemoApplication implements CommandLineRunner {
    @Autowired
    private   UsersRepository usersRepository;

    private final DynamicDataService dynamicDataService;

private final ConfirmationCodeService confirmationCodeService;

    public DemoApplication(ConfirmationCodeService confirmationCodeService, DynamicDataService dynamicDataService) {
        this.confirmationCodeService = confirmationCodeService;
        this.dynamicDataService=dynamicDataService;
    }


    public static void main(String[] args) {
        SpringApplication.run(DemoApplication.class, args);
    }


    @Override
    public void run(String... args) throws Exception {
        dynamicDataService.saveDynamicData(new DynamicData());
/*usersRepository.save(new User("username","name","surname","$10$NS9mwzj5sm9Vx5le/zoUeOBKjmsnPwyvme9c.mdyrpZOHQMSGlmcm",
        UserRole.USER,false,false));*/
        // confirmationCodeService.deactivateOverdueCodes();

      //  https://www.mapquestapi.com/geocoding/v1/address?key=g1CgD1eTytaXG7ubOigQK4bB9QyVSr92&inFormat=kvp&outFormat=json&location=Denver%2C+CO&thumbMaps=false
    }
}