package com.example.solar_power_plant;

import com.example.solar_power_plant.model.User;
import com.example.solar_power_plant.model.UserRoles;
import com.example.solar_power_plant.service.UsersService;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.ui.Model;

import java.util.List;
import java.util.Optional;

public class AuthorizationAccess {

    /*private final UsersService usersService;

    public AuthorizationAccess(UsersService usersService) {
        this.usersService = usersService;
    }*/

    public static Optional<User> getAuthorisedUser(UsersService usersService) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();

        //System.out.println("auth:"+auth);
        String username = auth.getName(); //get logged in username
        //System.out.println(".... username: "+auth.getName());

        //System.out.println("-- userService: "+usersService);
        //System.out.println(" --- userService: "+usersService.getClass());

        //String username="qwerty";

        //Optional<User> user=usersService.getUserByUsername(username);
        List<User> users=usersService.getAllUsers();

        /*for(User u:users){
            System.out.println("uu: "+u.getUsername());
        }*/
        /*if(user.isPresent()){
            System.out.println("us: "+user.get().getUsername());
        }
        else System.out.println("no no no :)");*/

        //System.out.println(" yyy: "+usersService.getUserByUsername(username));
        return usersService.getUserByUsername(username);
    }

    public static  void addAdminAccessToModel(Model model, UsersService usersService) {
        Optional<User> user = getAuthorisedUser(usersService);

        if (user.isPresent() && user.get().getUserRole() == UserRoles.ROLE_ADMIN) {
            model.addAttribute("adminAccess", "admin");
            //System.out.println("admin access");
        }
    }

}
