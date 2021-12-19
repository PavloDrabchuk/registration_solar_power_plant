package com.example.solar_power_plant.controllers;

import com.example.solar_power_plant.AuthorizationAccess;
import com.example.solar_power_plant.enums.UserRoles;
import com.example.solar_power_plant.model.*;
import com.example.solar_power_plant.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.PropertySource;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.validation.Valid;
import java.text.ParseException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;

@Controller
@PropertySource("classpath:project.properties")
public class AdminController {

    private final UsersService usersService;
    private final SolarPowerPlantService solarPowerPlantService;
    private final DynamicDataService dynamicDataService;
    private final MessageService messageService;

    private Optional<User> authorizedUser = Optional.empty();

    @Autowired
    public AdminController(UsersService usersService,
                           SolarPowerPlantService solarPowerPlantService,
                           BCryptPasswordEncoder bCryptPasswordEncoder,
                           DynamicDataService dynamicDataService,
                           MessageService messageService) {
        this.usersService = usersService;
        this.solarPowerPlantService = solarPowerPlantService;
        this.bCryptPasswordEncoder = bCryptPasswordEncoder;
        this.dynamicDataService = dynamicDataService;
        this.messageService = messageService;
    }

    private final BCryptPasswordEncoder bCryptPasswordEncoder;

    @GetMapping(path = "/admin")
    public String getAdminPage() {
        return "admin-page";
    }

    @GetMapping(path = "/admin/users")
    public String getUsersPage(@RequestParam(value = "page", defaultValue = "1") String page,
                               @RequestParam(value = "search", required = false) String searchUsername,
                               Model model) {
        double limitUsers = 7;

        int pageInt = AuthorizationAccess.getPage(page, AuthorizationAccess
                .getNumPagesList(usersService.getAllUsers(), limitUsers));

        int pageNumList = 0;

        if (searchUsername == null) {

            model.addAttribute("users",
                    usersService.getUsersForPage(
                            (pageInt - 1) * (int) limitUsers,
                            (int) limitUsers));

            pageNumList = AuthorizationAccess.getNumPagesList(usersService.getAllUsers(), limitUsers);

        } else {
            List<User> users = usersService.getUsersByUsername(searchUsername);
            if (users.size() > 0) {

                model.addAttribute("users",
                        usersService.getUsersByUsernameForPage(
                                searchUsername,
                                (pageInt - 1) * (int) limitUsers,
                                (int) limitUsers));

                pageNumList = AuthorizationAccess.getNumPagesList(users, limitUsers);

                model.addAttribute("search", searchUsername);
            } else {
                model.addAttribute("usersNotFoundMessage", "За Вашим запитом користувачів не знайдено.");
            }
        }
        model.addAttribute("numPages", pageNumList);
        model.addAttribute("currentPage", pageInt);

        return "dashboard/admin/users";
    }

    @GetMapping(path = "/admin/users/{id}")
    public String getUserById(@PathVariable String id, Model model, RedirectAttributes redirectAttributes) {

        Optional<User> user = usersService.getUserById(Long.valueOf(id));

        if (user.isPresent()) {
            model.addAttribute("user", user.get());
            model.addAttribute("solarPowerPlants", solarPowerPlantService.getSolarPowerPlantsByUser(user.get()));
            model.addAttribute("countOfRegisteredSolarStations", solarPowerPlantService.getCountSolarPowerPlantByUser(user.get()));

            if (user.get().getLocked()) {
                model.addAttribute("accountStatus", "Заблокований");
            } else {
                Boolean accountStatus = user.get().getActivated();
                model.addAttribute("accountStatus", accountStatus ? "Активований" : "Не активований");
            }
        } else {
            redirectAttributes.addFlashAttribute("userChangeError", "Помилка, спробуйте пізніше.");
            return "redirect:/admin/users";
        }
        return "dashboard/admin/user-by-id";
    }

    @PostMapping(path = "/admin/users/{id}/set-role")
    public String updateUserRoles(@PathVariable String id,
                                  @RequestParam(value = "role", defaultValue = "ROLE_USER") String role,
                                  Model model,
                                  RedirectAttributes redirectAttributes) throws IllegalArgumentException {

        Optional<User> user = usersService.getUserById(Long.valueOf(id));

        if (user.isPresent()) {
            try {
                user.get().setUserRole(UserRoles.valueOf(role));

//              System.out.println(" -- Role: " + UserRoles.valueOf(role).name());

                usersService.saveUser(user.get());
                redirectAttributes.addFlashAttribute("updateUserMessage", "Роль користувача змінено.");
            } catch (IllegalArgumentException ex) {
                redirectAttributes.addFlashAttribute("errorSetRoleMessage", "Помилка запиту, спробуйте пізніше.");

            }
        }

        return "redirect:/admin/users/" + id;
    }

