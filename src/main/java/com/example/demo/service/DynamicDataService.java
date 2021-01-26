package com.example.demo.service;

import com.example.demo.dao.DynamicDataRepository;
import com.example.demo.model.DynamicData;
import com.example.demo.model.SolarPowerPlant;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
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

    public List<DynamicData> generateData() {
        List<DynamicData> generatedData = new ArrayList<>();
        List<SolarPowerPlant> solarPowerPlantList = solarPowerPlantService.getAllSolarPowerPlants();

        double producedPower,
                leftLimit = 1D,
                rightLimit = 100D;

        for (SolarPowerPlant solarPowerPlant : solarPowerPlantList) {
            producedPower = leftLimit + new Random().nextDouble() * (rightLimit - leftLimit);
            generatedData.add(new DynamicData(solarPowerPlant, producedPower, LocalDateTime.now()));
        }

        return generatedData;
    }

    @Async
    @Scheduled(fixedRate = 5*60*1000)
    public void saveDynamicData() {
        System.out.println("\n\n save dynamic data: ");
        //dynamicDataRepository.save(dynamicData);
        for (DynamicData dynamicData : generateData()) {
            System.out.println("  id: "+dynamicData.getSolarPowerPlant().getId());
            dynamicDataRepository.save(dynamicData);
        }
    }
}

