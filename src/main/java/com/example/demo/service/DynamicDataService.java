package com.example.demo.service;

import com.example.demo.dao.DynamicDataRepository;
import com.example.demo.model.DynamicData;
import com.example.demo.model.SolarPowerPlant;
import com.example.demo.model.Weather;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

@Service
public class DynamicDataService {

    private final DynamicDataRepository dynamicDataRepository;
    private final SolarPowerPlantService solarPowerPlantService;

    public DynamicDataService(DynamicDataRepository dynamicDataRepository,
                              SolarPowerPlantService solarPowerPlantService) {
        this.dynamicDataRepository = dynamicDataRepository;
        this.solarPowerPlantService = solarPowerPlantService;
    }

    private List<DynamicData> generateData() {
        List<DynamicData> generatedData = new ArrayList<>();
        List<SolarPowerPlant> solarPowerPlantList = solarPowerPlantService.getAllSolarPowerPlants();

        double producedPower,
                leftLimit = 1D,
                rightLimit = 100D;

        LocalDateTime localDateTime = LocalDateTime.now();

        for (SolarPowerPlant solarPowerPlant : solarPowerPlantList) {
            // producedPower = leftLimit + new Random().nextDouble() * (rightLimit - leftLimit);
            System.out.println("Month: " + localDateTime.getMonthValue());

            producedPower = generateProducerPower(localDateTime.getMonthValue());

            generatedData.add(new DynamicData(solarPowerPlant, producedPower, localDateTime));
        }

        return generatedData;
    }

    @Async
    @Scheduled(fixedRate = 8 * 1000/*2 * 60 * 1000*/)
    public void saveDynamicData() {
        System.out.println("\n\n save dynamic data: ");
        //dynamicDataRepository.save(dynamicData);
        for (DynamicData dynamicData : generateData()) {
            System.out.println("  id: " + dynamicData.getSolarPowerPlant().getId());
            dynamicDataRepository.save(dynamicData);
        }
        System.out.println("\n\n\n");
    }

    private double generateProducerPower(int month) {
        double producerPower = 340;
        producerPower *= monthlyCoefficient(month) * weatherCoefficient(month);

        /*
        Months.values()[index]
         */


        //ArrayList<Weather> weathers = new ArrayList<Weather>();
        //System.out.println("Arrays: "+Arrays.toString(Weather.values()));
        System.out.println("Produced power: " + producerPower);

        System.out.println("Monthly coefficient: " + monthlyCoefficient(month));

        System.out.println("Weather coefficient: " + weatherCoefficient(month));

        return producerPower;
    }

    private double monthlyCoefficient(int month) {
        return ((Math.sin(0.4 * month - 1.02)) + 1) / 2;
    }

    private double weatherCoefficient(int month) {
        int weatherNumber;
        do {
            weatherNumber = new Random().nextInt(Weather.values().length);
            //System.out.println("weatherNumber: "+weatherNumber+"  name: "+Weather.values()[weatherNumber].name());
        } while (month > 3 && month < 10 && Weather.values()[weatherNumber].name().equals("Snow"));

        //System.out.println("w n: " + weatherNumber);
        return Weather.values()[weatherNumber].getCoefficient();
    }
}

