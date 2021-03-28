package com.example.demo.api;

import com.example.demo.model.SolarPowerPlant;
import com.example.demo.model.User;
import com.example.demo.model.UserRoles;
import com.example.demo.service.SolarPowerPlantService;
import com.example.demo.service.UsersService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Controller
public class AdminController {
    private final UsersService usersService;
    private final SolarPowerPlantService solarPowerPlantService;

    @Autowired
    public AdminController(UsersService usersService, SolarPowerPlantService solarPowerPlantService, BCryptPasswordEncoder bCryptPasswordEncoder) {
        this.usersService = usersService;
        this.solarPowerPlantService = solarPowerPlantService;
        this.bCryptPasswordEncoder = bCryptPasswordEncoder;
    }

    private final BCryptPasswordEncoder bCryptPasswordEncoder;

    @GetMapping(path = "/admin")
    public String getAllUsers(Model model) {
        System.out.println("getAllUsers");

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String username = auth.getName();//get logged in username
        Optional<User> user = usersService.getUserByUsername(username);

        if (user.get().getUserRoles().toString().equals(UserRoles.ADMIN.name())) {
            model.addAttribute("users", usersService.getAllUsers());
            System.out.println("--- ADMIN ---\n role: " + user.get().getUserRoles().toString().equals(UserRoles.ADMIN.name()));
            return "admin_page";
        } else {
            System.out.println("--- HOME ---");
            return "home";
        }
    }

    @GetMapping(path = "/admin/users")
    public String getUsersPage(@RequestParam(value = "page", defaultValue = "1") String page,
                               @RequestParam(value = "search", required = false) String searchUsername,
                               Model model) {
        model.addAttribute("usersMessage", "Users :)");

        if (getAuthorisedUser().isPresent()) {
            double limitUsers = 4;

            if (searchUsername == null) {

                model.addAttribute("users",
                        usersService.getUsersForPage(
                                (Integer.parseInt(page) - 1) * (int) limitUsers,
                                (int) limitUsers));

                List<String> pageNumList = usersService.getNumPagesList(usersService.getAllUsers(), limitUsers);

                model.addAttribute("numPages", pageNumList);
                model.addAttribute("currentPage", page);

            } else {
                List<User> users = usersService.getUsersByUsername(searchUsername);
                if (users.size() > 0) {

                    model.addAttribute("users",
                            usersService.getUsersByUsernameForPage(
                                    searchUsername,
                                    (Integer.parseInt(page) - 1) * (int) limitUsers,
                                    (int) limitUsers));

                    List<String> pageNumList = usersService.getNumPagesList(users, limitUsers);

                    model.addAttribute("numPages", pageNumList);
                    model.addAttribute("currentPage", page);
                    model.addAttribute("search", searchUsername);
                } else {
                    model.addAttribute("usersNotFoundMessage", "За Вашим запитом користувачів не знайдено.");
                }
            }
        }

        return "dashboard/admin/users";
    }

    @GetMapping(path = "/admin/users/{id}")
    public String getUserById(@PathVariable String id, Model model) {
        Optional<User> user = usersService.getUserById(Long.valueOf(id));
        if (user.isPresent()) {
            model.addAttribute("user", user.get());
        } else model.addAttribute("userChangeError", "Помилка, спробуйте пізніше.");
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
                                  @RequestParam(value = "role", defaultValue = "USER") String role,
                                  Model model,
                                  RedirectAttributes redirectAttributes) {
        Optional<User> user = usersService.getUserById(Long.valueOf(id));
        if (user.isPresent()) {
            System.out.println("---((((((((((((((((: role: " + role);
            //user.get().setUserRoles(UserRoles.model);
            switch (role) {
                case "USER": {
                    user.get().setUserRoles(UserRoles.USER);
                    break;
                }
                case "ADMIN": {
                    user.get().setUserRoles(UserRoles.ADMIN);
                    break;
                }
                case "EDITOR": {
                    user.get().setUserRoles(UserRoles.EDITOR);
                    break;
                }
                default: {
                    model.addAttribute("errorSetRoleMessage", "Помилка запиту, спробуйте пізніше");
                    break;
                }
            }
            usersService.saveUser(user.get());
            redirectAttributes.addFlashAttribute("updateUserMessage", "Роль користувача змінено.");
        } else {
            System.out.println("((((((((((((((((");
            redirectAttributes.addFlashAttribute("errorSetRoleMessage", "Помилка запиту, спробуйте пізніше");
        }

        return "redirect:/admin/users/" + id;
    }

    @GetMapping(path = "/admin/users/{id}/update")
    public String getUserByIdForUpdate(@PathVariable String id, Model model) {
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

    @DeleteMapping(path = "/admin/users/{id}/delete")
    public String deleteUserById(@PathVariable String id, Model model, RedirectAttributes redirectAttributes) {
        //usersService.deleteUserById(Long.valueOf(id));

        Optional<User> user = usersService.getUserById(Long.valueOf(id));
        if (user.isPresent() && getAuthorisedUser().isPresent() && user.get() != getAuthorisedUser().get()) {
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

        if (getAuthorisedUser().isPresent()) {
            double limitSolarPowerPlants = 4;

            if (searchName == null) {

                model.addAttribute("solarPowerPlants",
                        solarPowerPlantService.getAllSolarPowerPlantByUserForPage(
                                (Integer.parseInt(page) - 1) * (int) limitSolarPowerPlants,
                                (int) limitSolarPowerPlants));

                List<String> pageNumList = solarPowerPlantService.getNumPagesListForAll(
                        solarPowerPlantService.getAllSolarPowerPlants(), limitSolarPowerPlants);

                model.addAttribute("numPages", pageNumList);
                model.addAttribute("currentPage", page);

            } else {
                List<SolarPowerPlant> solarPowerPlants = solarPowerPlantService.getSolarPowerPlantsByName(searchName);

                for (SolarPowerPlant s:solarPowerPlants) {
                    System.out.println("   s: "+s.getName());
                }

                if (solarPowerPlants.size() > 0) {

                    model.addAttribute("solarPowerPlants",
                            solarPowerPlantService.getSolarPowerPlantsByNameForPage(
                                    searchName,
                                    (Integer.parseInt(page) - 1) * (int) limitSolarPowerPlants,
                                    (int) limitSolarPowerPlants));

                    List<String> pageNumList = solarPowerPlantService.getNumPagesListForAll(solarPowerPlants, limitSolarPowerPlants);

                    for (String p:pageNumList){
                        System.out.println("    p: "+p);
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
        Optional<SolarPowerPlant> solarPowerPlant = solarPowerPlantService.getSolarPowerPlantByStringId(id);
        if (solarPowerPlant.isPresent()) {
            model.addAttribute("solarPowerPlant", solarPowerPlant.get());
        } else model.addAttribute("solarPowerPlantChangeError", "Помилка, спробуйте пізніше.");
        return "dashboard/admin/solar-power-plant-by-id";
    }

    Optional<User> getAuthorisedUser() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String username = auth.getName();//get logged in username
        return usersService.getUserByUsername(username);
    }


}
