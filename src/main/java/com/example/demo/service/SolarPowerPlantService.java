package com.example.demo.service;

import com.example.demo.dao.SolarPowerPlantRepository;
import com.example.demo.model.SolarPowerPlant;
import com.example.demo.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class SolarPowerPlantService {
    private final SolarPowerPlantRepository solarPowerPlantRepository;

    @Autowired
    public SolarPowerPlantService(SolarPowerPlantRepository solarPowerPlantRepository) {
        this.solarPowerPlantRepository = solarPowerPlantRepository;
    }

    public void addSolarPowerPlant(SolarPowerPlant solarPowerPlant) {
        /*solarPowerPlantRepository.save(new SolarPowerPlant(
                solarPowerPlant.getName(),
                solarPowerPlant.getLocation(),
                solarPowerPlant.getNumber(),
                solarPowerPlant.getUser()));*/
        solarPowerPlantRepository.save(solarPowerPlant);
    }

    public List<SolarPowerPlant> getAllSolarPowerPlants() {
        return (List<SolarPowerPlant>) solarPowerPlantRepository.findAll();
    }

    public Iterable<SolarPowerPlant> getSolarPowerPlantsByUser(User user) {
        return solarPowerPlantRepository.findAllByUser(user);
    }

    public Optional<SolarPowerPlant> getSolarPowerPlantById(Long id) {
        return solarPowerPlantRepository.findById(id);
    }

    public List<String> getNumPagesList(double limit) {
        //double limitTracksId = 2;

        //List<String> listTrackId = tracksRepository.getListTrackId();
        //List<String> listTrackId = tracksRepository.getListTrackIdForPage((Integer.parseInt(page) - 1) * (int) limitTracksId, (int) limitTracksId);
        List<String> pageNumList = new ArrayList<>();
        for (int i = 1; i <= ((int) Math.ceil(getAllSolarPowerPlants().size() / limit)); i++) {
            pageNumList.add(Integer.toString(i));
        }
        return pageNumList;
    }

    public List<SolarPowerPlant> getSolarPowerPlantByUserForPage(int offset,int limit){
        return solarPowerPlantRepository.getListSolarPowerPlantForPage(offset,limit);
    }

    public void deleteSolarPowerPlant(Long id){
        solarPowerPlantRepository.deleteById(id);
    }

}