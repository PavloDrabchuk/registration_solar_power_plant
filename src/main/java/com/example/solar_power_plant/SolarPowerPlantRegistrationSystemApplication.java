package com.example.solar_power_plant;

import com.example.solar_power_plant.dao.UsersRepository;
import com.example.solar_power_plant.service.ConfirmationCodeService;
import com.example.solar_power_plant.service.DynamicDataService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableAsync
@EnableScheduling
public class SolarPowerPlantRegistrationSystemApplication implements CommandLineRunner {
    @Autowired
    private   UsersRepository usersRepository;

    private final DynamicDataService dynamicDataService;

private final ConfirmationCodeService confirmationCodeService;

    public SolarPowerPlantRegistrationSystemApplication(ConfirmationCodeService confirmationCodeService, DynamicDataService dynamicDataService) {
        this.confirmationCodeService = confirmationCodeService;
        this.dynamicDataService=dynamicDataService;
    }


    public static void main(String[] args) {
        SpringApplication.run(SolarPowerPlantRegistrationSystemApplication.class, args);
    }


    @Override
    public void run(String... args) throws Exception {
        //dynamicDataService.saveDynamicData();

/*usersRepository.save(new User("username","name","surname","$10$NS9mwzj5sm9Vx5le/zoUeOBKjmsnPwyvme9c.mdyrpZOHQMSGlmcm",
        UserRole.USER,false,false));*/
        // confirmationCodeService.deactivateOverdueCodes();

      //  https://www.mapquestapi.com/geocoding/v1/address?key=g1CgD1eTytaXG7ubOigQK4bB9QyVSr92&inFormat=kvp&outFormat=json&location=Denver%2C+CO&thumbMaps=false
    }
}