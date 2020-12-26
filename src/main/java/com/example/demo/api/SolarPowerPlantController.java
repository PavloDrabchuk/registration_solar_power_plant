package com.example.demo.api;

import com.example.demo.model.SolarPowerPlant;
import com.example.demo.model.User;
import com.example.demo.service.SolarPowerPlantService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

import java.util.Map;

@Controller
public class SolarPowerPlantController {
    private final SolarPowerPlantService solarPowerPlantService;

    @Autowired
    public SolarPowerPlantController(SolarPowerPlantService solarPowerPlantService){
        this.solarPowerPlantService=solarPowerPlantService;
    }

    @PostMapping(path="/addSolarPowerPlant")
    public String addSolarPowerPlant(@ModelAttribute("solarPowerPlant") SolarPowerPlant solarPowerPlant){
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
