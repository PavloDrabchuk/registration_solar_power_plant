package com.example.demo.api;

import com.example.demo.dao.UserRoleRepository;
import com.example.demo.model.User;
import com.example.demo.model.UserRole;
import com.example.demo.service.SolarPowerPlantService;
import com.example.demo.service.UserRoleService;
import com.example.demo.service.UsersService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import java.util.Map;

@Controller
public class UsersController {

    private final UsersService usersService;
    private final SolarPowerPlantService solarPowerPlantService;
    private final UserRoleService userRoleService;

    @Autowired
    public UsersController(UsersService usersService,
                           SolarPowerPlantService solarPowerPlantService,
                           UserRoleService userRoleService) {
        this.usersService = usersService;
        this.solarPowerPlantService = solarPowerPlantService;
        this.userRoleService = userRoleService;
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
    public String redirectToAccessPages(){
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String username = auth.getName();//get logged in username
        return (username=="anonymousUser") ? "index" : "redirect:/home";

    }

    @GetMapping(path = "/home")
    public ModelAndView getSolarPowerPlantsByUsername() {
        System.out.println("getSolarPowerPlantsByUsername");
        //Model model=new Model("getall");

        /*List<User> userList=new ArrayList<>();
        userList.add(new User(1,"Name1","Surname1"));
        userList.add(new User(2,"Name2","Surname2"));*/

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String username = auth.getName();//get logged in username
        User user = usersService.getUserByUsername(username);


        ModelAndView modelAndView = new ModelAndView("home");
        modelAndView.addObject("users", usersService.getAllUsers());
        modelAndView.addObject("solarPowerPlants", solarPowerPlantService.getAllSolarPowerPlants());
        modelAndView.addObject("solarPowerPlantsByUser", solarPowerPlantService.getSolarPowerPlantsByUser(user));
        modelAndView.addObject("name", username);
        //return usersService.getAllUsers();
        return modelAndView;
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

    @PostMapping(path = "/add")
    public String addUser(@ModelAttribute("user") User user) {
        System.out.println("addUser");
        UserRole userRole = userRoleService.getUserRole("USER");
        user.setUserRole(userRole);
        usersService.addUser(user);
        return "redirect:/home";
    }

    @GetMapping("/new")
    public String newCustomerForm(Map<String, Object> model) {
        System.out.println("newCustomerForm");
        User user = new User();
        model.put("user", user);
        return "add_user";
    }

    /*@GetMapping("/sign-in")
    String signIn() {
        return "login";
    }*/
}