    @PostMapping(path = "/admin/users/{id}/locking")
    public String updateUserLocking(@PathVariable String id,
                                    RedirectAttributes redirectAttributes) {
        Optional<User> user = usersService.getUserById(Long.valueOf(id));
        if (user.isPresent()) {
            user.get().setLocked(!user.get().getLocked());

            usersService.saveUser(user.get());
            redirectAttributes.addFlashAttribute("lockingUserMessage",
                    "Користувача " + (user.get().getLocked() ? "заблоковано" : "розблоковано") + ".");
        } else {
            redirectAttributes.addFlashAttribute("errorLockingUserMessage", "Помилка запиту, спробуйте пізніше.");
        }
        return "redirect:/admin/users/" + id;
    }

    @GetMapping(path = "/admin/users/{id}/update")
    public String getUserByIdForUpdate(@PathVariable String id, Model model) {

        Optional<User> user = usersService.getUserById(Long.valueOf(id));

        if (user.isPresent()) {
            model.addAttribute("user", user.get());
        } else model.addAttribute("userChangeError", "Помилка, спробуйте пізніше.");

        return "dashboard/admin/update-user-by-id";
    }

    @PutMapping(path = "/admin/users/{id}/update")
    public String updateUserById(@PathVariable String id,
                                 Model model,
                                 @RequestParam(value = "username") String username,
                                 @RequestParam(value = "email") String email,
                                 RedirectAttributes redirectAttributes) {
        Optional<User> user = usersService.getUserById(Long.valueOf(id));

        if (user.isPresent()) {
            if (!username.isEmpty()) {
                user.get().setUsername(username);
            }
            if (!email.isEmpty()) {
                user.get().setEmail(email);
            }
            usersService.saveUser(user.get());
        }

        // TODO: 19.12.2021 Надіслати сповіщення для користувача про оновлення інформації. 

        redirectAttributes.addFlashAttribute("updateUserMessage", "Інформацію про користувача оновлено.");
        model.addAttribute("users", usersService.getAllUsers());

        return "redirect:/admin/users";
    }

    @GetMapping(path = "/admin/add-user")
    public String getAddUserView(Model model) {
        User user = new User();
        model.addAttribute("user", user);

        return "dashboard/admin/add-user";
    }

    @PostMapping(path = "/admin/add-user")
    public String addNewUser(@Valid @ModelAttribute("user") User user,
                             RedirectAttributes redirectAttributes) {
        user.setActivated(true);
        user.setPassword(bCryptPasswordEncoder.encode(user.getPassword()));

        usersService.saveUser(user);

        // TODO: 19.12.2021 Надіслати сповіщення для користувача про його створення.

        redirectAttributes.addFlashAttribute("addUserMessage", "Користувача успішно додано.");

        return "redirect:/admin/users";
    }

    @DeleteMapping(path = "/admin/users/{id}/delete")
    public String deleteUserById(@PathVariable String id, RedirectAttributes redirectAttributes) {
        Optional<User> user = usersService.getUserById(Long.valueOf(id));
        authorizedUser = AuthorizationAccess.getAuthorisedUser(this.usersService);

        if (user.isPresent() && authorizedUser.isPresent() && user.get() != authorizedUser.get()) {

            usersService.sendRemovingUserEmail(user.get().getEmail());
            usersService.deleteUser(user.get());

            redirectAttributes.addFlashAttribute("deleteUserMessage", "Користувача видалено з системи.");
        } else {
            redirectAttributes.addFlashAttribute("deleteUserMessage", "Сталась помилка, спробуйте пізніше.");
        }

        return "redirect:/admin/users";
    }

