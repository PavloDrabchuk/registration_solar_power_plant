package com.example.solar_power_plant.controllers;

import com.example.solar_power_plant.AuthorizationAccess;
import com.example.solar_power_plant.model.*;
import com.example.solar_power_plant.service.*;
import org.springframework.beans.factory.annotation.Autowired;
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
public class UserController {

    private final UsersService usersService;
    private final SolarPowerPlantService solarPowerPlantService;

    private final ConfirmationCodeService confirmationCodeService;
    private final LocationService locationService;
    private final MessageService messageService;
    //private final UserValidator userValidator;

    //@Autowired
    private final BCryptPasswordEncoder bCryptPasswordEncoder;

    //private final Optional<User> authorizedUser;
    private Optional<User> authorizedUser = Optional.empty();


    @Autowired
    public UserController(UsersService usersService,
                          SolarPowerPlantService solarPowerPlantService,

                          ConfirmationCodeService confirmationCodeService,

                          LocationService locationService,
                          MessageService messageService,
                          BCryptPasswordEncoder bCryptPasswordEncoder) {
        this.usersService = usersService;
        this.solarPowerPlantService = solarPowerPlantService;

        this.confirmationCodeService = confirmationCodeService;
        //this.userValidator = userValidator;
        this.locationService = locationService;
        this.messageService = messageService;

        this.bCryptPasswordEncoder=bCryptPasswordEncoder;

        //authorizedUser = AuthorizationAccess.getAuthorisedUser(this.usersService);
    }


    @GetMapping
    public String redirectToAccessPages(Model model) {
        //Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        //String username = auth.getName();//get logged in username

        authorizedUser = AuthorizationAccess.getAuthorisedUser(this.usersService);

        /*authorizedUser.ifPresent(user -> model.addAttribute("countUnreadMessages",
                messageService.getCountUnreadMessagesByUser(user)));*/

        return (authorizedUser.isPresent()) ? "redirect:/home" : "index";

        //return ("username".equals("anonymousUser")) ? "index" : "redirect:/home";
    }


