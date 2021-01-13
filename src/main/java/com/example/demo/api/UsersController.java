package com.example.demo.api;

import com.example.demo.model.*;
import com.example.demo.service.ConfirmationCodeService;
import com.example.demo.service.SolarPowerPlantService;
import com.example.demo.service.UserRoleService;
import com.example.demo.service.UsersService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.ArrayList;
import java.util.Optional;

@Controller
public class UsersController {

    private final UsersService usersService;
    private final SolarPowerPlantService solarPowerPlantService;
    private final UserRoleService userRoleService;
    private final ConfirmationCodeService confirmationCodeService;

    @Autowired
    private BCryptPasswordEncoder bCryptPasswordEncoder;

    @Autowired
    public UsersController(UsersService usersService,
                           SolarPowerPlantService solarPowerPlantService,
                           UserRoleService userRoleService,
                           ConfirmationCodeService confirmationCodeService) {
        this.usersService = usersService;
        this.solarPowerPlantService = solarPowerPlantService;
        this.userRoleService = userRoleService;
        this.confirmationCodeService = confirmationCodeService;
    }


    /*@GetMapping
    public ModelAndView getAllUsers() {
        //Model model=new Model("getall");

//        List<User> userList=new ArrayList<>();
//        userList.add(new User(1,"Name1","Surname1"));
//        userList.add(new User(2,"Name2","Surname2"));

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String username = auth.getName();//get logged in username
        User user = usersService.getUserByUsername(username);


        ModelAndView modelAndView = new ModelAndView("index");
        modelAndView.addObject("users", usersService.getAllUsers());
        modelAndView.addObject("solarPowerPlants",solarPowerPlantService.getAllSolarPowerPlants());
        modelAndView.addObject("name",username);
        //return usersService.getAllUsers();
        return modelAndView;
    }*/

