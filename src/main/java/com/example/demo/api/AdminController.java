package com.example.demo.api;

import com.example.demo.model.User;
import com.example.demo.model.UserRoles;
import com.example.demo.service.UsersService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Optional;

@Controller
public class AdminController {
    private final UsersService usersService;

    @Autowired
    public AdminController(UsersService usersService) {
        this.usersService = usersService;
    }

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
    public String getUsersPage(Model model) {
        model.addAttribute("usersMessage", "Users :)");

        if (getAuthorisedUser().isPresent()) {
            model.addAttribute("users", usersService.getAllUsers());
        }

        return "dashboard/admin/users";
    }

    @GetMapping(path = "/admin/users/{id}")
    public String getUserById(@PathVariable String id, Model model) {
        Optional<User> user=usersService.getUserById(Long.valueOf(id));
        if(user.isPresent()) {
            model.addAttribute("user", user.get());
        } else model.addAttribute("userChangeError", "Помилка, спробуйте пізніше.");
        return "dashboard/admin/user-by-id";
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
                                 @RequestParam(value = "username", defaultValue = "username") String username,
                                 @RequestParam(value = "email", defaultValue = "example@example.com") String email) {
        User user = new User();
        user.setUsername(username);
        user.setEmail(email);
        usersService.saveUser(user);

        //тут можна надіслати сповіщення для користувача
        model.addAttribute("updateUserMessage", "Інформацію про користувача оновлено");
        return "dashboard/admin/users";
    }

    @GetMapping(path = "/admin/solar-power-plants")
    public String getSolarPowerPlantsPage(Model model) {
        model.addAttribute("solarPowerPlants", "Solar power plants :)");
        return "dashboard/admin/solar-power-plants";
    }

    Optional<User> getAuthorisedUser() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String username = auth.getName();//get logged in username
        return usersService.getUserByUsername(username);
    }

}
