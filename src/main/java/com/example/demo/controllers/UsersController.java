package com.example.demo.controllers;

import com.example.demo.model.*;
import com.example.demo.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Controller
public class UsersController {

    private final UsersService usersService;
    private final SolarPowerPlantService solarPowerPlantService;

    private final ConfirmationCodeService confirmationCodeService;
    private final LocationService locationService;
    //private final UserValidator userValidator;

    @Autowired
    private BCryptPasswordEncoder bCryptPasswordEncoder;

    @Autowired
    public UsersController(UsersService usersService,
                           SolarPowerPlantService solarPowerPlantService,

                           ConfirmationCodeService confirmationCodeService,

                           LocationService locationService) {
        this.usersService = usersService;
        this.solarPowerPlantService = solarPowerPlantService;

        this.confirmationCodeService = confirmationCodeService;
        //this.userValidator = userValidator;
        this.locationService = locationService;
    }


    @GetMapping
    public String redirectToAccessPages() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String username = auth.getName();//get logged in username

        return (username.equals("anonymousUser")) ? "index" : "redirect:/home";
    }


    @GetMapping(path = "/home")
    public String getSolarPowerPlantsByUsername(Model model, @RequestParam(value = "page", defaultValue = "1") String page) {

        System.out.println("getSolarPowerPlantsByUsername, page: " + page);

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String username = auth.getName();//get logged in username
        Optional<User> user = usersService.getUserByUsername(username);

        if (user.isPresent() && user.get().getActivated()) {
            double limitSolarPowerPlant = 4;

            System.out.println("status:" + user.get().getActivated());

            String userRole = user.get().getUserRole().toString();

            model.addAttribute("solarPowerPlantsByUser",
                    solarPowerPlantService.getSolarPowerPlantByUserForPage(
                            user.get().getId(),
                            (Integer.parseInt(page) - 1) * (int) limitSolarPowerPlant,
                            (int) limitSolarPowerPlant));

            model.addAttribute("name", username);

            List<String> pageNumList = solarPowerPlantService.getNumPagesList(user.get(), limitSolarPowerPlant);

            model.addAttribute("numPages", pageNumList);
            model.addAttribute("currentPage",page);

            if (userRole.equals("ADMIN")) {
                model.addAttribute("adminAccess", "admin");
                System.out.println("admin access");
            }

            return "home";
        } else {
            return "redirect:/confirm_registration";
        }
    }

    /*@GetMapping(path = "/id={id}")
    public User getUserById(@PathVariable("id") Integer id) {
        System.out.println("getUserById");
        return usersService.getUserById(id).orElse(null);
    }*/

    @GetMapping(path = "/registration/success")
    public String redirectToNew() {
        return "redirect:/registration";
    }

    @PostMapping(path = "/registration/success")
    public String addUser(@Valid @ModelAttribute("user") User user, BindingResult bindingResult, Model model) {

        int countErrors = 0;

        if (bindingResult.hasErrors()) {
            System.out.println("BINDING RESULT ERROR");
            return "add_user";
        } else {


            if (usersService.getUserByUsername(user.getUsername()).isPresent()) {
                countErrors++;
                model.addAttribute("duplicateUser", "Користувач з таким іменем уже існує.");
            }

            if (usersService.getUserByEmail(user.getEmail()).isPresent()) {
                countErrors++;
                model.addAttribute("duplicateEmail", "Введена електронна адреса вже використовується.");
            }

            if (countErrors != 0) {
                return "add_user";
            }


            System.out.println("addUser");

            user.setUserRole(UserRoles.USER);
            user.setActivated(false);
            user.setLocked(false);
            user.setPassword(bCryptPasswordEncoder.encode(user.getPassword()));
            user.setDateTimeOfCreation(LocalDateTime.now());
            System.out.println("activated: " + user.getActivated());
            usersService.saveUser(user);
            System.out.println("time: " + LocalDateTime.now());


            confirmationCodeService.sendConfirmationCode(user, TypesConfirmationCode.ConfirmRegistration);

            System.out.println("--- --- ---");
            model.addAttribute("email", user.getEmail());

            return "success_user_registration";
        }
    }

    @GetMapping(path = "/confirm_registration")
    public String confirmUserRegistration(Model model) {

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String username = auth.getName();
        Optional<User> user = usersService.getUserByUsername(username);


        user.ifPresent(value -> model.addAttribute("email", value.getEmail()));

        System.out.println("confirmUserRegistration");
        return "confirm_registration";
    }


    @GetMapping("/registration")
    public String newCustomerForm(Model model) {
        System.out.println("newCustomerForm");
        User user = new User();
        model.addAttribute("user", user);
        return "add_user";
    }


    @GetMapping(path = "/confirm/{confirmationCode}")
    public String activateAccount(@PathVariable("confirmationCode") String confirmationCode,
                                  //@PathVariable("username") String username,
                                  Model model) {
        System.out.println("CCode: " + confirmationCode);
        //System.out.println("U: " + username);

        Optional<ConfirmationCode> confirmationResult = confirmationCodeService.findConfirmationCodeByConfirmationCode(confirmationCode);


        if (confirmationResult.isPresent() && confirmationResult.get().getValid()) {
            confirmationResult.get().setValid(false);
            confirmationCodeService.saveConfirmationCode(confirmationResult.get());
            //User user = usersService.getUserByUsername(username);
            User user = confirmationResult.get().getUser();
            user.setActivated(true);
            usersService.saveUser(user);
            model.addAttribute("okMessage", "Реєстрацію аккаунту підтверджено.");
        } else {
            model.addAttribute("errorMessage", "Код підтвердження недійсний.");
        }
        return "success_user_registration_confirmed";
    }

    @PostMapping(path = "/sendConfirmationCodeAgain")
    public String sendConfirmationCodeAgain(Model model) {
        System.out.println("sendConfirmationCodeAgain");

        String sendingCodeMessage;

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String username = auth.getName();
        Optional<User> user = usersService.getUserByUsername(username);

        if (user.isPresent()) {

            confirmationCodeService.deactivateConfirmationCodesByUser(user.get());

            confirmationCodeService.sendConfirmationCode(user.get(), TypesConfirmationCode.ConfirmRegistration);

            sendingCodeMessage = "Посилання успішно відправлено ще раз на вказаний e-mail: " + user.get().getEmail()+".";
            model.addAttribute("email",user.get().getEmail());
        } else {
            sendingCodeMessage = "Посилання не надіслано, спробуйте пізніше ще раз.";
        }
        model.addAttribute("sendingCodeMessage", sendingCodeMessage);


        return "confirm_registration";
    }

    @GetMapping(path = "/profile")
    public String goToProfilePage(Model model) {
        System.out.println("goToProfilePage");

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String username = auth.getName();//get logged in username
        Optional<User> user = usersService.getUserByUsername(username);
        if (user.isPresent()) {
            model.addAttribute("userInformation", user.get());
            System.out.println("time: " + user.get().getDateTimeOfCreation());

            user.get().getStringInfo();
            model.addAttribute("countOfRegisteredSolarStations",solarPowerPlantService.getCountSolarPowerPlantByUser(user.get()));

            Boolean accountStatus = user.get().getActivated();
            model.addAttribute("accountStatus", accountStatus ? "Активований" : "Не активований");

            if (user.get().getUserRole()==UserRoles.ADMIN) {
                model.addAttribute("adminAccess", "admin");
                System.out.println("admin access");
            }

        }
        return "profile";
    }

    @GetMapping(path = "/profile/edit")
    public String editProfileInfo(Model model) {
        System.out.println("editProfileInfo");

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String username = auth.getName();//get logged in username
        Optional<User> user = usersService.getUserByUsername(username);

        if (user.isPresent()) {
            model.addAttribute("userInformation", user.get());

            Boolean accountStatus = user.get().getActivated();
            model.addAttribute("accountStatus", accountStatus ? "Активований" : "Не активовний");

            if (user.get().getUserRole()==UserRoles.ADMIN) {
                model.addAttribute("adminAccess", "admin");
                System.out.println("admin access");
            }

        }
        return "edit-profile";
    }

    @PostMapping(path = "/profile/update")
    public String updateProfileInfo(@Valid @ModelAttribute("userInformation") User updatedUserInfo, BindingResult bindingResult) {

        System.out.println("updateProfileInfo");

        if (bindingResult.hasErrors()) {
            System.out.println("BINDING RESULT ERROR");
            return "edit-profile";
        } else {

            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            String username = auth.getName();//get logged in username
            Optional<User> user = usersService.getUserByUsername(username);


            usersService.updateUserInformation(user.get(), updatedUserInfo);


            return "redirect:/profile";
        }
    }

    @GetMapping(path = "/recover_password")
    public String recoverPasswordRequest(Model model) {

        PasswordRecoverInformation recoverInformation = new PasswordRecoverInformation();

        model.addAttribute("recoverInformation", recoverInformation);
        System.out.println("recoverPassword");
        return "recover_password";
    }

    @GetMapping(path = "recoverPassword")
    public String redirectToRecoverPasswordPage() {
        return "redirect:/recover_password";
    }

    @PostMapping(path = "/recoverPassword")
    public String sendResetPasswordMail(@Valid @ModelAttribute("recoverInformation") PasswordRecoverInformation recoverInformation, Model model) {


        Optional<User> user = usersService.getUserByUsername(recoverInformation.getUsername());
        if (user.isPresent() && user.get().getEmail().equals(recoverInformation.getEmail())) {

            confirmationCodeService.sendConfirmationCode(user.get(), TypesConfirmationCode.RecoverPassword);
            model.addAttribute("sendMessageOK", "Повідомлення для відновлення паролю надіслано на Ваш e-mail: " + recoverInformation.getEmail() + ".");
        } else {
            model.addAttribute("sendMessageERROR", "Повідомлення не надіслано. Перевірте правильність введених даних.");
        }
        return "recover_password";
    }

    @GetMapping(path = "/recover/{confirmationCode}")
    public String recoverPassword(@PathVariable("confirmationCode") String confirmationCode,
                                  //@PathVariable("username") String username,
                                  Model model) {
        System.out.println("CCode: " + confirmationCode);

        Optional<ConfirmationCode> confirmationResult = confirmationCodeService.findConfirmationCodeByConfirmationCode(confirmationCode);

        if (confirmationResult.isPresent() && confirmationResult.get().getValid()) {
            confirmationResult.get().setValid(false);
            confirmationCodeService.saveConfirmationCode(confirmationResult.get());

            model.addAttribute("recoverSignalOK", "Введіть новий пароль.");
            model.addAttribute("username", confirmationResult.get().getUser().getUsername());
            model.addAttribute("confirmationCode", confirmationCode);

        } else {
            model.addAttribute("errorMessage", "Код підтвердження недійсний.");
        }
        return "recover_password";
    }

    @PostMapping(path = "/recover/{confirmationCode}")
    public String updatePassword(@Valid @ModelAttribute("updatePassword") ArrayList<String> updatePassword,
                                 @PathVariable("confirmationCode") String confirmationCode,
                                 @RequestParam("password") String password,
                                 @RequestParam("passwordAgain") String passwordAgain,
                                 Model model) {
        Optional<ConfirmationCode> confirmationCodeResult = confirmationCodeService.findConfirmationCodeByConfirmationCode(confirmationCode);
        User user = confirmationCodeResult.get().getUser();

        System.out.println("Confirmation code: " + confirmationCode);
        System.out.println("password: " + password);
        System.out.println("passwordAgain: " + passwordAgain);

        if (password.equals(passwordAgain)) {
            user.setPassword(bCryptPasswordEncoder.encode(password));
            usersService.saveUser(user);
            System.out.println("password are equals");
            model.addAttribute("updatePasswordOK", "Пароль успішно змінено.");
        } else {
            model.addAttribute("updatePasswordERROR", "Паролі не співпадають.");
        }

        System.out.println("************************");


        return "recover_password";
    }

}
