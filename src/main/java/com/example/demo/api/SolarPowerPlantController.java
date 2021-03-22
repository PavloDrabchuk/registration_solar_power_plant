package com.example.demo.api;

import com.example.demo.model.DynamicData;
import com.example.demo.model.Region;
import com.example.demo.model.SolarPowerPlant;
import com.example.demo.model.User;
import com.example.demo.service.DynamicDataService;
import com.example.demo.service.LocationService;
import com.example.demo.service.SolarPowerPlantService;
import com.example.demo.service.UsersService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.io.IOException;
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
    public String addSolarPowerPlant(@Valid @ModelAttribute("solarPowerPlant") SolarPowerPlant solarPowerPlant) throws IOException {
        System.out.println("addSolarPowerPlant");

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String username = auth.getName();//get logged in username

        Optional<User> user = usersService.getUserByUsername(username);
        if (user.isPresent()) {
            System.out.println("username: " + username + " \n userId: " + user.get().getId());

            solarPowerPlant.setUser(user.get());
        }
        System.out.println("Region: " + solarPowerPlant.getLocation().getRegion().getName());

        locationService.createLonLatCoordinates(solarPowerPlant.getLocation());
        solarPowerPlant.getLocation().setCountry("Україна");
        solarPowerPlant.setRegistrationDateTime(LocalDateTime.now(ZoneId.of("UTC")));
//        solarPowerPlant.getStaticData().setStringInstallationDate();
        System.out.println("Installation date: " + solarPowerPlant.getStaticData().getInstallationDate());
        solarPowerPlantService.addSolarPowerPlant(solarPowerPlant);
        return "redirect:/home";
    }

    @PostMapping(path = "/solarPowerPlant/delete/{id}")
    public String deleteSolarPowerPlant(@PathVariable("id") String stringId, Model model) {
        Optional<SolarPowerPlant> solarPowerPlant = solarPowerPlantService.getSolarPowerPlantByStringId(stringId);
        if (solarPowerPlant.isPresent()) {
            System.out.println("Is present!: " + solarPowerPlant.get().getId());
            solarPowerPlantService.deleteSolarPowerPlant(solarPowerPlant.get());

            model.addAttribute("deletedSolarPowerPlantOK", "Успішно видалено!");

            return "solar_power_plant_info_by_id";
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
        return "add_solar_power_plant";
    }

    @GetMapping(path = "/view/{id}")
    public String getSolarPowerPlantsById(@PathVariable("id") String stringId, Model model) {
        System.out.println("getSolarPowerPlantsById: " + stringId);

        Optional<SolarPowerPlant> solarPowerPlant = solarPowerPlantService.getSolarPowerPlantByStringId(stringId);

        Optional<SolarPowerPlant> solarPowerPlant1 = solarPowerPlantService.getSolarPowerPlantById(1L);

        // System.out.println("solarPowerPlant: "+solarPowerPlant1.get().getStringId());

        if (solarPowerPlant.isPresent()) {
            model.addAttribute("solarPowerPlant", solarPowerPlant);
            model.addAttribute("dynamicData", dynamicDataService.getDynamicDataBySolarPowerPlant(solarPowerPlant.get()));

        } else {
            model.addAttribute("notFoundSolarPowerPlant", "Сонячну станцію не знайдено");
        }

        return "solar_power_plant_info_by_id";
    }

    @PostMapping(path = "/view/{id}/data")
    public String getData(@PathVariable String id,
                          @RequestParam(value = "startDate", defaultValue = "World") String startDate,
                          @RequestParam(value = "finishDate", defaultValue = "World") String finishDate,
                          Model model) {
        model.addAttribute("info", startDate + " - " + finishDate + "\nid: " + id);

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

        model.addAttribute("startDate", startDate);
        model.addAttribute("finishDate", finishDate);
        model.addAttribute("id", id);

        model.addAttribute("data", dynamicDataService.getDynamicDataBetweenCollectionDateTimeAndBySolarPowerPlant(
                LocalDateTime.parse(startDate + " 00:00", formatter),
                LocalDateTime.parse(finishDate + " 00:00", formatter),
                solarPowerPlantService.getSolarPowerPlantByStringId(id).get()));
        return "data";
    }

    @PostMapping(path = "/view/{id}/data/export")
    public String exportData(@PathVariable String id,
                             @RequestParam(value = "startDate", defaultValue = "World") String startDate,
                             @RequestParam(value = "finishDate", defaultValue = "World") String finishDate,
                             @RequestParam(value = "file-format", defaultValue = "World") String fileFormat,
                             Model model,
                             HttpServletRequest request,
                             HttpServletResponse response) throws IOException {


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

        String filename = getFilename(solarPowerPlantService.getSolarPowerPlantByStringId(id).get().getName());

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

        List<DynamicData> data = dynamicDataService.getDynamicDataBetweenCollectionDateTimeAndBySolarPowerPlant(
                LocalDateTime.parse(startDate + " 00:00", formatter),
                LocalDateTime.parse(finishDate + " 00:00", formatter),
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

    public String getFilename(String name) {
        //String filename = "";
        //String date = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd-hh-mm-ss"));
        return reformatName(name) + "-"
                + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd_HH-mm-ss"))
                + "-data";
    }

    public String reformatName(String name) {
        return name.replace(' ', '_');
    }
}
