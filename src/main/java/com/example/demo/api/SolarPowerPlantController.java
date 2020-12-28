package com.example.demo.api;

import com.example.demo.model.SolarPowerPlant;
import com.example.demo.model.User;
import com.example.demo.service.SolarPowerPlantService;
import com.example.demo.service.UsersService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

import java.util.Map;

@Controller
public class SolarPowerPlantController {

    private final UsersService usersService;
    private final SolarPowerPlantService solarPowerPlantService;


    @Autowired
    public SolarPowerPlantController(UsersService usersService,
            SolarPowerPlantService solarPowerPlantService){
        this.usersService=usersService;
        this.solarPowerPlantService=solarPowerPlantService;
    }

    @PostMapping(path="/addSolarPowerPlant")
    public String addSolarPowerPlant(@ModelAttribute("solarPowerPlant") SolarPowerPlant solarPowerPlant){
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String username = auth.getName();//get logged in username

        User user = usersService.getUserByUsername(username);

        System.out.println("username: "+username+" \n userId: "+user.getId());

        solarPowerPlant.setUser(user);

        solarPowerPlantService.addSolarPowerPlant(solarPowerPlant);
        return "redirect:/";
    }

    @GetMapping("/newSolarPowerPlant")
    public String newSolarPowerPlant(Map<String, Object> model) {
        SolarPowerPlant solarPowerPlant = new SolarPowerPlant();
        model.put("solarPowerPlant", solarPowerPlant);
        return "add_solar_power_plant";
    }
}
