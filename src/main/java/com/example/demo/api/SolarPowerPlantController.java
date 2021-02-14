package com.example.demo.api;

import com.example.demo.model.Location;
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
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.ModelAndView;

import javax.validation.Valid;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

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
}