    @GetMapping(path = "/admin/solar-power-plants")
    public String getSolarPowerPlantsPage(@RequestParam(value = "page", defaultValue = "1") String page,
                                          @RequestParam(value = "search", required = false) String searchName,
                                          Model model) {
        model.addAttribute("solarPowerPlantsMessage", "Solar power plants :)");

        double limitSolarPowerPlants = 7;

        int pageInt = AuthorizationAccess.getPage(page, AuthorizationAccess
                .getNumPagesList(solarPowerPlantService.getAllSolarPowerPlants(), limitSolarPowerPlants));

        int pageNumList = 0;

        if (searchName == null) {

            model.addAttribute("solarPowerPlants",
                    solarPowerPlantService.getAllSolarPowerPlantByUserForPage(
                            (pageInt - 1) * (int) limitSolarPowerPlants,
                            (int) limitSolarPowerPlants));

            pageNumList = AuthorizationAccess.getNumPagesList(solarPowerPlantService.getAllSolarPowerPlants(), limitSolarPowerPlants);

        } else {
            List<SolarPowerPlant> solarPowerPlants = solarPowerPlantService.getSolarPowerPlantsByName(searchName);

            if (solarPowerPlants.size() > 0) {
                model.addAttribute("solarPowerPlants",
                        solarPowerPlantService.getSolarPowerPlantsByNameForPage(
                                searchName,
                                (pageInt - 1) * (int) limitSolarPowerPlants,
                                (int) limitSolarPowerPlants));

                pageNumList = AuthorizationAccess.getNumPagesList(solarPowerPlants, limitSolarPowerPlants);

                model.addAttribute("search", searchName);
            } else {
                model.addAttribute("solarPowerPlantNotFoundMessage", "За Вашим запитом станції не знайдено.");
            }

        }
        model.addAttribute("numPages", pageNumList);
        model.addAttribute("currentPage", pageInt);

        return "dashboard/admin/solar-power-plants";
    }

    @GetMapping(path = "/admin/solar-power-plants/{id}")
    public String getSolarPowerPlantById(@PathVariable String id, Model model) {

        Optional<SolarPowerPlant> solarPowerPlant = solarPowerPlantService.getSolarPowerPlantByStringId(id);

        if (solarPowerPlant.isPresent()) {
            model.addAttribute("solarPowerPlant", solarPowerPlant.get());
            dynamicDataService.addTotalAndAveragePowerToModel(model, solarPowerPlant.get());
        } else {
            model.addAttribute("solarPowerPlantChangeError", "Помилка, спробуйте пізніше.");
        }

        return "dashboard/admin/solar-power-plant-by-id";
    }

    @GetMapping(path = "/admin/solar-power-plants/{id}/update")
    public String getSolarPowerPlantByIdForUpdate(@PathVariable String id, Model model) {
        solarPowerPlantService.addSolarPowerPlantInfoToModel(id, model, true);

        return "dashboard/solar-power-plant/update-solar-power-plant-by-id";
    }

    @PutMapping(path = "/admin/solar-power-plants/{id}/update")
    public String updateSolarPowerPlantById(@PathVariable String id,
                                            Model model,
                                            @RequestParam(value = "installationDate") String installationDate,
                                            RedirectAttributes redirectAttributes,
                                            @Valid SolarPowerPlant solarPowerPlant) throws ParseException {

        authorizedUser = AuthorizationAccess.getAuthorisedUser(this.usersService);

        solarPowerPlantService.updateSolarPowerPlant(solarPowerPlant, installationDate);

        User recipient = solarPowerPlantService.getSolarPowerPlantByStringId(id).orElseThrow().getUser();

        String title, text;

        title = "Сповіщення про оновлення інформації про сонячну електростанцію.";
        text = "Інформацію про Вашу сонячну електростанцію оновлено адміністратором. <br>Назва електростанції: " + solarPowerPlant.getName()
                + ". <br>Дані оновлено: " + LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd.MM.yyyy hh:mm:ss"))
                + ".";

        Message informationMessage = messageService.prepareMessage(title, text, authorizedUser,
                recipient, "INFORMATION");
        messageService.save(informationMessage);

        redirectAttributes.addFlashAttribute("updateSolarPowerPlantMessage", "Інформацію про сонячну станцію оновлено.");
        model.addAttribute("solarPowerPlants", solarPowerPlantService.getAllSolarPowerPlants());

        return "redirect:/admin/solar-power-plants";
    }

    @DeleteMapping(path = "/admin/solar-power-plants/{id}/delete")
    public String deleteSolarPowerPlantById(@PathVariable String id, Model model, RedirectAttributes redirectAttributes) {

        Optional<SolarPowerPlant> solarPowerPlant = solarPowerPlantService.getSolarPowerPlantByStringId(id);

        if (solarPowerPlant.isPresent()) {

            solarPowerPlantService.sendRemovingSolarPowerPlantEmail(solarPowerPlant.get().getUser().getEmail());
            solarPowerPlantService.deleteSolarPowerPlant(solarPowerPlant.get());

            redirectAttributes.addFlashAttribute("deleteSolarPowerPlantMessage", "Сонячну станцію видалено з системи.");
        } else {
            redirectAttributes.addFlashAttribute("deleteSolarPowerPlantMessage", "Сталась помилка, спробуйте пізніше.");
        }
        model.addAttribute("solarPowerPlants", solarPowerPlantService.getAllSolarPowerPlants());

        return "redirect:/admin/solar-power-plants";
    }
}
