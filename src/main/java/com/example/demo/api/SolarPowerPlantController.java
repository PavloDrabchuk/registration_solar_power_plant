package com.example.demo.api;

import com.example.demo.model.SolarPowerPlant;
import com.example.demo.model.User;
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

import java.util.Map;
import java.util.Optional;

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
        System.out.println("addSolarPowerPlant");

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String username = auth.getName();//get logged in username

        User user = usersService.getUserByUsername(username);

        System.out.println("username: "+username+" \n userId: "+user.getId());

        solarPowerPlant.setUser(user);

        solarPowerPlantService.addSolarPowerPlant(solarPowerPlant);
        return "redirect:/home";
    }

    @GetMapping("/newSolarPowerPlant")
    public String newSolarPowerPlant(Model model) {
        System.out.println("newSolarPowerPlant");

        SolarPowerPlant solarPowerPlant = new SolarPowerPlant();
        model.addAttribute("solarPowerPlant", solarPowerPlant);
        return "add_solar_power_plant";
    }

    @GetMapping(path = "/view/{id}")
    public String getSolarPowerPlantsById(@PathVariable("id") Long id, Model model) {
        System.out.println("getSolarPowerPlantsById");
        //Model model=new Model("getall");

        /*List<User> userList=new ArrayList<>();
        userList.add(new User(1,"Name1","Surname1"));
        userList.add(new User(2,"Name2","Surname2"));*/

        /*Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String username = auth.getName();//get logged in username
        User user = usersService.getUserByUsername(username);*/

        Optional<SolarPowerPlant> solarPowerPlant=solarPowerPlantService.getSolarPowerPlantById(id);

        System.out.println(" -- id: "+solarPowerPlant.get().getId());

        //ModelAndView modelAndView = new ModelAndView("solar_power_plant_info_by_id");
        //modelAndView.addObject("users", usersService.getAllUsers());
        //modelAndView.addObject("solarPowerPlants", solarPowerPlantService.getAllSolarPowerPlants());
        model.addAttribute("solarPowerPlant", solarPowerPlantService.getSolarPowerPlantById(id));
        //modelAndView.addObject("name", username);
        //return usersService.getAllUsers();
        return "solar_power_plant_info_by_id";
    }
}
