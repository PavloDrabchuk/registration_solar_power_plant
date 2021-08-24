package com.example.solar_power_plant.controllers;

import com.example.solar_power_plant.AuthorizationAccess;
import com.example.solar_power_plant.dao.DataByPeriodAndSolarPowerPlant;
import com.example.solar_power_plant.enums.Region;
import com.example.solar_power_plant.model.*;
import com.example.solar_power_plant.service.*;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.json.JSONException;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;
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
    private final MessageService messageService;
    private final EmailSenderService emailSenderService;

    private Optional<User> authorizedUser = Optional.empty();

    @Autowired
    public SolarPowerPlantController(UsersService usersService,
                                     SolarPowerPlantService solarPowerPlantService,
                                     LocationService locationService,
                                     DynamicDataService dynamicDataService,
                                     MessageService messageService,
                                     EmailSenderService emailSenderService) {
        this.usersService = usersService;
        this.solarPowerPlantService = solarPowerPlantService;

        //System.out.println("SolarPowerPlantController");

        this.locationService = locationService;
        this.dynamicDataService = dynamicDataService;
        this.messageService = messageService;
        this.emailSenderService = emailSenderService;
        //System.out.println("userServise: " + this.usersService);

        //authorizedUser = AuthorizationAccess.getAuthorisedUser(this.usersService);
    }
    //private final Optional<User> authorizedUser = AuthorizationAccess.getAuthorisedUser(getUsersService());


    /*UsersService getUsersService() {
        return this.usersService;
    }*/


    //AuthorizationAccess.add


    @PostMapping(path = "/solar-power-plant/add")
    public String addSolarPowerPlant(
            @Valid
            @ModelAttribute("solarPowerPlant")
                    SolarPowerPlant solarPowerPlant,
            BindingResult bindingResult,
            Model model) throws IOException {

        if (bindingResult.hasErrors()) {
            System.out.println("-=-= errors");
            for (ObjectError h : bindingResult.getAllErrors()) {
                System.out.println("  e: " + h.toString() + ", " + h.getObjectName() + "\n");
            }

            model.addAttribute("solarPowerPlant", solarPowerPlant);

            model.addAttribute("regions", Region.values());
            model.addAttribute("localDate", LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd")));

//            addAdminAccessToModel(model);
            //AuthorizationAccess.addAdminAccessToModel(model, usersService);

            return "add_solar_power_plant";
            //return "redirect:/newSolarPowerPlant";
        }

        System.out.println("addSolarPowerPlant");

        /*Authentication auth = SecurityContextHolder
                .getContext()
                .getAuthentication();
        String username = auth.getName();//get logged in username

        Optional<User> user = usersService.getUserByUsername(username);*/

        authorizedUser = AuthorizationAccess.getAuthorisedUser(this.usersService);

        if (authorizedUser.isPresent()) {
            System.out.println("username: " + authorizedUser.get().getUsername() + " \n userId: " + authorizedUser.get().getId());

            solarPowerPlant.setUser(authorizedUser.get());
        }
        System.out.println("Region: " + solarPowerPlant.getLocation().getRegion().getName());

        locationService.createLonLatCoordinates(solarPowerPlant.getLocation());

        if (solarPowerPlant.getLocation().getLatitude() == -1) {
            Path filePath = Paths.get("system-information-files/error-location.txt");

            String message = solarPowerPlant.getLocation().getStringLocation() + "\n";

            Files.writeString(filePath, message, StandardOpenOption.APPEND);
        }

        solarPowerPlant.getLocation().setCountry("Україна");
        solarPowerPlant.setRegistrationDateTime(LocalDateTime.now(ZoneId.of("UTC")));

//        solarPowerPlant.getStaticData().setStringInstallationDate();
        System.out.println("Installation date: " + solarPowerPlant.getStaticData().getInstallationDate());
        solarPowerPlantService.addSolarPowerPlant(solarPowerPlant, 0);
        return "redirect:/home";
    }

    @PostMapping(path = "/solar-power-plant/delete/{id}")
    public String deleteSolarPowerPlant(@PathVariable("id") String stringId, RedirectAttributes redirectAttributes) {
        Optional<SolarPowerPlant> solarPowerPlant = solarPowerPlantService.getSolarPowerPlantByStringId(stringId);
        if (solarPowerPlant.isPresent()) {
            System.out.println("Is present!: " + solarPowerPlant.get().getId());

            solarPowerPlantService.sendRemovingSolarPowerPlantEmail(solarPowerPlant.get().getUser().getEmail());
            solarPowerPlantService.deleteSolarPowerPlant(solarPowerPlant.get());

            redirectAttributes.addFlashAttribute("deletedSolarPowerPlantOK", "Успішно видалено!");

            //return "solar_power_plant_info_by_id";
//            return "redirect:/home";
        } else {
            redirectAttributes.addFlashAttribute("deletedSolarPowerPlantError", "Сталась помилка, спробуйте пізніше!");

//            return "redirect:/home";
        }
        return "redirect:/home";
    }

    @GetMapping(path = "/solar-power-plant/delete/{id}")
    public String redirectToSolarPowerPlantPage(@PathVariable("id") Long id, Model model) {

        return "redirect:/home";
    }

    @GetMapping("/solar-power-plant/new")
    public String newSolarPowerPlant(Model model) {
        System.out.println("newSolarPowerPlant");

        //authorizedUser = AuthorizationAccess.getAuthorisedUser(this.usersService);

        /*authorizedUser.ifPresent(user -> model.addAttribute("countUnreadMessages",
                messageService.getCountUnreadMessagesByUser(user)));*/

        SolarPowerPlant solarPowerPlant = new SolarPowerPlant();
        model.addAttribute("solarPowerPlant", solarPowerPlant);

        model.addAttribute("regions", Region.values());
        model.addAttribute("localDate", LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd")));

        /*Optional<User> user=getAuthorisedUser();

        if (user.isPresent() && user.get().getUserRoles()== UserRoles.ADMIN) {
            model.addAttribute("adminAccess", "admin");
            System.out.println("admin access");
        }*/
        //AuthorizationAccess.addAdminAccessToModel(model, usersService);

        return "add_solar_power_plant";
    }

    @GetMapping(path = "/view/{id}")
    public String getSolarPowerPlantsById(@PathVariable("id") String stringId, Model model) {
        System.out.println("getSolarPowerPlantsById: " + stringId);

        authorizedUser = AuthorizationAccess.getAuthorisedUser(this.usersService);

        /*authorizedUser.ifPresent(user -> model.addAttribute("countUnreadMessages",
                messageService.getCountUnreadMessagesByUser(user)));*/

        Optional<SolarPowerPlant> solarPowerPlant = solarPowerPlantService.getSolarPowerPlantByStringId(stringId);

        //Optional<SolarPowerPlant> solarPowerPlant1 = solarPowerPlantService.getSolarPowerPlantById(1L);

        // System.out.println("solarPowerPlant: "+solarPowerPlant1.get().getStringId());

        if (solarPowerPlant.isPresent() && authorizedUser.isPresent() && solarPowerPlant.get().getUser() == authorizedUser.get()) {
            model.addAttribute("solarPowerPlant", solarPowerPlant);

            /*for (DataByPeriodAndSolarPowerPlant ff : dynamicDataService.getDataByMonthAndSolarPowerPlant(solarPowerPlant.get())) {
                System.out.println(" - - - month: " + ff.getPeriod() + ",  value: " + ff.getTotal());
            }*/
            /*for(Double ff:dynamicDataService.getDataByMonthAndSolarPowerPlant(solarPowerPlant.get()).values()) {
                System.out.println(" - - - value: " + ff);
            }*/

            model.addAttribute("dynamicData", dynamicDataService.getDynamicDataBySolarPowerPlant(solarPowerPlant.get()));

            /*Double totalPower = dynamicDataService.getTotalPowerBySolarPowerPlant(solarPowerPlant.get());
            //if (totalPower != null) model.addAttribute("totalPower", String.format("%,.2f", totalPower));
            //else model.addAttribute("totalPower", "Недостатньо даних.");

            model.addAttribute("totalPower", totalPower != null ? String.format("%,.2f", totalPower) : "Недостатньо даних.");

            Double totalPowerForLarThirtyDays = dynamicDataService.getTotalPowerForLastThirtyDaysBySolarPowerPlant(solarPowerPlant.get());

            model.addAttribute("totalPowerForLarThirtyDays", totalPowerForLarThirtyDays != null ? String.format("%,.2f", totalPowerForLarThirtyDays) : "Недостатньо даних.");


            //model.addAttribute("totalPowerForLarThirtyDays",
            //        String.format("%,.2f", dynamicDataService.getTotalPowerForLastThirtyDaysBySolarPowerPlant(solarPowerPlant.get())));
            //model.addAttribute("averagePowerForDay", "Недостатньо даних.");

            Double averagePowerForDay = dynamicDataService.getAveragePowerPerDayBySolarPowerPlant(solarPowerPlant.get());
            //model.addAttribute("averagePowerForDay",
            //      String.format("%,.2f", dynamicDataService.getAveragePowerPerDayBySolarPowerPlant(solarPowerPlant.get())));

            model.addAttribute("averagePowerForDay", averagePowerForDay != null ? String.format("%,.2f", averagePowerForDay) : "Недостатньо даних.");

            System.out.println(" using time: " + solarPowerPlantService.getUsingTime(solarPowerPlant.get()));

            model.addAttribute("usingTime", solarPowerPlantService.getUsingTime(solarPowerPlant.get()));*/

            dynamicDataService.addTotalAndAveragePowerToModel(model, solarPowerPlant.get());

        } else {
            model.addAttribute("notFoundSolarPowerPlant", "Сонячну станцію не знайдено");
        }

        //AuthorizationAccess.addAdminAccessToModel(model, usersService);

        return "solar_power_plant_info_by_id";
    }

    @GetMapping(path = "/view/{id}/update")
    public String getSolarPowerPlantByIdForUpdate(@PathVariable("id") String id, Model model) {
        //System.out.println("user:== " + usersService.getUserById(Long.valueOf(id)));
        //System.out.println("integer id: " + Long.valueOf(id));

        //authorizedUser = AuthorizationAccess.getAuthorisedUser(this.usersService);

        //AuthorizationAccess.addAdminAccessToModel(model, usersService);

        /*authorizedUser.ifPresent(user -> model.addAttribute("countUnreadMessages",
                messageService.getCountUnreadMessagesByUser(user)));*/

        /*Optional<SolarPowerPlant> solarPowerPlant = solarPowerPlantService.getSolarPowerPlantByStringId(id);
        if (solarPowerPlant.isPresent()) {
            model.addAttribute("solarPowerPlant", solarPowerPlant.get());
            model.addAttribute("regions", Region.values());
            model.addAttribute("localDate", LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd")));
        } else model.addAttribute("solarPowerPlantChangeError", "Помилка, спробуйте пізніше.");*/

        solarPowerPlantService.addSolarPowerPlantInfoToModel(id, model);


        return "dashboard/user/solar-power-plant/update-solar-power-plant-by-id";
    }

    @PostMapping(path = "/view/{id}/update")
    public String updateSolarPowerPlantById(@PathVariable String id,
                                            Model model,
                                            /*@RequestParam(value = "name") String name,
                                            @RequestParam(value = "quantity") Integer quantity,
                                            @RequestParam(value = "installationDate") String installationDate,
                                            @RequestParam(value = "region") String region,
                                            @RequestParam(value = "city") String city,
                                            @RequestParam(value = "street") String street,
                                            @RequestParam(value = "number") String number,
                                            @RequestParam(value = "longitude") Double longitude,
                                            @RequestParam(value = "latitude") Double latitude,*/
//                                            @DateTimeFormat(pattern = "yyyy-MM-dd")
                                            @RequestParam(value = "installationDate") String installationDate,
                                            RedirectAttributes redirectAttributes,
                                            @Valid
                                            @ModelAttribute("solarPowerPlant")
                                                    SolarPowerPlant solarPowerPlant) throws ParseException {
        //System.out.println("user:== " + usersService.getUserById(Long.valueOf(id)));
        //System.out.println("integer id: " + Long.valueOf(id));

        //solarPowerPlant.getStaticData().setInstallationDate(installationDate);

        //solarPowerPlantService.addSolarPowerPlant(solarPowerPlant,1);
        // TODO: 09.08.2021 Optimise this method.
        // TODO: 15.08.2021 Fix installationDate.

//        System.out.println(" --> Installation date: "+solarPowerPlant.getStaticData().getInstallationDate().toString());
        System.out.println(" --> Installation date: " + installationDate);

        System.out.println("Location string: " + solarPowerPlant.getLocation().getStringLocation());
        System.out.println("Location: " + solarPowerPlant.getLocation());

        /*Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String username = auth.getName();//get logged in username*/

        System.out.println("spp info: " + solarPowerPlant.getId() + " s_id: " + solarPowerPlant.getStringId());
        System.out.println("  - spp info: " + solarPowerPlant.getName() + " s_id: " + solarPowerPlant.getLocation().getRegion());
        System.out.println("  - spp info: " + installationDate + " s_id: " + solarPowerPlant.getStaticData().getPower());

        solarPowerPlantService.updateSolarPowerPlant(solarPowerPlant, installationDate);

        /*Optional<SolarPowerPlant> updatedSolarPowerPlant = solarPowerPlantService.getSolarPowerPlantById(solarPowerPlant.getId());
        if (updatedSolarPowerPlant.isPresent()) {
            updatedSolarPowerPlant.get().setName(solarPowerPlant.getName());


            updatedSolarPowerPlant.get().getLocation().setCountry("Україна");
            updatedSolarPowerPlant.get().getLocation().setRegion(solarPowerPlant.getLocation().getRegion());
            updatedSolarPowerPlant.get().getLocation().setCity(solarPowerPlant.getLocation().getCity());
            updatedSolarPowerPlant.get().getLocation().setStreet(solarPowerPlant.getLocation().getStreet());
            updatedSolarPowerPlant.get().getLocation().setNumber(solarPowerPlant.getLocation().getNumber());

            updatedSolarPowerPlant.get().getLocation().setLatitude(solarPowerPlant.getLocation().getLatitude());
            updatedSolarPowerPlant.get().getLocation().setLongitude(solarPowerPlant.getLocation().getLongitude());


            // BeanUtils.copyProperties(updatedSolarPowerPlant,solarPowerPlant);
            //updatedSolarPowerPlant.get().setLocation(solarPowerPlant.getLocation());

            //updatedSolarPowerPlant.get().setLocation(solarPowerPlant.getLocation());

            //updatedSolarPowerPlant.get().setStaticData(solarPowerPlant.getStaticData());

            updatedSolarPowerPlant.get().getStaticData().setPower(solarPowerPlant.getStaticData().getPower());
            updatedSolarPowerPlant.get().getStaticData().setQuantity(solarPowerPlant.getStaticData().getQuantity());

            updatedSolarPowerPlant.get().getStaticData().setInstallationDate(installationDate);

            solarPowerPlantService.addSolarPowerPlant(updatedSolarPowerPlant.get(), 1);
        }*/

        //------------------------------

        /*Optional<User> user = usersService.getUserByUsername(username);
        if (user.isPresent()) {
            System.out.println("username: " + username + " \n userId: " + user.get().getId());

            solarPowerPlant.setUser(user.get());
        }
        //System.out.println("Region: " + solarPowerPlant.getLocation().getRegion().getName());

        Optional<SolarPowerPlant> solarPowerPlant1 = solarPowerPlantService.getSolarPowerPlantByStringId(solarPowerPlant.getStringId());

        solarPowerPlant1.ifPresent(powerPlant -> solarPowerPlant.setRegistrationDateTime(powerPlant.getRegistrationDateTime()));
        //locationService.createLonLatCoordinates(solarPowerPlant.getLocation());
        solarPowerPlant.getLocation().setCountry("Україна");
        //solarPowerPlant.setRegistrationDateTime(LocalDateTime.now(ZoneId.of("UTC")));*/


        //solarPowerPlantService.addSolarPowerPlant(solarPowerPlant, 1);
        /*Optional<SolarPowerPlant> solarPowerPlant = solarPowerPlantService.getSolarPowerPlantByStringId(id);
        if (solarPowerPlant.isPresent()) {
            if (!name.isEmpty()) {
                solarPowerPlant.get().setName(name);
            }
            if (quantity!=null) {
                solarPowerPlant.get().getStaticData().setQuantity(quantity);
            }
            if (installationDate!=null) {
                solarPowerPlant.get().getStaticData().setInstallationDate(installationDate);
            }
            if (region!=null) {
                solarPowerPlant.get().getLocation().setRegion(region);
            }
            if (city!=null) {
                solarPowerPlant.get().getLocation().setCity(city);
            }
            if (street!=null) {
                solarPowerPlant.get().getLocation().setStreet(street);
            }
            if (number!=null) {
                solarPowerPlant.get().getLocation().setNumber(number);
            }
            if (longitude!=null) {
                solarPowerPlant.get().getLocation().setNumber(number);
            }
            if (latitude!=null) {
                solarPowerPlant.get().getLocation().setNumber(number);
            }
            solarPowerPlantService.addSolarPowerPlant(solarPowerPlant.get(),1);
        }*/
        redirectAttributes.addFlashAttribute("updateSolarPowerPlantMessage", "Інформацію про сонячну станцію оновлено.");
        //model.addAttribute("solarPowerPlants", solarPowerPlantService.getAllSolarPowerPlants());
        return "redirect:/view/" + id;
    }

    @PostMapping(path = "/view/{id}/data")
    public String getData(@PathVariable String id,
                          @RequestParam(value = "startDate", defaultValue = "") String startDate,
                          @RequestParam(value = "finishDate", defaultValue = "") String finishDate,
                          @RequestParam(value = "dataPeriod", defaultValue = "Отримати дані") String dataPeriod,
                          Model model,
                          RedirectAttributes redirectAttributes) {
        System.out.println("dataPeriod: " + dataPeriod);

        // TODO: 09.08.2021 Optimise this method 
        //authorizedUser = AuthorizationAccess.getAuthorisedUser(this.usersService);

        /*authorizedUser.ifPresent(user -> model.addAttribute("countUnreadMessages",
                messageService.getCountUnreadMessagesByUser(user)));*/


        Optional<SolarPowerPlant> solarPowerPlant = solarPowerPlantService.getSolarPowerPlantByStringId(id);
        if (solarPowerPlant.isEmpty()) {
            redirectAttributes.addFlashAttribute("getDataError", "Сталась помилка при отриманні даних. Спробуйте пізніше або зверніться до адміністратора.");
            return "redirect:/home";
        }

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
        List<DynamicData> data = new ArrayList<>();

        if (dataPeriod.equals("Отримати дані")) {

            if (startDate.equals("")) {
                //startDate = LocalDate.now().toString()+"T00:00";
                startDate = LocalDateTime.now().minusHours(24).toString().substring(0, 16);
            }
            System.out.println("... startDate: " + startDate);
            if (finishDate.equals("")) {
                //finishDate = LocalDate.now().toString()+"T23:59";
                finishDate = LocalDateTime.now().toString().substring(0, 16);
                System.out.println(".....date now(): " + LocalDateTime.now().toString().substring(0, 16));
                System.out.println(".....date now()-24h: " + LocalDateTime.now().minusHours(24).toString().substring(0, 16));
            }
            System.out.println("... finishDate: " + finishDate);

            /*model.addAttribute("data", dynamicDataService.getDynamicDataBetweenCollectionDateTimeAndBySolarPowerPlant(
                    LocalDateTime.parse(startDate.replace("T", " "), formatter),
                    LocalDateTime.parse(finishDate.replace("T", " "), formatter),
                    solarPowerPlantService.getSolarPowerPlantByStringId(id).get()));*/

            data = dynamicDataService.getDynamicDataBetweenCollectionDateTimeAndBySolarPowerPlant(
                    LocalDateTime.parse(startDate.replace("T", " "), formatter),
                    LocalDateTime.parse(finishDate.replace("T", " "), formatter),
                    solarPowerPlant.get());

        } else if (dataPeriod.equals("Отримати дані за весь час")) {
            Optional<DynamicData> firstDynamicData = dynamicDataService.getFirstDynamicDataBySolarPowerPlant(solarPowerPlantService.getSolarPowerPlantByStringId(id).get());
            startDate = firstDynamicData.map(dynamicData -> dynamicData.getCollectionDateTime().toString().substring(0, 16)).orElseGet(() -> LocalDateTime.now().toString().substring(0, 16));

            finishDate = LocalDateTime.now().toString().substring(0, 16);
            System.out.println("... startDate: " + startDate);
            System.out.println("... finishDate: " + finishDate);

            /*model.addAttribute("data", dynamicDataService.getDynamicDataBySolarPowerPlant(
                    solarPowerPlantService.getSolarPowerPlantByStringId(id).get()));*/

            data = dynamicDataService.getDynamicDataBySolarPowerPlant(solarPowerPlant.get());
        }

        model.addAttribute("data", data);

        //DateTimeFormatter formatterForView = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

        model.addAttribute("info", "Дані з " +
                LocalDateTime.parse(startDate.replace("T", " "), formatter)
                        .format(DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm")) + " по " +
                LocalDateTime.parse(finishDate.replace("T", " "), formatter)
                        .format(DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm")));
        //System.out.println("info: "+ startDate + " - " + finishDate + "\nid: " + id);

//        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

//String g=LocalDateTime.now().toString();
        model.addAttribute("startDate", startDate);
        model.addAttribute("finishDate", finishDate);
        model.addAttribute("id", id);

        /*if (dataPeriod.equals("Отримати дані")) {
            model.addAttribute("data", dynamicDataService.getDynamicDataBetweenCollectionDateTimeAndBySolarPowerPlant(
                    LocalDateTime.parse(startDate.replace("T", " "), formatter),
                    LocalDateTime.parse(finishDate.replace("T", " "), formatter),
                    solarPowerPlantService.getSolarPowerPlantByStringId(id).get()));
        } else if (dataPeriod.equals("Отримати дані за весь час")) {
            model.addAttribute("data", dynamicDataService.getDynamicDataBySolarPowerPlant(
                    solarPowerPlantService.getSolarPowerPlantByStringId(id).get()));
        }*/
        model.addAttribute("solarPowerPlant", solarPowerPlant.get());

        List<DataByPeriodAndSolarPowerPlant> dataByMonthAndSolarPowerPlantList = dynamicDataService.getDataByMonthAndSolarPowerPlant(
                solarPowerPlant.get());

        //List<Double> totalPowers = new ArrayList<>(120);
        List<Double> totalPowers = Arrays.asList(new Double[12]);
        System.out.println("size: " + totalPowers.size());

        for (DataByPeriodAndSolarPowerPlant totalPower : dataByMonthAndSolarPowerPlantList) {
            totalPowers.set(totalPower.getPeriod() - 1, totalPower.getTotal());
        }

        model.addAttribute("dataForGraphsByMonth", totalPowers);

        List<DataByPeriodAndSolarPowerPlant> dataByHourAndSolarPowerPlantList = dynamicDataService.getDataByHourAndSolarPowerPlant(
                solarPowerPlant.get());

        List<Double> averagePowers = Arrays.asList(new Double[24]);
        System.out.println("size: " + totalPowers.size());

        for (DataByPeriodAndSolarPowerPlant totalPower : dataByHourAndSolarPowerPlantList) {
            averagePowers.set(totalPower.getPeriod(), totalPower.getTotal());
            System.out.println("th: " + totalPower.getPeriod() + "  t: " + totalPower.getTotal());
        }

        model.addAttribute("dataForGraphsByHour", averagePowers);


        //AuthorizationAccess.addAdminAccessToModel(model, usersService);

        return "data";
    }

    @PostMapping(path = "/view/{id}/data/export")
    public String exportData(@PathVariable String id,
                             @RequestParam(value = "startDate", defaultValue = "World") String startDate,
                             @RequestParam(value = "finishDate", defaultValue = "World") String finishDate,
                             @RequestParam(value = "file-format", defaultValue = "World") String fileFormat,
                             Model model,
                             HttpServletRequest request,
                             HttpServletResponse response,
                             RedirectAttributes redirectAttributes) throws IOException, TransformerException, ParserConfigurationException, JSONException {


        //model.addAttribute("resultMessage", "Зараз почнеться завантаження, якщо ні - натисніть на << посилання >>");

        //model.addAttribute("fileFormat", fileFormat);

        //Path newFilePath = Paths.get("upload-dir/1.txt");
        //Files.createFile(newFilePath);

        /*FileWriter fileWriter = new FileWriter("upload-dir/1.txt");
        PrintWriter printWriter = new PrintWriter(fileWriter);
        printWriter.print("1Some String");
        printWriter.printf("Product name is %s and its price is %d $", "iPhone", 1000);
        printWriter.close();*/

        /*List<String[]> dataLines = new ArrayList<>();
        dataLines.add(new String[]
                {"John", "Doe", "38", "Comment Data\nAnother line of comment data"});
        dataLines.add(new String[]
                {"Jane", "Doe, Jr.", "19", "She said \"I'm being quoted\""});
        givenDataArray_whenConvertToCSV_thenOutputCreated("upload-dir/f.csv", dataLines);*/

        String filename = createFilename(solarPowerPlantService.getSolarPowerPlantByStringId(id).get().getName());

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

        List<DynamicData> data = dynamicDataService.getDynamicDataBetweenCollectionDateTimeAndBySolarPowerPlant(
                LocalDateTime.parse(startDate.replace("T", " "), formatter),
                LocalDateTime.parse(finishDate.replace("T", " "), formatter),
                solarPowerPlantService.getSolarPowerPlantByStringId(id).get());

       /* System.out.println("****-*-*-*---------------");
        for(DynamicData d:data){
            System.out.println(" dd: "+d.getId()+" h: "+d.getSolarPowerPlant().getName());
        }
        System.out.println("****-*-*-*---------------");*/

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

        // ==========================
        /*String fileName = "f.csv";
        System.out.println("t-t-t-t-t-t-t");
        String dataDirectory = request.getServletContext().getRealPath("upload-dir/");
        Path file = Paths.get("upload-dir/"+fileName);

        if (Files.exists(file)) {
            System.out.println("5-5-5-5-5-5-5");

            response.setContentType("application/csv");
            response.addHeader("Content-Disposition", "attachment; filename=" + fileName);
            try {
                Files.copy(file, response.getOutputStream());
                response.getOutputStream().flush();
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        } else{
            System.out.println("File not found");
            return "download-file-error";
        }
        System.out.println("0=0=0=0=0=0=0=0=0");*/
        System.out.println("........ filename: " + filename);
        return dynamicDataService.downloadData(request, response, filename);

        // ==========================

        //return "export-data";

        //return "redirect:/view/"+id+"/data/export";
        //return "redirect:/home";
        //return null;
    }

    @GetMapping(path = "/view/{id}/data/export")
    public String getExportData(@PathVariable String id) {

        //authorizedUser = AuthorizationAccess.getAuthorisedUser(this.usersService);

        /*authorizedUser.ifPresent(user -> model.addAttribute("countUnreadMessages",
                messageService.getCountUnreadMessagesByUser(user)));*/

        /*String fileName = "f.csv";
        System.out.println("t-t-t-t-t-t-t");
        String dataDirectory = request.getServletContext().getRealPath("upload-dir/");
        Path file = Paths.get("upload-dir/"+fileName);

        if (Files.exists(file)) {
            System.out.println("5-5-5-5-5-5-5");

            response.setContentType("application/csv");
            response.addHeader("Content-Disposition", "attachment; filename=" + fileName);
            try {
                Files.copy(file, response.getOutputStream());
                response.getOutputStream().flush();
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }*/
        System.out.println("ty=ty=ty=ty=ty");
        //return "redirect:/home";
        return "redirect:/view/" + id;
    }

    /*public String convertToCSV(String[] data) {
        return Stream.of(data)
                .map(this::escapeSpecialCharacters)
                .collect(Collectors.joining(","));
    }

    public String escapeSpecialCharacters(String data) {
        String escapedData = data.replaceAll("\\R", " ");
        if (data.contains(",") || data.contains("\"") || data.contains("'")) {
            data = data.replace("\"", "\"\"");
            escapedData = "\"" + data + "\"";
        }
        return escapedData;
    }

    public void givenDataArray_whenConvertToCSV_thenOutputCreated(String fileName, List<String[]> data) throws IOException {
        File csvOutputFile = new File(fileName);
        try (PrintWriter pw = new PrintWriter(csvOutputFile)) {
            data.stream()
                    .map(this::convertToCSV)
                    .forEach(pw::println);
        }
        //assertTrue(csvOutputFile.exists());
    }*/

    public String createFilename(String name) {
        //String filename = "";
        //String date = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd-hh-mm-ss"));
        return name.replace(' ', '_') + "-"
                + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd_HH-mm-ss"))
                + "-data";
    }

    /*public String reformatName(String name) {
        return name.replace(' ', '_');
    }*/

    /*Optional<User> getAuthorisedUser() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String username = auth.getName();//get logged in username
        return usersService.getUserByUsername(username);
    }

    private void addAdminAccessToModel(Model model) {
        Optional<User> user = getAuthorisedUser();

        if (user.isPresent() && user.get().getUserRole() == UserRoles.ADMIN) {
            model.addAttribute("adminAccess", "admin");
            //System.out.println("admin access");
        }
    }*/

    /*private int getPage(String page, int maxPage) {
        int pageInt;
        try {
            pageInt = Integer.parseInt(page);
        } catch (NumberFormatException ex) {
            //System.err.println("Invalid string in argumment");
            pageInt = 1;
        }

        if (pageInt > maxPage) pageInt = 1;

        return pageInt;
    }*/

    /*@ModelAttribute("countUnreadMessages")
    public long getCountUnreadMessages(){
        authorizedUser = AuthorizationAccess.getAuthorisedUser(this.usersService);

        System.out.println(" Count unread message: "+authorizedUser.map(messageService::getCountUnreadMessagesByUser).orElse(0L));

        return authorizedUser.map(messageService::getCountUnreadMessagesByUser).orElse(0L);

        *//*authorizedUser.ifPresent(user -> model.addAttribute("countUnreadMessages",
                messageService.getCountUnreadMessagesByUser(user)));*//*
    }*/
}
