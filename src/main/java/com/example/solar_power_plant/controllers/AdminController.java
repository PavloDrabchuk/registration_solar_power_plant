package com.example.solar_power_plant.controllers;

import com.example.solar_power_plant.AuthorizationAccess;
import com.example.solar_power_plant.model.*;
import com.example.solar_power_plant.service.DynamicDataService;
import com.example.solar_power_plant.service.MessageService;
import com.example.solar_power_plant.service.SolarPowerPlantService;
import com.example.solar_power_plant.service.UsersService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.validation.Valid;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.text.ParseException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;

@Controller
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
        return "admin_page";
        //System.out.println("getAllUsers");

        /*getAuthorisedUser().ifPresent(user -> model.addAttribute("countUnreadMessages",
                messageService.getCountUnreadMessagesByUser(user)));*/

        /*Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String username = auth.getName();//get logged in username
        Optional<User> user = usersService.getUserByUsername(username);*/

        //Optional<User> user=getAuthorisedUser();
        /*authorizedUser = AuthorizationAccess.getAuthorisedUser(this.usersService);

        if (authorizedUser.isPresent() && authorizedUser.get().getUserRole().toString().equals(UserRoles.ROLE_ADMIN.name())) {


            //model.addAttribute("users", usersService.getAllUsers());
            //System.out.println("--- ADMIN ---\n role: " + authorizedUser.get().getUserRole().toString().equals(UserRoles.ROLE_ADMIN.name()));

            //addAdminAccessToModel(model);


            return "admin_page";
        } else {
            System.out.println("--- HOME ---");
            return "redirect:/home";
        }*/
    }

    @GetMapping(path = "/admin/users")
    public String getUsersPage(@RequestParam(value = "page", defaultValue = "1") String page,
                               @RequestParam(value = "search", required = false) String searchUsername,
                               Model model) {
        //model.addAttribute("usersMessage", "Users :)");

        //addAdminAccessToModel(model);

        /*getAuthorisedUser().ifPresent(user -> model.addAttribute("countUnreadMessages",
                messageService.getCountUnreadMessagesByUser(user)));*/

        //authorizedUser = AuthorizationAccess.getAuthorisedUser(this.usersService);

        //if (authorizedUser.isPresent()) {
        double limitUsers = 7;

        int pageInt = getPage(page, usersService
                .getNumPagesList(usersService.getAllUsers(),
                        limitUsers).size());

        List<String> pageNumList = null;

        if (searchUsername == null) {

            model.addAttribute("users",
                    usersService.getUsersForPage(
                            (pageInt - 1) * (int) limitUsers,
                            (int) limitUsers));

            pageNumList = usersService
                    .getNumPagesList(usersService.getAllUsers(),
                            limitUsers);

//                model.addAttribute("numPages", pageNumList);
//                model.addAttribute("currentPage", page);

        } else {
            List<User> users = usersService.getUsersByUsername(searchUsername);
            if (users.size() > 0) {

                model.addAttribute("users",
                        usersService.getUsersByUsernameForPage(
                                searchUsername,
                                (pageInt - 1) * (int) limitUsers,
                                (int) limitUsers));

                pageNumList = usersService.getNumPagesList(users, limitUsers);

//                    model.addAttribute("numPages", pageNumList);
//                    model.addAttribute("currentPage", page);
                model.addAttribute("search", searchUsername);
            } else {
                model.addAttribute("usersNotFoundMessage", "За Вашим запитом користувачів не знайдено.");
            }
        }
        model.addAttribute("numPages", pageNumList);
        model.addAttribute("currentPage", page);
        //}

        return "dashboard/admin/users";
    }

    @GetMapping(path = "/admin/users/{id}")
    public String getUserById(@PathVariable String id, Model model,RedirectAttributes redirectAttributes) {

        //addAdminAccessToModel(model);

        /*getAuthorisedUser().ifPresent(user -> model.addAttribute("countUnreadMessages",
                messageService.getCountUnreadMessagesByUser(user)));*/


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
            //model.addAttribute("countUnreadMessages", messageService.getCountUnreadMessagesByUser(user.get()));
        } else {
            redirectAttributes.addFlashAttribute("userChangeError", "Помилка, спробуйте пізніше.");
            return "redirect:/admin/users";
        }
        return "dashboard/admin/user-by-id";
    }

    /*@GetMapping(path = "/admin/users/search")
    public String searchUserByUsername(@RequestParam(value = "page", defaultValue = "1") String page,
                                       @RequestParam(value = "username", defaultValue = "") String username,
                                       Model model) {
        List<User> users = usersService.getUsersByUsername(username);
        if (users.size() > 0) {
            double limitUsers = 2;

            model.addAttribute("users",
                    usersService.getUsersByUsernameForPage(
                            username,
                            (Integer.parseInt(page) - 1) * (int) limitUsers,
                            (int) limitUsers));

            List<String> pageNumList = usersService.getNumPagesList(users, limitUsers);

            model.addAttribute("numPages", pageNumList);
            model.addAttribute("currentPage", page);
        } else {
            model.addAttribute("usersNotFoundMessage", "За Вашим запитом користувачів не знайдено.");
        }
        return "dashboard/admin/users";
    }*/

    @PostMapping(path = "/admin/users/{id}/set-role")
    public String updateUserRoles(@PathVariable String id,
                                  @RequestParam(value = "role", defaultValue = "ROLE_USER") String role,
                                  Model model,
                                  RedirectAttributes redirectAttributes) throws IllegalArgumentException {

        Optional<User> user = usersService.getUserById(Long.valueOf(id));

        if (user.isPresent()) {
            System.out.println("---((((((((((((((((: role: " + role);
            //user.get().setUserRoles(UserRoles.model);
            //user.get().setUserRole(UserRoles.valueOf(role));

            try {
                user.get().setUserRole(UserRoles.valueOf(role));

                System.out.println(" -- Role: " + UserRoles.valueOf(role).name());

                usersService.saveUser(user.get());
                redirectAttributes.addFlashAttribute("updateUserMessage", "Роль користувача змінено.");
            } catch (IllegalArgumentException ex) {
                redirectAttributes.addFlashAttribute("errorSetRoleMessage", "Помилка запиту, спробуйте пізніше.");

            }

            /*switch (role) {
                case "ROLE_USER": {
                    user.get().setUserRole(UserRoles.ROLE_USER);
                    break;
                }
                case "ROLE_ADMIN": {
                    user.get().setUserRole(UserRoles.ROLE_ADMIN);
                    break;
                }
                case "ROLE_EDITOR": {
                    user.get().setUserRole(UserRoles.ROLE_EDITOR);
                    break;
                }
                default: {
                    model.addAttribute("errorSetRoleMessage", "Помилка запиту, спробуйте пізніше.");
                    break;
                }
            }*/
//            usersService.saveUser(user.get());
//            redirectAttributes.addFlashAttribute("updateUserMessage", "Роль користувача змінено.");
        } /*else {
            //System.out.println("((((((((((((((((");
            redirectAttributes.addFlashAttribute("errorSetRoleMessage", "Помилка запиту, спробуйте пізніше.");
        }*/

        return "redirect:/admin/users/" + id;
    }

    @PostMapping(path = "/admin/users/{id}/locking")
    public String updateUserLocking(@PathVariable String id,
                                    RedirectAttributes redirectAttributes) {
        Optional<User> user = usersService.getUserById(Long.valueOf(id));
        if (user.isPresent()) {
            //Boolean locked=user.get().getLocked();
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

        //addAdminAccessToModel(model);

        /*getAuthorisedUser().ifPresent(user -> model.addAttribute("countUnreadMessages",
                messageService.getCountUnreadMessagesByUser(user)));*/

        System.out.println("user:== " + usersService.getUserById(Long.valueOf(id)));
        System.out.println("integer id: " + Long.valueOf(id));

        Optional<User> user = usersService.getUserById(Long.valueOf(id));
        if (user.isPresent()) {
            model.addAttribute("user", user.get());
        } else model.addAttribute("userChangeError", "Помилка, спробуйте пізніше.");

        return "dashboard/admin/update-user-by-id";
    }

    @PostMapping(path = "/admin/users/{id}/update")
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
        //тут можна надіслати сповіщення для користувача
        redirectAttributes.addFlashAttribute("updateUserMessage", "Інформацію про користувача оновлено.");
        model.addAttribute("users", usersService.getAllUsers());
        return "redirect:/admin/users";
    }

    @GetMapping(path = "/admin/add-user")
    public String getAddUserView(Model model) {

        //addAdminAccessToModel(model);

        /*getAuthorisedUser().ifPresent(user -> model.addAttribute("countUnreadMessages",
                messageService.getCountUnreadMessagesByUser(user)));*/

        User user = new User();
        model.addAttribute("user", user);

        return "dashboard/admin/add-user";
    }

    @PostMapping(path = "/admin/add-user")
    public String addNewUser(Model model,
                             @Valid @ModelAttribute("user") User user,
                             RedirectAttributes redirectAttributes) {

        System.out.println(" role: " + user.getUserRole());

        //user.setUserRole(UserRoles.valueOf(user.getUserRole().));
        user.setActivated(true);
        user.setLocked(false);
        user.setPassword(bCryptPasswordEncoder.encode(user.getPassword()));
        user.setDateTimeOfCreation(LocalDateTime.now());
        //System.out.println("activated: " + user.getActivated());
        usersService.saveUser(user);

        //тут можна надіслати сповіщення для користувача
        redirectAttributes.addFlashAttribute("addUserMessage", "Користувача успішно додано.");


        return "redirect:/admin/users";
    }

    @DeleteMapping(path = "/admin/users/{id}/delete")
    public String deleteUserById(@PathVariable String id, Model model, RedirectAttributes redirectAttributes) {
        //usersService.deleteUserById(Long.valueOf(id));

        Optional<User> user = usersService.getUserById(Long.valueOf(id));
        authorizedUser = AuthorizationAccess.getAuthorisedUser(this.usersService);

        if (user.isPresent() && authorizedUser.isPresent() && user.get() != authorizedUser.get()) {
            usersService.deleteUser(user.get());

            //Тут можна надіслати ласта користувачу про видалення його аккаунта

            redirectAttributes.addFlashAttribute("deleteUserMessage", "Користувача видалено з системи.");
        } else {
            redirectAttributes.addFlashAttribute("deleteUserMessage", "Сталась помилка, спробуйте пізніше.");
        }
        model.addAttribute("users", usersService.getAllUsers());

        return "redirect:/admin/users";
    }

    @GetMapping(path = "/admin/solar-power-plants")
    public String getSolarPowerPlantsPage(@RequestParam(value = "page", defaultValue = "1") String page,
                                          @RequestParam(value = "search", required = false) String searchName,
                                          Model model) {
        model.addAttribute("solarPowerPlantsMessage", "Solar power plants :)");

        //addAdminAccessToModel(model);

        /*getAuthorisedUser().ifPresent(user -> model.addAttribute("countUnreadMessages",
                messageService.getCountUnreadMessagesByUser(user)));*/

        authorizedUser = AuthorizationAccess.getAuthorisedUser(this.usersService);

        if (authorizedUser.isPresent()) {
            double limitSolarPowerPlants = 7;

            int pageInt = getPage(page, solarPowerPlantService.getNumPagesListForAll(
                    solarPowerPlantService.getAllSolarPowerPlants(), limitSolarPowerPlants).size());

            if (searchName == null) {

                model.addAttribute("solarPowerPlants",
                        solarPowerPlantService.getAllSolarPowerPlantByUserForPage(
                                (pageInt - 1) * (int) limitSolarPowerPlants,
                                (int) limitSolarPowerPlants));

                List<String> pageNumList = solarPowerPlantService.getNumPagesListForAll(
                        solarPowerPlantService.getAllSolarPowerPlants(), limitSolarPowerPlants);

                model.addAttribute("numPages", pageNumList);
                model.addAttribute("currentPage", page);

            } else {
                List<SolarPowerPlant> solarPowerPlants = solarPowerPlantService.getSolarPowerPlantsByName(searchName);

                for (SolarPowerPlant s : solarPowerPlants) {
                    System.out.println("   s: " + s.getName());
                }

                if (solarPowerPlants.size() > 0) {

                    model.addAttribute("solarPowerPlants",
                            solarPowerPlantService.getSolarPowerPlantsByNameForPage(
                                    searchName,
                                    (pageInt - 1) * (int) limitSolarPowerPlants,
                                    (int) limitSolarPowerPlants));

                    List<String> pageNumList = solarPowerPlantService.getNumPagesListForAll(solarPowerPlants, limitSolarPowerPlants);

                    for (String p : pageNumList) {
                        System.out.println("    p: " + p);
                    }

                    model.addAttribute("numPages", pageNumList);
                    model.addAttribute("currentPage", page);
                    model.addAttribute("search", searchName);
                } else {
                    model.addAttribute("solarPowerPlantNotFoundMessage", "За Вашим запитом станції не знайдено.");
                }
            }
        }

        return "dashboard/admin/solar-power-plants";
    }

    @GetMapping(path = "/admin/solar-power-plants/{id}")
    public String getSolarPowerPlantById(@PathVariable String id, Model model) {

        //addAdminAccessToModel(model);

        /*getAuthorisedUser().ifPresent(user -> model.addAttribute("countUnreadMessages",
                messageService.getCountUnreadMessagesByUser(user)));*/

        Optional<SolarPowerPlant> solarPowerPlant = solarPowerPlantService.getSolarPowerPlantByStringId(id);

        if (solarPowerPlant.isPresent()) {
            model.addAttribute("solarPowerPlant", solarPowerPlant.get());

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
        } else model.addAttribute("solarPowerPlantChangeError", "Помилка, спробуйте пізніше.");
        return "dashboard/admin/solar-power-plant-by-id";
    }

    @GetMapping(path = "/admin/solar-power-plants/{id}/update")
    public String getSolarPowerPlantByIdForUpdate(@PathVariable String id, Model model) {
        //System.out.println("user:== " + usersService.getUserById(Long.valueOf(id)));
        //System.out.println("integer id: " + Long.valueOf(id));

        //addAdminAccessToModel(model);

        //Optional<User> authorisedUser=getAuthorisedUser();
        /*getAuthorisedUser().ifPresent(user -> model.addAttribute("countUnreadMessages",
                messageService.getCountUnreadMessagesByUser(user)));*/

        Optional<SolarPowerPlant> solarPowerPlant = solarPowerPlantService.getSolarPowerPlantByStringId(id);
        if (solarPowerPlant.isPresent()) {
            model.addAttribute("solarPowerPlant", solarPowerPlant.get());
            model.addAttribute("regions", Region.values());
            model.addAttribute("localDate", LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd")));
        } else model.addAttribute("solarPowerPlantChangeError", "Помилка, спробуйте пізніше.");

        return "dashboard/admin/update-solar-power-plant-by-id";
    }

    @PostMapping(path = "/admin/solar-power-plants/{id}/update")
    public String updateSolarPowerPlantById(@PathVariable String id,
                                            Model model,
                                            @RequestParam(value = "installationDate") String installationDate,
                                            RedirectAttributes redirectAttributes,
                                            @Valid SolarPowerPlant solarPowerPlant) throws ParseException {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String username = auth.getName();//get logged in username

        System.out.println("spp info: " + solarPowerPlant.getId() + " s_id: " + solarPowerPlant.getStringId());
        System.out.println("  - spp info: " + solarPowerPlant.getName() + " s_id: " + solarPowerPlant.getLocation().getRegion());
        System.out.println("  - spp info: " + installationDate + " s_id: " + solarPowerPlant.getStaticData().getPower());

        Optional<SolarPowerPlant> updatedSolarPowerPlant = solarPowerPlantService.getSolarPowerPlantById(solarPowerPlant.getId());
        if (updatedSolarPowerPlant.isPresent()) {
            updatedSolarPowerPlant.get().setName(solarPowerPlant.getName());


            updatedSolarPowerPlant.get().getLocation().setCountry("Україна");
            updatedSolarPowerPlant.get().getLocation().setRegion(solarPowerPlant.getLocation().getRegion());
            updatedSolarPowerPlant.get().getLocation().setCity(solarPowerPlant.getLocation().getCity());
            updatedSolarPowerPlant.get().getLocation().setStreet(solarPowerPlant.getLocation().getStreet());
            updatedSolarPowerPlant.get().getLocation().setNumber(solarPowerPlant.getLocation().getNumber());

            updatedSolarPowerPlant.get().getLocation().setLatitude(solarPowerPlant.getLocation().getLatitude());
            updatedSolarPowerPlant.get().getLocation().setLongitude(solarPowerPlant.getLocation().getLongitude());

            //updatedSolarPowerPlant.get().setLocation(solarPowerPlant.getLocation());

            //updatedSolarPowerPlant.get().setStaticData(solarPowerPlant.getStaticData());

            updatedSolarPowerPlant.get().getStaticData().setPower(solarPowerPlant.getStaticData().getPower());
            updatedSolarPowerPlant.get().getStaticData().setQuantity(solarPowerPlant.getStaticData().getQuantity());
            updatedSolarPowerPlant.get().getStaticData().setInstallationDate(installationDate);

            solarPowerPlantService.addSolarPowerPlant(updatedSolarPowerPlant.get(), 1);
        }


        //тут можна надіслати сповіщення для користувача
        redirectAttributes.addFlashAttribute("updateSolarPowerPlantMessage", "Інформацію про сонячну станцію оновлено.");
        model.addAttribute("solarPowerPlants", solarPowerPlantService.getAllSolarPowerPlants());
        return "redirect:/admin/solar-power-plants";
    }

    @DeleteMapping(path = "/admin/solar-power-plants/{id}/delete")
    public String deleteSolarPowerPlantById(@PathVariable String id, Model model, RedirectAttributes redirectAttributes) {
        //usersService.deleteUserById(Long.valueOf(id));

        Optional<SolarPowerPlant> solarPowerPlant = solarPowerPlantService.getSolarPowerPlantByStringId(id);

        authorizedUser = AuthorizationAccess.getAuthorisedUser(this.usersService);

        if (solarPowerPlant.isPresent() && authorizedUser.isPresent()) {
            solarPowerPlantService.deleteSolarPowerPlant(solarPowerPlant.get());

            //Тут можна надіслати ласта користувачу про видалення його аккаунта
            // TODO: 05.08.2021 Make sending a letter to the user about removing your account.

            redirectAttributes.addFlashAttribute("deleteSolarPowerPlantMessage", "Сонячну станцію видалено з системи.");
        } else {
            redirectAttributes.addFlashAttribute("deleteSolarPowerPlantMessage", "Сталась помилка, спробуйте пізніше.");
        }
        model.addAttribute("solarPowerPlants", solarPowerPlantService.getAllSolarPowerPlants());

        return "redirect:/admin/solar-power-plants";
    }

    /*Optional<User> getAuthorisedUser() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String username = auth.getName();//get logged in username
        return usersService.getUserByUsername(username);
    }*/

    /*private void addAdminAccessToModel(Model model) {
        Optional<User> user = getAuthorisedUser();

        if (user.isPresent() && user.get().getUserRole() == UserRoles.ROLE_ADMIN) {
            model.addAttribute("adminAccess", "admin");
            //System.out.println("admin access");
        }
    }*/

    private int getPage(String page, int maxPage) {
        int pageInt;
        try {
            pageInt = Integer.parseInt(page);
        } catch (NumberFormatException ex) {
            //System.err.println("Invalid string in argumment");
            pageInt = 1;
        }

        if (pageInt > maxPage) pageInt = 1;

        return pageInt;
    }

}
