package com.example.solar_power_plant.service;

import com.example.solar_power_plant.AuthorizationAccess;
import com.example.solar_power_plant.dao.SolarPowerPlantRepository;
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

    public String getStringOfUsageTime(@NotNull SolarPowerPlant solarPowerPlant) {

        /*LocalDate date = LocalDate.now();
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

        System.out.println("--- year: " + year + " month: " + month + " day: " + day);*/

        ArrayList<Integer> usageTime = AuthorizationAccess.getUsageTime(solarPowerPlant.getStaticData().getInstallationDate(), LocalDate.now());
        String result = "";

        if (usageTime.get(0) != 0) result += Math.abs(usageTime.get(0)) + " р. ";
        if (usageTime.get(1) != 0) result += Math.abs(usageTime.get(1)) + " міс. ";
        if (usageTime.get(2) != 0) result += Math.abs(usageTime.get(2)) + " д.";

        if (result.equals("")) result += "1 д.";

        System.out.println("result: " + result);

        return result;
    }

    public void addSolarPowerPlantInfoToModel(String id, Model model, boolean isAdmin) {
        Optional<SolarPowerPlant> solarPowerPlant = getSolarPowerPlantByStringId(id);

        if (solarPowerPlant.isPresent()) {
            model.addAttribute("solarPowerPlant", solarPowerPlant.get());
            model.addAttribute("regions", Region.values());
            model.addAttribute("localDate", LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd")));
            model.addAttribute("isAdmin", isAdmin);
        } else model.addAttribute("solarPowerPlantChangeError", "Помилка, спробуйте пізніше.");
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


            // BeanUtils.copyProperties(updatedSolarPowerPlant,solarPowerPlant);
            //updatedSolarPowerPlant.get().setLocation(solarPowerPlant.getLocation());

            //updatedSolarPowerPlant.get().setLocation(solarPowerPlant.getLocation());

            //updatedSolarPowerPlant.get().setStaticData(solarPowerPlant.getStaticData());

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

        //emailSenderService.sendEmailWithSubjectAndText(email, subject, text);
        emailSenderService.sendEmail(emailSenderService.createSimpleMail(email, subject, text));
    }

}