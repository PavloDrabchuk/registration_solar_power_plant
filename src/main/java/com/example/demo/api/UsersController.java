package com.example.demo.api;

import com.example.demo.model.ConfirmationCode;
import com.example.demo.model.User;
import com.example.demo.model.UserRoles;
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
import java.util.Optional;
import java.util.UUID;

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

    @GetMapping(path="/add")
    public String redirectToNew(){
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
            UUID uuid = UUID.randomUUID();
            String stringConfirmationCode = uuid.toString() + "_" + user.getUsername();
            System.out.println("confirmationCode: " + stringConfirmationCode);

            ConfirmationCode confirmationCode = new ConfirmationCode(user, stringConfirmationCode);
            confirmationCodeService.saveConfirmationCode(confirmationCode);

            usersService.sendMailWithConfirmationCode(user.getEmail(), confirmationCode.getConfirmationCode());
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

    @GetMapping(path = "/confirm/{confirmationCode}_{username}")
    public String activateAccount(@PathVariable("confirmationCode") String confirmationCode,
                                @PathVariable("username") String username,
                                Model model) {
        System.out.println("CCode: " + confirmationCode);
        System.out.println("U: " + username);

        Optional<ConfirmationCode> confirmationResult = confirmationCodeService.findConfirmationCodeByConfirmationCode(confirmationCode + "_" + username);
        if (confirmationResult.isPresent()) {
            User user=usersService.getUserByUsername(username);
            user.setActivated(true);
            usersService.saveUser(user);
            model.addAttribute("okMessage", "Реєстрацію аккаунту підтверджено.");
        } else {
            model.addAttribute("errorMessage", "Код підтвердження недійсний.");
        }
        return "success_user_registration_confirmed";
    }
}
