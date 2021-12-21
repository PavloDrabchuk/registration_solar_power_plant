package com.example.solar_power_plant;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableAsync
@EnableScheduling
public class SolarPowerPlantRegistrationSystemApplication {

    public static void main(String[] args) {
        SpringApplication.run(SolarPowerPlantRegistrationSystemApplication.class, args);
    }
}