    @GetMapping
    public String redirectToAccessPages() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String username = auth.getName();//get logged in username
        /*if (!username.equals("anonymousUser")) {
            User user = usersService.getUserByUsername(username);
            System.out.println("status:" + user.getActivated());
            return user.getActivated()==true ? "redirect:/home" : "redirect:/confirm_registration";
        } else{
            return "index";
        }*/
        return (username.equals("anonymousUser")) ? "index" : "redirect:/home";
    }


    @GetMapping(path = "/home")
    public String getSolarPowerPlantsByUsername(Model model) {

        System.out.println("getSolarPowerPlantsByUsername");
        //Model model=new Model("getall");

        /*List<User> userList=new ArrayList<>();
        userList.add(new User(1,"Name1","Surname1"));
        userList.add(new User(2,"Name2","Surname2"));*/

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String username = auth.getName();//get logged in username
        User user = usersService.getUserByUsername(username);

        if (user.getActivated()) {
            //User user = usersService.getUserByUsername(username);
            System.out.println("status:" + user.getActivated());
            //return user.getActivated()==true ? "redirect:/home" : "redirect:/confirm_registration";

            String userRole = user.getUserRoles().toString();


            // ModelAndView modelAndView = new ModelAndView("home");
            //modelAndView.addObject("users", usersService.getAllUsers());
            //modelAndView.addObject("solarPowerPlants", solarPowerPlantService.getAllSolarPowerPlants());
            model.addAttribute("solarPowerPlantsByUser", solarPowerPlantService.getSolarPowerPlantsByUser(user));
            model.addAttribute("name", username);

            if (userRole.equals("ADMIN")) {
                model.addAttribute("adminAccess", "admin");
                System.out.println("admin access");
            }

            //return usersService.getAllUsers();
            //return modelAndView;
            return "home";
        } else {
            return "confirm_registration";
        }
    }

    @GetMapping(path = "/id={id}")
    public User getUserById(@PathVariable("id") Integer id) {
        System.out.println("getUserById");
        return usersService.getUserById(id).orElse(null);
    }

    /*@PostMapping
    public void addUser(@Valid @NotNull @RequestBody User user) {
        usersService.addUser(user);
    }*/

    /*@RequestMapping(value = "/save", method = RequestMethod.POST)
    public String saveCustomer(@ModelAttribute("customer") Customer customer) {
        customerService.save(customer);
        return "redirect:/";
    }*/

    @GetMapping(path = "/add")
    public String redirectToNew() {
        return "redirect:/new";
    }

    @PostMapping(path = "/add")
    public String addUser(@Valid @ModelAttribute("user") User user, BindingResult bindingResult) {

        if (bindingResult.hasErrors()) {
            System.out.println("BINDING RESULT ERROR");
            return "add_user";
        } else {

            System.out.println("addUser");
            //UserRole userRole = userRoleService.getUserRole("USER");
            user.setUserRoles(UserRoles.USER);
            user.setActivated(false);
            user.setLocked(false);
            user.setPassword(bCryptPasswordEncoder.encode(user.getPassword()));
            System.out.println("activated: " + user.getActivated());
            usersService.saveUser(user);

            //відправляємо посилання активації
            confirmationCodeService.sendConfirmationCode(user, TypesConfirmationCode.confirmRegistration);
            /*UUID uuid = UUID.randomUUID();
            String stringConfirmationCode = uuid.toString();
            System.out.println("confirmationCode: " + stringConfirmationCode);

            ConfirmationCode confirmationCode = new ConfirmationCode(user, stringConfirmationCode);
            confirmationCodeService.saveConfirmationCode(confirmationCode);

            usersService.sendMailWithConfirmationCode(user.getEmail(), confirmationCode.getConfirmationCode());*/

            System.out.println("--- --- ---");

            return "success_user_registration";
        }
    }

    @GetMapping(path = "/confirm_registration")
    public String confirmUserRegistration(Model model) {
        model.addAttribute("email", "emailll");
        System.out.println("confirmUserRegistration");
        return "confirm_registration";
    }


    @PostMapping(path = "/confirmRegistration")
    public String confirmRegistration(@Valid @ModelAttribute("confirmationCode") ConfirmationCode confirmationCode, Model model) {

        System.out.println("confirmRegistration");

        if ("confirmationCode.getConfirmationCode()".equals("1234")) {
            model.addAttribute("okMessage", "Реєстрацію аккаунту підтверджено.");
        } else {
            model.addAttribute("errorMessage", "Код підтвердження недійсний.");
        }

        return "success_user_registration_confirmed";

    }

    @GetMapping("/new")
    public String newCustomerForm(Model model) {
        System.out.println("newCustomerForm");
        User user = new User();
        model.addAttribute("user", user);
        return "add_user";
    }

    /*@GetMapping(path = "/success_user_registration")
    public String successUserRegistration(Model model) {
        model.addAttribute("email", "emailll");
        System.out.println("successUserRegistration");
        return "success_user_registration";
    }*/

    /*@GetMapping("/sign-in")
    String signIn() {
        return "login";
    }*/

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
            User user=confirmationResult.get().getUser();
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

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String username = auth.getName();//get logged in username
        User user = usersService.getUserByUsername(username);

        //зроби деактивацію всіх активних кодів для користувача

        confirmationCodeService.deactivateConfirmationCodesByUser(user);

        //відправляємо посилання активації
        confirmationCodeService.sendConfirmationCode(user, TypesConfirmationCode.confirmRegistration);
        /*UUID uuid = UUID.randomUUID();
        String stringConfirmationCode = uuid.toString();
        System.out.println("confirmationCode: " + stringConfirmationCode);

        ConfirmationCode confirmationCode = new ConfirmationCode(user, stringConfirmationCode);
        confirmationCodeService.saveConfirmationCode(confirmationCode);

        usersService.sendMailWithConfirmationCode(user.getEmail(), confirmationCode.getConfirmationCode());*/

        model.addAttribute("sendingCodeMessage", "Посилання успішно відправлено ще раз на вказаний e-mail: {email}");

        return "confirm_registration";
    }

    @GetMapping(path = "/profile")
    public String goToProfilePage(Model model) {
        System.out.println("goToProfilePage");

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String username = auth.getName();//get logged in username
        User user = usersService.getUserByUsername(username);

        model.addAttribute("userInformation", user);

        Boolean accountStatus = user.getActivated();
        model.addAttribute("accountStatus", accountStatus ? "Активований" : "Не активовний");

        return "profile";
    }

    @GetMapping(path = "/edit_profile")
    public String editProfileInfo(Model model) {
        System.out.println("editProfileInfo");

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String username = auth.getName();//get logged in username
        User user = usersService.getUserByUsername(username);

        model.addAttribute("userInformation", user);

        Boolean accountStatus = user.getActivated();
        model.addAttribute("accountStatus", accountStatus ? "Активований" : "Не активовний");

        return "edit_profile";
    }

    @PostMapping(path = "/updateProfileInfo")
    public String updateProfileInfo(@Valid @ModelAttribute("userInformation") User updatedUserInfo, BindingResult bindingResult) {

        System.out.println("updateProfileInfo");

        if (bindingResult.hasErrors()) {
            System.out.println("BINDING RESULT ERROR");
            return "edit_profile";
        } else {

            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            String username = auth.getName();//get logged in username
            User user = usersService.getUserByUsername(username);

            //updatedUserInfo.getStringInfo();

            usersService.updateUserInformation(user,updatedUserInfo);

            //usersService.saveUser(updatedUserInfo);

            return "redirect:/profile";
        }
    }

    @GetMapping(path = "/recover_password")
    public String recoverPasswordRequest(Model model) {
        //model.addAttribute("email", "emailll");
        PasswordRecoverInformation recoverInformation=new PasswordRecoverInformation();

        model.addAttribute("recoverInformation", recoverInformation);
        System.out.println("recoverPassword");
        return "recover_password";
    }

    @PostMapping(path="/recoverPassword")
    public String sendResetPasswordMail(@Valid @ModelAttribute("recoverInformation") PasswordRecoverInformation recoverInformation, Model model){
        model.addAttribute("sendMessage","Повідомлення для відновлення паролю надіслано на вказаний email: "+recoverInformation.getEmail()+".");

        User user=usersService.getUserByUsername(recoverInformation.getUsername());

        confirmationCodeService.sendConfirmationCode(user,TypesConfirmationCode.recoverPassword);

        return "recover_password";
    }

    @GetMapping(path = "/recover/{confirmationCode}")
    public String recoverPassword(@PathVariable("confirmationCode") String confirmationCode,
                                  //@PathVariable("username") String username,
                                  Model model) {
        System.out.println("CCode: " + confirmationCode);
        //System.out.println("U: " + username);

        Optional<ConfirmationCode> confirmationResult = confirmationCodeService.findConfirmationCodeByConfirmationCode(confirmationCode);

        if (confirmationResult.isPresent() && confirmationResult.get().getValid()) {
            confirmationResult.get().setValid(false);
            confirmationCodeService.saveConfirmationCode(confirmationResult.get());
            //User user = usersService.getUserByUsername(username);
            /*User user=confirmationResult.get().getUser();
            user.setActivated(true);
            usersService.saveUser(user);*/
            model.addAttribute("recoverSignalOK", "Введіть новий пароль.");
            model.addAttribute("username",confirmationResult.get().getUser().getUsername());
            model.addAttribute("confirmationCode",confirmationCode);
            /*String password="",passwordAgain="";

            ArrayList<String> updatePassword=new ArrayList<>();
            updatePassword.add(password);
            updatePassword.add(passwordAgain);

            model.addAttribute("updatePassword", updatePassword);*/
        } else {
            model.addAttribute("errorMessage", "Код підтвердження недійсний.");
        }
        return "recover_password";
    }

    @PostMapping(path = "/recover/{confirmationCode}")
    public String updatePassword(@Valid @ModelAttribute("updatePassword") ArrayList<String> updatePassword,
                                 //@RequestParam(value="username",required = false) String username,
                                // @RequestParam("username") String username1,
                                 @PathVariable("confirmationCode") String confirmationCode,
                                 @RequestParam("password") String password,
                                 @RequestParam("passwordAgain") String passwordAgain,
                                 Model model){
        //model.addAttribute("sendMessage","Повідомлення для відновлення паролю надіслано на вказаний email: "+recoverInformation.getEmail()+".");
        model.addAttribute("updatePasswordOK","Пароль успішно змінено");
Optional<ConfirmationCode> confirmationCodeResult=confirmationCodeService.findConfirmationCodeByConfirmationCode(confirmationCode);
User user=confirmationCodeResult.get().getUser();
        //System.out.println("username1: "+username1);
        System.out.println("Confirmation code: "+confirmationCode);
        System.out.println("password: "+password);
        System.out.println("passwordAgain: "+passwordAgain);
        //User user=usersService.getUserByUsername(recoverInformation.getUsername());

        if(password.equals(passwordAgain)){
            user.setPassword(bCryptPasswordEncoder.encode(password));
            usersService.saveUser(user);
        }

        System.out.println("************************");

        //confirmationCodeService.sendConfirmationCode(user,TypesConfirmationCode.recoverPassword);

        return "recover_password";
    }

}
