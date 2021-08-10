package com.example.solar_power_plant.service;

import com.example.solar_power_plant.dao.SolarPowerPlantRepository;
import com.example.solar_power_plant.model.Region;
import com.example.solar_power_plant.model.SolarPowerPlant;
import com.example.solar_power_plant.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.ui.Model;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

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

        if (action == 0) {
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

    public List<String> getNumPagesList(User user, double limit) {
        //double limitTracksId = 2;

        //List<String> listTrackId = tracksRepository.getListTrackId();
        //List<String> listTrackId = tracksRepository.getListTrackIdForPage((Integer.parseInt(page) - 1) * (int) limitTracksId, (int) limitTracksId);
        List<String> pageNumList = new ArrayList<>();
        for (int i = 1; i <= ((int) Math.ceil(getSolarPowerPlantsByUser(user).size() / limit)); i++) {
            pageNumList.add(Integer.toString(i));
        }
        return pageNumList;
    }

    public List<String> getNumPagesListForAll(List<SolarPowerPlant> solarPowerPlants, double limit) {
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

    public List<SolarPowerPlant> getSolarPowerPlantByUserForPage(Long id, int offset, int limit) {
        return solarPowerPlantRepository.getListSolarPowerPlantForPage(id, offset, limit);
    }

    public List<SolarPowerPlant> getAllSolarPowerPlantByUserForPage(int offset, int limit) {
        return solarPowerPlantRepository.getListOfAllSolarPowerPlantForPage(offset, limit);
    }

    public void deleteSolarPowerPlant(SolarPowerPlant solarPowerPlant) {
        solarPowerPlantRepository.delete(solarPowerPlant);
    }

    public Integer getCountSolarPowerPlantByUser(User user) {
        return solarPowerPlantRepository.countAllByUser(user);
    }

    public String getUsageTime(SolarPowerPlant solarPowerPlant) {

        LocalDate date = LocalDate.now();
        int year, month, day;
        String result = "";

        Calendar calendar = new GregorianCalendar();
        calendar.setTime(solarPowerPlant.getStaticData().getInstallationDate());

        year = date.getYear();
        month = date.getMonthValue();
        day = date.getDayOfMonth();

        System.out.println("year: " + year + " month: " + month + " day: " + day);

        day -= calendar.get(Calendar.DAY_OF_MONTH);
        if (day < 0) month--;
        month -= (calendar.get(Calendar.MONTH) + 1);
        if (month < 0) year--;
        year -= calendar.get(Calendar.YEAR);

        System.out.println("--- year: " + year + " month: " + month + " day: " + day);


        if (year != 0) result += Math.abs(year) + " р. ";
        if (month != 0) result += Math.abs(month) + " міс. ";
        if (day != 0) result += Math.abs(day) + " д.";

        if (result.equals("")) result += "1 д.";

        System.out.println("result: " + result);

        return result;
    }

    public void addSolarPowerPlantInfoToModel(String id, Model model) {
        Optional<SolarPowerPlant> solarPowerPlant = getSolarPowerPlantByStringId(id);

        if (solarPowerPlant.isPresent()) {
            model.addAttribute("solarPowerPlant", solarPowerPlant.get());
            model.addAttribute("regions", Region.values());
            model.addAttribute("localDate", LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd")));
        } else model.addAttribute("solarPowerPlantChangeError", "Помилка, спробуйте пізніше.");
    }

}