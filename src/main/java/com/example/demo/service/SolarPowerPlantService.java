package com.example.demo.service;

import com.example.demo.dao.SolarPowerPlantRepository;
import com.example.demo.model.SolarPowerPlant;
import com.example.demo.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class SolarPowerPlantService {
    private final SolarPowerPlantRepository solarPowerPlantRepository;

    @Autowired
    public SolarPowerPlantService(SolarPowerPlantRepository solarPowerPlantRepository) {
        this.solarPowerPlantRepository = solarPowerPlantRepository;
    }

    public void addSolarPowerPlant(SolarPowerPlant solarPowerPlant, int action) {
        /*
        actions:
        0 - create
        1 - update
         */

        /*solarPowerPlantRepository.save(new SolarPowerPlant(
                solarPowerPlant.getName(),
                solarPowerPlant.getLocation(),
                solarPowerPlant.getNumber(),
                solarPowerPlant.getUser()));*/
        String stringId;
        Optional<SolarPowerPlant> solarPowerPlant1;

        if(action==0) {
            do {
                stringId = UUID.randomUUID().toString();
                solarPowerPlant1 = solarPowerPlantRepository.findByStringId(stringId);
                System.out.println("stringId: " + stringId);
            } while (solarPowerPlant1.isPresent());


            solarPowerPlant.setStringId(stringId);
        }

        solarPowerPlantRepository.save(solarPowerPlant);
    }

    public List<SolarPowerPlant> getAllSolarPowerPlants() {
        return (List<SolarPowerPlant>) solarPowerPlantRepository.findAll();
    }

    public List<SolarPowerPlant> getSolarPowerPlantsByUser(User user) {
        return (List<SolarPowerPlant>) solarPowerPlantRepository.findAllByUser(user);
    }

    public Optional<SolarPowerPlant> getSolarPowerPlantById(Long id) {
        return solarPowerPlantRepository.findById(id);
    }

    public Optional<SolarPowerPlant> getSolarPowerPlantByStringId(String stringId) {
        return solarPowerPlantRepository.findByStringId(stringId);
    }

    public List<String> getNumPagesList(User user,double limit) {
        //double limitTracksId = 2;

        //List<String> listTrackId = tracksRepository.getListTrackId();
        //List<String> listTrackId = tracksRepository.getListTrackIdForPage((Integer.parseInt(page) - 1) * (int) limitTracksId, (int) limitTracksId);
        List<String> pageNumList = new ArrayList<>();
        for (int i = 1; i <= ((int) Math.ceil(getSolarPowerPlantsByUser(user).size() / limit)); i++) {
            pageNumList.add(Integer.toString(i));
        }
        return pageNumList;
    }

    public List<String> getNumPagesListForAll(List<SolarPowerPlant> solarPowerPlants,double limit) {
        //double limitTracksId = 2;

        //List<String> listTrackId = tracksRepository.getListTrackId();
        //List<String> listTrackId = tracksRepository.getListTrackIdForPage((Integer.parseInt(page) - 1) * (int) limitTracksId, (int) limitTracksId);
        List<String> pageNumList = new ArrayList<>();
        for (int i = 1; i <= ((int) Math.ceil(solarPowerPlants.size() / limit)); i++) {
            pageNumList.add(Integer.toString(i));
        }
        return pageNumList;
    }

    public List<SolarPowerPlant> getSolarPowerPlantsByName(String name) {
        return solarPowerPlantRepository.getSolarPowerPlantByNameContaining(name);
    }

    public List<SolarPowerPlant> getSolarPowerPlantsByNameForPage(String name, int offset, int limit) {
        return solarPowerPlantRepository.getListSolarPowerPlantsByNameForPage(name, offset, limit);
    }

    public List<SolarPowerPlant> getSolarPowerPlantByUserForPage(Long id, int offset,int limit){
        return solarPowerPlantRepository.getListSolarPowerPlantForPage(id, offset,limit);
    }

    public List<SolarPowerPlant> getAllSolarPowerPlantByUserForPage(int offset,int limit){
        return solarPowerPlantRepository.getListOfAllSolarPowerPlantForPage(offset,limit);
    }

    public void deleteSolarPowerPlant(SolarPowerPlant solarPowerPlant){
        solarPowerPlantRepository.delete(solarPowerPlant);
    }

    public Integer getCountSolarPowerPlantByUser(User user){
        return solarPowerPlantRepository.countAllByUser(user);
    }

}