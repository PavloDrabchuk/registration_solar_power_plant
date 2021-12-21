package com.example.solar_power_plant.service;

import com.example.solar_power_plant.AuthorizationAccess;
import com.example.solar_power_plant.repository.SolarPowerPlantRepository;
import com.example.solar_power_plant.enums.Region;
import com.example.solar_power_plant.model.SolarPowerPlant;
import com.example.solar_power_plant.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestParam;

import javax.validation.constraints.NotNull;
import java.text.ParseException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

@Service
public class SolarPowerPlantService {
    private final SolarPowerPlantRepository solarPowerPlantRepository;
    private final EmailSenderService emailSenderService;

    @Value("${ADMIN_EMAIL}")
    private String ADMIN_EMAIL;

    @Autowired
    public SolarPowerPlantService(SolarPowerPlantRepository solarPowerPlantRepository,
                                  EmailSenderService emailSenderService) {
        this.solarPowerPlantRepository = solarPowerPlantRepository;
        this.emailSenderService = emailSenderService;
    }


    /**
     * @param solarPowerPlant
     * @param action          // 0 - create, 1 - update
     */
    public void addSolarPowerPlant(SolarPowerPlant solarPowerPlant, int action) {
        /*
        actions:
        0 - create
        1 - update
         */

        String stringId;
        Optional<SolarPowerPlant> solarPowerPlant1;

        if (action == 0) {
            do {
                stringId = UUID.randomUUID().toString();
                solarPowerPlant1 = solarPowerPlantRepository.findByStringId(stringId);
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

    public String getStringOfUsageTime(@NotNull SolarPowerPlant solarPowerPlant) {
        ArrayList<Integer> usageTime = AuthorizationAccess.getUsageTime(solarPowerPlant.getStaticData().getInstallationDate(), LocalDate.now());
        String result = "";

        if (usageTime.get(0) != 0) result += Math.abs(usageTime.get(0)) + " р. ";
        if (usageTime.get(1) != 0) result += Math.abs(usageTime.get(1)) + " міс. ";
        if (usageTime.get(2) != 0) result += Math.abs(usageTime.get(2)) + " д.";

        if (result.equals("")) result += "1 д.";

        return result;
    }

    public void addSolarPowerPlantInfoToModel(String id, Model model, boolean isAdmin) {
        Optional<SolarPowerPlant> solarPowerPlant = getSolarPowerPlantByStringId(id);

        if (solarPowerPlant.isPresent()) {
            model.addAttribute("solarPowerPlant", solarPowerPlant.get());
            model.addAttribute("regions", Region.values());
            model.addAttribute("localDate", LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd")));
            model.addAttribute("isAdmin", isAdmin);
        } else {
            model.addAttribute("solarPowerPlantChangeError", "Помилка, спробуйте пізніше.");
        }
    }

    public void updateSolarPowerPlant(SolarPowerPlant newSolarPowerPlant,
                                      @RequestParam(value = "installationDate") String installationDate) throws ParseException {

        Optional<SolarPowerPlant> updatedSolarPowerPlant = getSolarPowerPlantById(newSolarPowerPlant.getId());
        if (updatedSolarPowerPlant.isPresent()) {
            updatedSolarPowerPlant.get().setName(newSolarPowerPlant.getName());

            updatedSolarPowerPlant.get().getLocation().setCountry("Україна");
            updatedSolarPowerPlant.get().getLocation().setRegion(newSolarPowerPlant.getLocation().getRegion());
            updatedSolarPowerPlant.get().getLocation().setCity(newSolarPowerPlant.getLocation().getCity());
            updatedSolarPowerPlant.get().getLocation().setStreet(newSolarPowerPlant.getLocation().getStreet());
            updatedSolarPowerPlant.get().getLocation().setNumber(newSolarPowerPlant.getLocation().getNumber());

            updatedSolarPowerPlant.get().getLocation().setLatitude(newSolarPowerPlant.getLocation().getLatitude());
            updatedSolarPowerPlant.get().getLocation().setLongitude(newSolarPowerPlant.getLocation().getLongitude());

            updatedSolarPowerPlant.get().getStaticData().setPower(newSolarPowerPlant.getStaticData().getPower());
            updatedSolarPowerPlant.get().getStaticData().setQuantity(newSolarPowerPlant.getStaticData().getQuantity());

            updatedSolarPowerPlant.get().getStaticData().setInstallationDate(installationDate);

            addSolarPowerPlant(updatedSolarPowerPlant.get(), 1);
        }
    }

    public void sendRemovingSolarPowerPlantEmail(String email) {
        System.out.println("1) ==..=.=.=.=..=.=.=.=.=.=.=.");
        String subject = "Видалення сонячної електростанції";
        String text = "Доброго дня. Вашу сонячну електростанцію видалено з системи. У разі виникнення питань звертайтесь до адміністратора: "
                + ADMIN_EMAIL + ".";

        emailSenderService.sendEmail(emailSenderService.createSimpleMail(email, subject, text));
    }
}