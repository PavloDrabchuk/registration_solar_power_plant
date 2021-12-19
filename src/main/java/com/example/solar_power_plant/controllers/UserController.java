package com.example.solar_power_plant.controllers;

import com.example.solar_power_plant.AuthorizationAccess;
import com.example.solar_power_plant.model.*;
import com.example.solar_power_plant.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.Optional;

@Controller
public class UserController {

    private final UsersService usersService;
    private final SolarPowerPlantService solarPowerPlantService;

    private Optional<User> authorizedUser = Optional.empty();

    @Autowired
    public UserController(UsersService usersService,
                          SolarPowerPlantService solarPowerPlantService) {
        this.usersService = usersService;
        this.solarPowerPlantService = solarPowerPlantService;
    }

    @GetMapping
    public String redirectToAccessPages() {

        authorizedUser = AuthorizationAccess.getAuthorisedUser(this.usersService);
        return (authorizedUser.isPresent()) ? "redirect:/home" : "index";
    }


    @GetMapping(path = "/home")
    public String getSolarPowerPlantsByUsername(Model model, @RequestParam(value = "page", defaultValue = "1") String page) {

        authorizedUser = AuthorizationAccess.getAuthorisedUser(this.usersService);

        if (authorizedUser.isPresent() && authorizedUser.get().getLocked()) {
            return "redirect:/locked-account";
        }

        if (authorizedUser.isPresent() && authorizedUser.get().getActivated()) {
            double limitSolarPowerPlant = 4;

            int pageInt = AuthorizationAccess
                    .getPage(page, AuthorizationAccess
                            .getNumPagesList(solarPowerPlantService
                                    .getSolarPowerPlantsByUser(authorizedUser.get()), limitSolarPowerPlant));

            model.addAttribute("solarPowerPlantsByUser",
                    solarPowerPlantService.getSolarPowerPlantByUserForPage(
                            authorizedUser.get().getId(),
                            (pageInt - 1) * (int) limitSolarPowerPlant,
                            (int) limitSolarPowerPlant));

            model.addAttribute("name", authorizedUser.get().getUsername());

            int pageNumList = AuthorizationAccess.getNumPagesList(solarPowerPlantService.getSolarPowerPlantsByUser(authorizedUser.get()), limitSolarPowerPlant);

            model.addAttribute("numPages", pageNumList);
            model.addAttribute("currentPage", pageInt);

            return "home";
        } else {
            return "redirect:/confirm_registration";
        }
    }

    @GetMapping(path = "/profile")
    public String goToProfilePage(Model model) {

        authorizedUser = AuthorizationAccess.getAuthorisedUser(this.usersService);

        if (authorizedUser.isPresent()) {
            model.addAttribute("userInformation", authorizedUser.get());
            model.addAttribute("countOfRegisteredSolarStations", solarPowerPlantService.getCountSolarPowerPlantByUser(authorizedUser.get()));

            // authorizedUser.get().getStringInfo();

            if (authorizedUser.get().getLocked()) {
                model.addAttribute("accountStatus", "Заблокований");
            } else {
                Boolean accountStatus = authorizedUser.get().getActivated();
                model.addAttribute("accountStatus", accountStatus ? "Активований" : "Не активований");
            }
        }
        return "profile";
    }

    @GetMapping(path = "/profile/edit")
    public String editProfileInfo(Model model) {

        authorizedUser = AuthorizationAccess.getAuthorisedUser(this.usersService);

        if (authorizedUser.isPresent()) {
            model.addAttribute("userInformation", authorizedUser.get());

            Boolean accountStatus = authorizedUser.get().getActivated();
            model.addAttribute("accountStatus", accountStatus ? "Активований" : "Не активовний");
        }

        return "edit-profile";
    }

    @PutMapping(path = "/profile/update")
    public String updateProfileInfo(@Valid @ModelAttribute("userInformation") User updatedUserInfo, BindingResult bindingResult) {
        // TODO: 19.12.2021 Validation.
        if (bindingResult.hasErrors()) {
            System.out.println("BINDING RESULT ERROR");

            return "edit-profile";
        } else {
            authorizedUser = AuthorizationAccess.getAuthorisedUser(this.usersService);
            authorizedUser.ifPresent(value -> usersService.updateUserInformation(value, updatedUserInfo));

            return "redirect:/profile";
        }
    }
}
