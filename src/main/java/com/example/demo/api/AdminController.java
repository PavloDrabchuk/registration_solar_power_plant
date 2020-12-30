package com.example.demo.api;

import com.example.demo.model.User;
import com.example.demo.service.UsersService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.servlet.ModelAndView;

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
        //Model model=new Model("getall");

        /*List<User> userList=new ArrayList<>();
        userList.add(new User(1,"Name1","Surname1"));
        userList.add(new User(2,"Name2","Surname2"));*/

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String username = auth.getName();//get logged in username
        User user = usersService.getUserByUsername(username);

        if(!user.getUserRole().getName().equals("ADMIN")){
            return "home";
        } else{
            model.addAttribute("users", usersService.getAllUsers());
            return "admin_page";
        }


        /*ModelAndView modelAndView = new ModelAndView("admin_page");
        modelAndView.addObject("users", usersService.getAllUsers());

        //return usersService.getAllUsers();
        return modelAndView;*/
    }

    /*@GetMapping(path = "/admin")
    public String redirectToPageByAccess() {
        System.out.println("redirectToPageByAccess");
        //Model model=new Model("getall");



        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String username = auth.getName();//get logged in username
        User user = usersService.getUserByUsername(username);

        return !user.getUserRole().getName().equals("ADMIN") ? "home" : "admin_page";

    }*/
}
