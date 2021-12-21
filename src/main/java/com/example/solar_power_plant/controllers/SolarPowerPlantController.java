package com.example.solar_power_plant.controllers;

import com.example.solar_power_plant.AuthorizationAccess;
import com.example.solar_power_plant.enums.Region;
import com.example.solar_power_plant.model.*;
import com.example.solar_power_plant.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.json.JSONException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.text.ParseException;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.*;

@Controller
public class SolarPowerPlantController {

    private final UsersService usersService;
    private final SolarPowerPlantService solarPowerPlantService;
    private final LocationService locationService;
    private final DynamicDataService dynamicDataService;

    private Optional<User> authorizedUser = Optional.empty();

    @Autowired
    public SolarPowerPlantController(UsersService usersService,
                                     SolarPowerPlantService solarPowerPlantService,
                                     LocationService locationService,
                                     DynamicDataService dynamicDataService) {
        this.usersService = usersService;
        this.solarPowerPlantService = solarPowerPlantService;
        this.locationService = locationService;
        this.dynamicDataService = dynamicDataService;
    }

    @PostMapping(path = "/solar-power-plant/add")
    public String addSolarPowerPlant(
            @Valid
            @ModelAttribute("solarPowerPlant")
                    SolarPowerPlant solarPowerPlant,
            BindingResult bindingResult,
            Model model) throws IOException {

        if (bindingResult.hasErrors()) {
            /*for (ObjectError h : bindingResult.getAllErrors()) {
                System.out.println("  e: " + h.toString() + ", " + h.getObjectName() + "\n");
            }*/

            model.addAttribute("solarPowerPlant", solarPowerPlant);

            model.addAttribute("regions", Region.values());
            model.addAttribute("localDate", LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd")));

            return "add-solar-power-plant";
        }

        authorizedUser = AuthorizationAccess.getAuthorisedUser(this.usersService);
        authorizedUser.ifPresent(solarPowerPlant::setUser);

        locationService.createLonLatCoordinates(solarPowerPlant.getLocation());

        if (solarPowerPlant.getLocation().getLatitude() == -1) {
            Path filePath = Paths.get("system-information-files/error-location.txt");

            String message = solarPowerPlant.getLocation().getStringLocation() + "\n";
            Files.writeString(filePath, message, StandardOpenOption.APPEND);
        }

        solarPowerPlant.getLocation().setCountry("Україна");
        solarPowerPlant.setRegistrationDateTime(LocalDateTime.now(ZoneId.of("UTC")));

        solarPowerPlantService.addSolarPowerPlant(solarPowerPlant, 0);

        return "redirect:/home";
    }

    @DeleteMapping(path = "/solar-power-plant/delete/{id}")
    public String deleteSolarPowerPlant(@PathVariable("id") String stringId, RedirectAttributes redirectAttributes) {
        Optional<SolarPowerPlant> solarPowerPlant = solarPowerPlantService.getSolarPowerPlantByStringId(stringId);

        if (solarPowerPlant.isPresent()) {
            solarPowerPlantService.sendRemovingSolarPowerPlantEmail(solarPowerPlant.get().getUser().getEmail());
            solarPowerPlantService.deleteSolarPowerPlant(solarPowerPlant.get());

            redirectAttributes.addFlashAttribute("deletedSolarPowerPlantOK", "Успішно видалено!");
        } else {
            redirectAttributes.addFlashAttribute("deletedSolarPowerPlantError", "Сталась помилка, спробуйте пізніше!");

        }
        return "redirect:/home";
    }

    @GetMapping(path = "/solar-power-plant/delete/{id}")
    public String redirectToSolarPowerPlantPage(@PathVariable("id") Long id) {
        return "redirect:/home";
    }

    @GetMapping("/solar-power-plant/new")
    public String newSolarPowerPlant(Model model) {

        SolarPowerPlant solarPowerPlant = new SolarPowerPlant();
        model.addAttribute("solarPowerPlant", solarPowerPlant);

        model.addAttribute("regions", Region.values());
        model.addAttribute("localDate", LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd")));

        return "add-solar-power-plant";
    }

    @GetMapping(path = "/view/{id}")
    public String getSolarPowerPlantsById(@PathVariable("id") String stringId, Model model) {

        authorizedUser = AuthorizationAccess.getAuthorisedUser(this.usersService);

        Optional<SolarPowerPlant> solarPowerPlant = solarPowerPlantService.getSolarPowerPlantByStringId(stringId);

        if (solarPowerPlant.isPresent() && authorizedUser.isPresent() && solarPowerPlant.get().getUser() == authorizedUser.get()) {
            model.addAttribute("solarPowerPlant", solarPowerPlant.get());
            model.addAttribute("dynamicData", dynamicDataService.getDynamicDataBySolarPowerPlant(solarPowerPlant.get()));

            dynamicDataService.addTotalAndAveragePowerToModel(model, solarPowerPlant.get());
        } else {
            model.addAttribute("notFoundSolarPowerPlant", "Сонячну станцію не знайдено");
        }

        return "solar-power-plant-info-by-id";
    }

    @GetMapping(path = "/view/{id}/update")
    public String getSolarPowerPlantByIdForUpdate(@PathVariable("id") String id, Model model) {
        solarPowerPlantService.addSolarPowerPlantInfoToModel(id, model,false);
        return "dashboard/solar-power-plant/update-solar-power-plant-by-id";
    }

    @PutMapping(path = "/view/{id}/update")
    public String updateSolarPowerPlantById(@PathVariable String id,
                                            @RequestParam(value = "installationDate") String installationDate,
                                            RedirectAttributes redirectAttributes,
                                            @Valid
                                            @ModelAttribute("solarPowerPlant")
                                                    SolarPowerPlant solarPowerPlant) throws ParseException {

        solarPowerPlantService.updateSolarPowerPlant(solarPowerPlant, installationDate);

        redirectAttributes.addFlashAttribute("updateSolarPowerPlantMessage", "Інформацію про сонячну станцію оновлено.");
        return "redirect:/view/" + id;
    }

    @PostMapping(path = "/view/{id}/data")
    public String getData(@PathVariable String id,
                          @RequestParam(value = "startDate", defaultValue = "2000-01-01 00:00") String startDate,
                          @RequestParam(value = "finishDate", defaultValue = "2172-12-31 23:59") String finishDate,
                          @RequestParam(value = "dataPeriod", defaultValue = "Отримати дані") String dataPeriod,
                          Model model,
                          RedirectAttributes redirectAttributes) {

        Optional<SolarPowerPlant> solarPowerPlant = solarPowerPlantService.getSolarPowerPlantByStringId(id);

        if (solarPowerPlant.isEmpty()) {
            redirectAttributes.addFlashAttribute("getDataError", "Сталась помилка при отриманні даних. Спробуйте пізніше або зверніться до адміністратора.");
            return "redirect:/home";
        }

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
        DateTimeFormatter formatterInfoMessage = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm");
        List<DynamicData> data = new ArrayList<>();

        if (dataPeriod.equals("Отримати дані")) {

            if (startDate.equals("")) {
                startDate = LocalDateTime.now().minusHours(24).toString().substring(0, 16);
            }

            if (finishDate.equals("")) {
                finishDate = LocalDateTime.now().toString().substring(0, 16);
              }

            data = dynamicDataService.getDynamicDataBetweenCollectionDateTimeAndBySolarPowerPlant(
                    LocalDateTime.parse(startDate.replace("T", " "), formatter),
                    LocalDateTime.parse(finishDate.replace("T", " "), formatter),
                    solarPowerPlant.get());

        } else if (dataPeriod.equals("Отримати дані за весь час")) {
            Optional<DynamicData> firstDynamicData = dynamicDataService.getFirstDynamicDataBySolarPowerPlant(solarPowerPlantService.getSolarPowerPlantByStringId(id).get());
            startDate = firstDynamicData.map(dynamicData -> dynamicData.getCollectionDateTime().toString().substring(0, 16)).orElseGet(() -> LocalDateTime.now().toString().substring(0, 16));
            finishDate = LocalDateTime.now().toString().substring(0, 16);

            data = dynamicDataService.getDynamicDataBySolarPowerPlant(solarPowerPlant.get());
        }

        model.addAttribute("data", data);

        model.addAttribute("info", "Дані з " +
                LocalDateTime.parse(startDate.replace("T", " "), formatter)
                        .format(formatterInfoMessage) + " по " +
                LocalDateTime.parse(finishDate.replace("T", " "), formatter)
                        .format(formatterInfoMessage));

        model.addAttribute("startDate", startDate);
        model.addAttribute("finishDate", finishDate);
        model.addAttribute("id", id);
        model.addAttribute("solarPowerPlant", solarPowerPlant.get());
        model.addAttribute("dataForGraphsByMonth", dynamicDataService.getTotalPowers(solarPowerPlant.get()));
        model.addAttribute("dataForGraphsByHour", dynamicDataService.getAveragePowers(solarPowerPlant.get()));

        return "data";
    }

    @PostMapping(path = "/view/{id}/data/export")
    public String exportData(@PathVariable String id,
                             @RequestParam(value = "startDate", defaultValue = "2000-01-01 00:00") String startDate,
                             @RequestParam(value = "finishDate", defaultValue = "2172-12-31 23:59") String finishDate,
                             @RequestParam(value = "file-format", defaultValue = "csv") String fileFormat,
                             HttpServletResponse response,
                             RedirectAttributes redirectAttributes) throws IOException, TransformerException, ParserConfigurationException, JSONException {

        Optional<SolarPowerPlant> solarPowerPlant = solarPowerPlantService.getSolarPowerPlantByStringId(id);

        if (solarPowerPlant.isEmpty()) {
            redirectAttributes.addFlashAttribute("getDataError", "Сталась помилка при отриманні даних. Спробуйте пізніше або зверніться до адміністратора.");
            return "redirect:/home";
        }

        String filename = createFilename(solarPowerPlant.get().getName());
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

        List<DynamicData> data = dynamicDataService.getDynamicDataBetweenCollectionDateTimeAndBySolarPowerPlant(
                LocalDateTime.parse(startDate.replace("T", " "), formatter),
                LocalDateTime.parse(finishDate.replace("T", " "), formatter),
                solarPowerPlant.get());

        // TODO: 19.12.2021 Зроби функцію, де аргументом функції є розширення файлу (fileFormat.toLowerCase()).
        switch (fileFormat.toLowerCase()) {
            case "csv": {
                filename += ".csv";
                dynamicDataService.createDataCSV(filename, data);
                break;
            }
            case "xml": {
                filename += ".xml";
                dynamicDataService.createDataXML(filename, data);
                break;
            }
            case "json": {
                filename += ".json";
                dynamicDataService.createDataJSON(filename, data);
                break;
            }
            default: {
                redirectAttributes.addFlashAttribute("exportDataError", "Сталась помилка. Дані неможливо завантажити. Спробуйте пізніше.");
                return "redirect:/home";
            }

        }
        return dynamicDataService.downloadData(response, filename);
    }

    @GetMapping(path = "/view/{id}/data/export")
    public String getExportData(@PathVariable String id) {
        return "redirect:/view/" + id;
    }

    public String createFilename(String name) {
        return name.replace(' ', '_') + "-"
                + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd_HH-mm-ss"))
                + "-data";
    }
}
