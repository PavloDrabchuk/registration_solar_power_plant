package com.example.solar_power_plant.controllers;

import com.example.solar_power_plant.AuthorizationAccess;
import com.example.solar_power_plant.model.ConfirmationCode;
import com.example.solar_power_plant.model.PasswordRecoverInformation;
import com.example.solar_power_plant.enums.TypesConfirmationCode;
import com.example.solar_power_plant.model.User;
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
import java.util.Optional;

@Controller
public class RegistrationController {
    private final UsersService usersService;

    private final ConfirmationCodeService confirmationCodeService;
    //private final UserValidator userValidator;

    //@Autowired
    private final BCryptPasswordEncoder bCryptPasswordEncoder;

    //private final Optional<User> authorizedUser;
    private Optional<User> authorizedUser = Optional.empty();


    @Autowired
    public RegistrationController(UsersService usersService,
                                  ConfirmationCodeService confirmationCodeService,
                                  BCryptPasswordEncoder bCryptPasswordEncoder) {
        this.usersService = usersService;

        this.confirmationCodeService = confirmationCodeService;
        //this.userValidator = userValidator;
        this.bCryptPasswordEncoder = bCryptPasswordEncoder;

        //authorizedUser = AuthorizationAccess.getAuthorisedUser(this.usersService);
    }

    @GetMapping(path = "/login")
    public String redirectFromLoginPage(){
        authorizedUser = AuthorizationAccess.getAuthorisedUser(this.usersService);

        return (authorizedUser.isPresent()) ? "redirect:/home" : "login";
    }

    @GetMapping("/registration")
    public String newCustomerForm(Model model) {
        System.out.println("newCustomerForm");
        User user = new User();
        model.addAttribute("user", user);
        return "add-user";
    }

    @GetMapping(path = "/registration/success")
    public String redirectToNew() {
        return "redirect:/registration";
    }

    @PostMapping(path = "/registration/success")
    public String addUser(@Valid @ModelAttribute("user") User user, BindingResult bindingResult, Model model) {

        int countErrors = 0;

        if (bindingResult.hasErrors()) {
            System.out.println("BINDING RESULT ERROR");
            return "add-user";
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
                return "add-user";
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

            return "success-user-registration";
        }
    }

    @GetMapping(path = "/confirm_registration")
    public String confirmUserRegistration(Model model) {

        /*Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String username = auth.getName();
        Optional<User> user = usersService.getUserByUsername(username);*/

        authorizedUser = AuthorizationAccess.getAuthorisedUser(this.usersService);

        if (authorizedUser.isPresent() && authorizedUser.get().getLocked()) {
            return "redirect:/home";
        }

        authorizedUser.ifPresent(value -> model.addAttribute("email", value.getEmail()));

        System.out.println("confirmUserRegistration");
        return "confirm-registration";
    }

    @GetMapping(path = "/locked-account")
    public String gerLockedAccountView(Model model) {

        /*Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String username = auth.getName();
        Optional<User> user = usersService.getUserByUsername(username);*/

        authorizedUser = AuthorizationAccess.getAuthorisedUser(this.usersService);

        if (authorizedUser.isPresent() && !authorizedUser.get().getLocked()) {
            return "redirect:/confirm_registration";
        }

        //user.ifPresent(value -> model.addAttribute("email", value.getEmail()));

        System.out.println("locked-account");
        return "locked-account";
    }


    @GetMapping(path = "/confirm/{confirmationCode}")
    public String activateAccount(@PathVariable("confirmationCode") String confirmationCode,
                                  //@PathVariable("username") String username,
                                  Model model) {
        System.out.println("CCode: " + confirmationCode);
        //System.out.println("U: " + username);

        authorizedUser = AuthorizationAccess.getAuthorisedUser(this.usersService);

        /*authorizedUser.ifPresent(user -> model.addAttribute("countUnreadMessages",
                messageService.getCountUnreadMessagesByUser(user)));*/

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
        return "success-user-registration-confirmed";
    }

    @PostMapping(path = "/sendConfirmationCodeAgain")
    public String sendConfirmationCodeAgain(Model model) {
        System.out.println("sendConfirmationCodeAgain");

        String sendingCodeMessage;

        /*Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String username = auth.getName();
        Optional<User> user = usersService.getUserByUsername(username);*/

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


        return "confirm-registration";
    }

    @GetMapping(path = "/recover-password")
    public String recoverPasswordRequest(Model model) {

        //authorizedUser = AuthorizationAccess.getAuthorisedUser(this.usersService);

        /*authorizedUser.ifPresent(user -> model.addAttribute("countUnreadMessages",
                messageService.getCountUnreadMessagesByUser(user)));*/

        PasswordRecoverInformation recoverInformation = new PasswordRecoverInformation();

        model.addAttribute("recoverInformation", recoverInformation);
        System.out.println("recoverPassword");
        return "recover-password";
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
        return "recover-password";
    }

    @GetMapping(path = "/recover/{confirmationCode}")
    public String recoverPassword(@PathVariable("confirmationCode") String confirmationCode,
                                  //@PathVariable("username") String username,
                                  Model model) {
        System.out.println("CCode: " + confirmationCode);

        //authorizedUser = AuthorizationAccess.getAuthorisedUser(this.usersService);

        /*authorizedUser.ifPresent(user -> model.addAttribute("countUnreadMessages",
                messageService.getCountUnreadMessagesByUser(user)));*/

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
        return "recover-password";
    }

    @PostMapping(path = "/recover/{confirmationCode}")
    public String updatePassword(@Valid @ModelAttribute("updatePassword") ArrayList<String> updatePassword,
                                 @PathVariable("confirmationCode") String confirmationCode,
                                 @RequestParam("password") String password,
                                 @RequestParam("passwordAgain") String passwordAgain,
                                 Model model) {
        Optional<ConfirmationCode> confirmationCodeResult = confirmationCodeService.findConfirmationCodeByConfirmationCode(confirmationCode);

        /*if (confirmationCodeResult.isPresent()) {
            User user = confirmationCodeResult.get().getUser();
        }*/

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


        return "recover-password";
    }
}
