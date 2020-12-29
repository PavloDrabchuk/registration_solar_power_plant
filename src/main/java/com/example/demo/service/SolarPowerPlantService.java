package com.example.demo.service;

import com.example.demo.dao.SolarPowerPlantRepository;
import com.example.demo.model.SolarPowerPlant;
import com.example.demo.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class SolarPowerPlantService {
    private final SolarPowerPlantRepository solarPowerPlantRepository;

    @Autowired
    public SolarPowerPlantService(SolarPowerPlantRepository solarPowerPlantRepository){
        this.solarPowerPlantRepository=solarPowerPlantRepository;
    }

    public void addSolarPowerPlant(SolarPowerPlant solarPowerPlant){
        /*solarPowerPlantRepository.save(new SolarPowerPlant(
                solarPowerPlant.getName(),
                solarPowerPlant.getLocation(),
                solarPowerPlant.getNumber(),
                solarPowerPlant.getUser()));*/
        solarPowerPlantRepository.save(solarPowerPlant);
    }

    public Iterable<SolarPowerPlant> getAllSolarPowerPlants(){
        return solarPowerPlantRepository.findAll();
    }

    public Iterable<SolarPowerPlant> getSolarPowerPlantsByUser(User user){
        return solarPowerPlantRepository.findAllByUser(user);
    }

    public Optional<SolarPowerPlant> getSolarPowerPlantById(Integer id){
        return  solarPowerPlantRepository.findById(id);
    }
}
