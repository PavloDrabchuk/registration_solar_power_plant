package com.example.solar_power_plant.controllers;

import com.example.solar_power_plant.dao.DaMaS;
import com.example.solar_power_plant.model.*;
import com.example.solar_power_plant.service.DynamicDataService;
import com.example.solar_power_plant.service.LocationService;
import com.example.solar_power_plant.service.SolarPowerPlantService;
import com.example.solar_power_plant.service.UsersService;
import org.springframework.beans.factory.annotation.Autowired;
import org.json.JSONException;
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

    @PostMapping(path = "/addSolarPowerPlant")
    public String addSolarPowerPlant(
            @Valid
            @ModelAttribute("solarPowerPlant")
                    SolarPowerPlant solarPowerPlant,
            BindingResult bindingResult,
            Model model) throws IOException {

        if (bindingResult.hasErrors()) {
            System.out.println("-=-= errors");
            for (ObjectError h:bindingResult.getAllErrors()) {
                System.out.println("  e: "+h.toString()+", "+h.getObjectName()+"\n");
            }

            model.addAttribute("solarPowerPlant", solarPowerPlant);

            model.addAttribute("regions", Region.values());
            model.addAttribute("localDate", LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd")));

            addAdminAccessToModel(model);

            return "add_solar_power_plant";
            //return "redirect:/newSolarPowerPlant";
        }

        System.out.println("addSolarPowerPlant");

        Authentication auth = SecurityContextHolder
                .getContext()
                .getAuthentication();
        String username = auth.getName();//get logged in username

        Optional<User> user = usersService.getUserByUsername(username);
        if (user.isPresent()) {
            System.out.println("username: " + username + " \n userId: " + user.get().getId());

            solarPowerPlant.setUser(user.get());
        }
        System.out.println("Region: " + solarPowerPlant.getLocation().getRegion().getName());

        locationService.createLonLatCoordinates(solarPowerPlant.getLocation());
        solarPowerPlant.getLocation().setCountry("Україна");
        solarPowerPlant.setRegistrationDateTime(
                LocalDateTime.now(ZoneId.of("UTC")));
//        solarPowerPlant.getStaticData().setStringInstallationDate();
        System.out.println("Installation date: " + solarPowerPlant.getStaticData().getInstallationDate());
        solarPowerPlantService.addSolarPowerPlant(solarPowerPlant, 0);
        return "redirect:/home";
    }

    @PostMapping(path = "/solarPowerPlant/delete/{id}")
    public String deleteSolarPowerPlant(@PathVariable("id") String stringId, Model model, RedirectAttributes redirectAttributes) {
        Optional<SolarPowerPlant> solarPowerPlant = solarPowerPlantService.getSolarPowerPlantByStringId(stringId);
        if (solarPowerPlant.isPresent()) {
            System.out.println("Is present!: " + solarPowerPlant.get().getId());
            solarPowerPlantService.deleteSolarPowerPlant(solarPowerPlant.get());

            redirectAttributes.addFlashAttribute("deletedSolarPowerPlantOK", "Успішно видалено!");

            //return "solar_power_plant_info_by_id";
            return "redirect:/home";
        } else {
            return "redirect:/home";
        }
    }

    @GetMapping(path = "/solarPowerPlant/delete/{id}")
    public String redirectToSolarPowerPlantPage(@PathVariable("id") Long id, Model model) {

        return "redirect:/home";
    }

    @GetMapping("/newSolarPowerPlant")
    public String newSolarPowerPlant(Model model) {
        System.out.println("newSolarPowerPlant");

        SolarPowerPlant solarPowerPlant = new SolarPowerPlant();
        model.addAttribute("solarPowerPlant", solarPowerPlant);

        model.addAttribute("regions", Region.values());
        model.addAttribute("localDate", LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd")));

        /*Optional<User> user=getAuthorisedUser();

        if (user.isPresent() && user.get().getUserRoles()== UserRoles.ADMIN) {
            model.addAttribute("adminAccess", "admin");
            System.out.println("admin access");
        }*/
        addAdminAccessToModel(model);

        return "add_solar_power_plant";
    }

    @GetMapping(path = "/view/{id}")
    public String getSolarPowerPlantsById(@PathVariable("id") String stringId, Model model) {
        System.out.println("getSolarPowerPlantsById: " + stringId);

        Optional<SolarPowerPlant> solarPowerPlant = solarPowerPlantService.getSolarPowerPlantByStringId(stringId);

        //Optional<SolarPowerPlant> solarPowerPlant1 = solarPowerPlantService.getSolarPowerPlantById(1L);

        // System.out.println("solarPowerPlant: "+solarPowerPlant1.get().getStringId());

        if (solarPowerPlant.isPresent() && getAuthorisedUser().isPresent() && solarPowerPlant.get().getUser() == getAuthorisedUser().get()) {
            model.addAttribute("solarPowerPlant", solarPowerPlant);

            for(DaMaS ff:dynamicDataService.getDataByMonthAndSolarPowerPlant(solarPowerPlant.get())) {
                System.out.println(" - - - month: " + ff.getMonth()+",  value: "+ff.getTotal());
            }
            /*for(Double ff:dynamicDataService.getDataByMonthAndSolarPowerPlant(solarPowerPlant.get()).values()) {
                System.out.println(" - - - value: " + ff);
            }*/

            model.addAttribute("dynamicData", dynamicDataService.getDynamicDataBySolarPowerPlant(solarPowerPlant.get()));

            Double totalPower = dynamicDataService.getTotalPowerBySolarPowerPlant(solarPowerPlant.get());
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


            model.addAttribute("usingTime", solarPowerPlantService.getUsingTime(solarPowerPlant.get()));
        } else {
            model.addAttribute("notFoundSolarPowerPlant", "Сонячну станцію не знайдено");
        }

        addAdminAccessToModel(model);

        return "solar_power_plant_info_by_id";
    }

    @GetMapping(path = "/view/{id}/update")
    public String getSolarPowerPlantByIdForUpdate(@PathVariable("id") String id, Model model) {
        //System.out.println("user:== " + usersService.getUserById(Long.valueOf(id)));
        //System.out.println("integer id: " + Long.valueOf(id));
        addAdminAccessToModel(model);

        Optional<SolarPowerPlant> solarPowerPlant = solarPowerPlantService.getSolarPowerPlantByStringId(id);
        if (solarPowerPlant.isPresent()) {
            model.addAttribute("solarPowerPlant", solarPowerPlant.get());
            model.addAttribute("regions", Region.values());
            model.addAttribute("localDate", LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd")));
        } else model.addAttribute("solarPowerPlantChangeError", "Помилка, спробуйте пізніше.");

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
                                            RedirectAttributes redirectAttributes,
                                            @Valid SolarPowerPlant solarPowerPlant) throws ParseException {
        //System.out.println("user:== " + usersService.getUserById(Long.valueOf(id)));
        //System.out.println("integer id: " + Long.valueOf(id));

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String username = auth.getName();//get logged in username

        Optional<User> user = usersService.getUserByUsername(username);
        if (user.isPresent()) {
            System.out.println("username: " + username + " \n userId: " + user.get().getId());

            solarPowerPlant.setUser(user.get());
        }
        //System.out.println("Region: " + solarPowerPlant.getLocation().getRegion().getName());

        Optional<SolarPowerPlant> solarPowerPlant1 = solarPowerPlantService.getSolarPowerPlantByStringId(solarPowerPlant.getStringId());

        solarPowerPlant1.ifPresent(powerPlant -> solarPowerPlant.setRegistrationDateTime(powerPlant.getRegistrationDateTime()));
        //locationService.createLonLatCoordinates(solarPowerPlant.getLocation());
        solarPowerPlant.getLocation().setCountry("Україна");
        //solarPowerPlant.setRegistrationDateTime(LocalDateTime.now(ZoneId.of("UTC")));


        solarPowerPlantService.addSolarPowerPlant(solarPowerPlant, 1);
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
                          @RequestParam(value = "dataPeriod", defaultValue = "World") String dataPeriod,
                          Model model) {
        System.out.println("dataPeriod: " + dataPeriod);

        if (startDate.equals("")) {
            //startDate = LocalDate.now().toString()+"T00:00";
            startDate = LocalDateTime.now().minusHours(24).toString().substring(0,16);
        }
        System.out.println("... startDate: "+startDate);
        if (finishDate.equals("")) {
            //finishDate = LocalDate.now().toString()+"T23:59";
            finishDate = LocalDateTime.now().toString().substring(0,16);
            System.out.println(".....date now(): "+LocalDateTime.now().toString().substring(0,16));
            System.out.println(".....date now()-24h: "+LocalDateTime.now().minusHours(24).toString().substring(0,16));
        }
        System.out.println("... finishDate: " + finishDate);

        model.addAttribute("info", "Дані з "+startDate.replace("T"," ") + " по " + finishDate.replace("T"," "));
        //System.out.println("info: "+ startDate + " - " + finishDate + "\nid: " + id);
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
//String g=LocalDateTime.now().toString();
        model.addAttribute("startDate", startDate);
        model.addAttribute("finishDate", finishDate);
        model.addAttribute("id", id);

        if (dataPeriod.equals("Отримати дані")) {
            model.addAttribute("data", dynamicDataService.getDynamicDataBetweenCollectionDateTimeAndBySolarPowerPlant(
                    LocalDateTime.parse(startDate.replace("T", " "), formatter),
                    LocalDateTime.parse(finishDate.replace("T", " "), formatter),
                    solarPowerPlantService.getSolarPowerPlantByStringId(id).get()));
        } else if (dataPeriod.equals("Отримати дані за весь час")) {
            model.addAttribute("data", dynamicDataService.getDynamicDataBySolarPowerPlant(
                    solarPowerPlantService.getSolarPowerPlantByStringId(id).get()));
        }
        model.addAttribute("solarPowerPlant", solarPowerPlantService.getSolarPowerPlantByStringId(id).get());

        addAdminAccessToModel(model);

        return "data";
    }

    @PostMapping(path = "/view/{id}/data/export")
    public String exportData(@PathVariable String id,
                             @RequestParam(value = "startDate", defaultValue = "World") String startDate,
                             @RequestParam(value = "finishDate", defaultValue = "World") String finishDate,
                             @RequestParam(value = "file-format", defaultValue = "World") String fileFormat,
                             Model model,
                             HttpServletRequest request,
                             HttpServletResponse response) throws IOException, TransformerException, ParserConfigurationException, JSONException {


        model.addAttribute("resultMessage", "Зараз почнеться завантаження, якщо ні - натисніть на << посилання >>");

        model.addAttribute("fileFormat", fileFormat);

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

        switch (fileFormat) {
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
                return "download-file-error";
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
    public String getExportData(HttpServletRequest request,
                                HttpServletResponse response, @PathVariable String id) {
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
        return "redirect:/home";
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

    Optional<User> getAuthorisedUser() {
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
    }
}