    @GetMapping(path = "/home")
    public String getSolarPowerPlantsByUsername(Model model, @RequestParam(value = "page", defaultValue = "1") String page) {

        System.out.println("getSolarPowerPlantsByUsername, page: " + page);

        authorizedUser = AuthorizationAccess.getAuthorisedUser(this.usersService);

        /*authorizedUser.ifPresent(user -> model.addAttribute("countUnreadMessages",
                messageService.getCountUnreadMessagesByUser(user)));*/

        /*Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String username = auth.getName();//get logged in username
        Optional<User> user = usersService.getUserByUsername(username);*/


        if (authorizedUser.isPresent() && authorizedUser.get().getLocked()) {
            return "redirect:/locked-account";
        }

        if (authorizedUser.isPresent() && authorizedUser.get().getActivated()) {
            double limitSolarPowerPlant = 4;

            int pageInt = AuthorizationAccess.getPage(page, solarPowerPlantService.getNumPagesList(authorizedUser.get(), limitSolarPowerPlant).size());

            System.out.println("status:" + authorizedUser.get().getActivated());

            //String userRole = authorizedUser.get().getUserRole().toString();

            model.addAttribute("solarPowerPlantsByUser",
                    solarPowerPlantService.getSolarPowerPlantByUserForPage(
                            authorizedUser.get().getId(),
                            (pageInt - 1) * (int) limitSolarPowerPlant,
                            (int) limitSolarPowerPlant));

            model.addAttribute("name", authorizedUser.get().getUsername());

            //List<String> pageNumList = solarPowerPlantService.getNumPagesList(authorizedUser.get(), limitSolarPowerPlant);

            //int pageNumList = (int) Math.ceil(solarPowerPlantService.getSolarPowerPlantsByUser(authorizedUser.get()).size() / limitSolarPowerPlant);
            int pageNumList = AuthorizationAccess.getNumPagesList(solarPowerPlantService.getSolarPowerPlantsByUser(authorizedUser.get()),limitSolarPowerPlant);

            model.addAttribute("numPages", pageNumList);
            model.addAttribute("currentPage", pageInt);

            //model.addAttribute("countUnreadMessages",messageService.getCountUnreadMessagesByUser(user.get()));

            /*if (userRole.equals("ROLE_ADMIN")) {
                model.addAttribute("adminAccess", "admin");
                System.out.println("admin access");
            }*/

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

    /*@GetMapping(path = "/registration/success")
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

            //user.setUserRole(UserRoles.ROLE_USER);
            //user.setActivated(false);
            //user.setLocked(false);
            user.setPassword(bCryptPasswordEncoder.encode(user.getPassword()));

            //user.setDateTimeOfCreation(LocalDateTime.now());

            System.out.println("activated: " + user.getActivated());
            usersService.saveUser(user);
            System.out.println("time: " + LocalDateTime.now());


            confirmationCodeService.sendConfirmationCode(user, TypesConfirmationCode.ConfirmRegistration);

            System.out.println("--- --- ---");
            model.addAttribute("email", user.getEmail());

            return "success_user_registration";
        }
    }*/

    /*@GetMapping(path = "/confirm_registration")
    public String confirmUserRegistration(Model model) {

        *//*Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String username = auth.getName();
        Optional<User> user = usersService.getUserByUsername(username);*//*

        authorizedUser = AuthorizationAccess.getAuthorisedUser(this.usersService);

        if (authorizedUser.isPresent() && authorizedUser.get().getLocked()) {
            return "redirect:/home";
        }

        authorizedUser.ifPresent(value -> model.addAttribute("email", value.getEmail()));

        System.out.println("confirmUserRegistration");
        return "confirm_registration";
    }

    @GetMapping(path = "/locked-account")
    public String gerLockedAccountView(Model model) {

        *//*Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String username = auth.getName();
        Optional<User> user = usersService.getUserByUsername(username);*//*

        authorizedUser = AuthorizationAccess.getAuthorisedUser(this.usersService);

        if (authorizedUser.isPresent() && !authorizedUser.get().getLocked()) {
            return "redirect:/confirm_registration";
        }

        //user.ifPresent(value -> model.addAttribute("email", value.getEmail()));

        System.out.println("locked-account");
        return "locked-account";
    }

    @GetMapping("/registration")
    public String newCustomerForm(Model model) {
        System.out.println("newCustomerForm");
        User user = new User();
        model.addAttribute("user", user);
        return "add_user";
    }*/


    /*@GetMapping(path = "/confirm/{confirmationCode}")
    public String activateAccount(@PathVariable("confirmationCode") String confirmationCode,
                                  //@PathVariable("username") String username,
                                  Model model) {
        System.out.println("CCode: " + confirmationCode);
        //System.out.println("U: " + username);

        authorizedUser = AuthorizationAccess.getAuthorisedUser(this.usersService);

        *//*authorizedUser.ifPresent(user -> model.addAttribute("countUnreadMessages",
                messageService.getCountUnreadMessagesByUser(user)));*//*

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

        *//*Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String username = auth.getName();
        Optional<User> user = usersService.getUserByUsername(username);*//*

        authorizedUser = AuthorizationAccess.getAuthorisedUser(this.usersService);

        if (authorizedUser.isPresent()) {

            confirmationCodeService.deactivateConfirmationCodesByUser(authorizedUser.get());

            confirmationCodeService.sendConfirmationCode(authorizedUser.get(), TypesConfirmationCode.ConfirmRegistration);

            sendingCodeMessage = "Посилання успішно відправлено ще раз на вказаний e-mail: " + authorizedUser.get().getEmail() + ".";
            model.addAttribute("email", authorizedUser.get().getEmail());
        } else {
            sendingCodeMessage = "Посилання не надіслано, спробуйте пізніше ще раз.";
        }
        model.addAttribute("sendingCodeMessage", sendingCodeMessage);


        return "confirm_registration";
    }*/

    @GetMapping(path = "/profile")
    public String goToProfilePage(Model model) {
        System.out.println("goToProfilePage");

        authorizedUser = AuthorizationAccess.getAuthorisedUser(this.usersService);

        /*authorizedUser.ifPresent(user -> model.addAttribute("countUnreadMessages",
                messageService.getCountUnreadMessagesByUser(user)));*/

        /*Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String username = auth.getName();//get logged in username
        Optional<User> user = usersService.getUserByUsername(username);*/

        if (authorizedUser.isPresent()) {
            model.addAttribute("userInformation", authorizedUser.get());
            System.out.println("time: " + authorizedUser.get().getDateTimeOfCreation());

            authorizedUser.get().getStringInfo();
            model.addAttribute("countOfRegisteredSolarStations", solarPowerPlantService.getCountSolarPowerPlantByUser(authorizedUser.get()));

            if (authorizedUser.get().getLocked()) {
                model.addAttribute("accountStatus", "Заблокований");
            } else {
                Boolean accountStatus = authorizedUser.get().getActivated();
                model.addAttribute("accountStatus", accountStatus ? "Активований" : "Не активований");
            }
            /*if (authorizedUser.get().getUserRole() == UserRoles.ROLE_ADMIN) {
                model.addAttribute("adminAccess", "admin");
                System.out.println("admin access");
            }*/
            //model.addAttribute("countUnreadMessages",messageService.getCountUnreadMessagesByUser(user.get()));

        }
        return "profile";
    }

    @GetMapping(path = "/profile/edit")
    public String editProfileInfo(Model model) {
        System.out.println("editProfileInfo");

        /*Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String username = auth.getName();//get logged in username
        Optional<User> user = usersService.getUserByUsername(username);*/

        authorizedUser = AuthorizationAccess.getAuthorisedUser(this.usersService);

        /*authorizedUser.ifPresent(user -> model.addAttribute("countUnreadMessages",
                messageService.getCountUnreadMessagesByUser(user)));*/

        //Optional<User> user = getAuthorisedUser();

        if (authorizedUser.isPresent()) {
            model.addAttribute("userInformation", authorizedUser.get());

            Boolean accountStatus = authorizedUser.get().getActivated();
            model.addAttribute("accountStatus", accountStatus ? "Активований" : "Не активовний");

            /*if (authorizedUser.get().getUserRole() == UserRoles.ROLE_ADMIN) {
                model.addAttribute("adminAccess", "admin");
                System.out.println("admin access");
            }*/

            //model.addAttribute("countUnreadMessages",messageService.getCountUnreadMessagesByUser(user.get()));

        }
        return "edit-profile";
    }

    @PutMapping(path = "/profile/update")
    public String updateProfileInfo(@Valid @ModelAttribute("userInformation") User updatedUserInfo, BindingResult bindingResult) {

        System.out.println("updateProfileInfo");

        if (bindingResult.hasErrors()) {
            System.out.println("BINDING RESULT ERROR");
            return "edit-profile";
        } else {

            /*Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            String username = auth.getName();//get logged in username
            Optional<User> user = usersService.getUserByUsername(username);*/

            authorizedUser = AuthorizationAccess.getAuthorisedUser(this.usersService);

            authorizedUser.ifPresent(value -> usersService.updateUserInformation(value, updatedUserInfo));

            return "redirect:/profile";
        }
    }

    /*@GetMapping(path = "/recover-password")
    public String recoverPasswordRequest(Model model) {

        //authorizedUser = AuthorizationAccess.getAuthorisedUser(this.usersService);

        *//*authorizedUser.ifPresent(user -> model.addAttribute("countUnreadMessages",
                messageService.getCountUnreadMessagesByUser(user)));*//*

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

        //authorizedUser = AuthorizationAccess.getAuthorisedUser(this.usersService);

        *//*authorizedUser.ifPresent(user -> model.addAttribute("countUnreadMessages",
                messageService.getCountUnreadMessagesByUser(user)));*//*

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

        *//*if (confirmationCodeResult.isPresent()) {
            User user = confirmationCodeResult.get().getUser();
        }*//*

        System.out.println("Confirmation code: " + confirmationCode);
        System.out.println("password: " + password);
        System.out.println("passwordAgain: " + passwordAgain);

        if (password.equals(passwordAgain) && confirmationCodeResult.isPresent()) {
            User user = confirmationCodeResult.get().getUser();
            user.setPassword(bCryptPasswordEncoder.encode(password));
            usersService.saveUser(user);

            //System.out.println("password are equals");
            model.addAttribute("updatePasswordOK", "Пароль успішно змінено.");
        } else {
            model.addAttribute("updatePasswordERROR", "Паролі не співпадають.");
        }

        System.out.println("************************");


        return "recover_password";
    }*/

    /*Optional<User> getAuthorisedUser() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String username = auth.getName();//get logged in username
        return usersService.getUserByUsername(username);
    }*/

    /*private int getPage(String page, int maxPage) {
        int pageInt;
        try {
            pageInt = Integer.parseInt(page);
        } catch (NumberFormatException ex) {
            //System.err.println("Invalid string in argument");
            pageInt = 1;
        }

        if (pageInt > maxPage) pageInt = 1;

        return pageInt;
    }*/

   /* @ModelAttribute("version")
    public String getVersion() {
        return "versionService.getVersion()";
    }*/


}
