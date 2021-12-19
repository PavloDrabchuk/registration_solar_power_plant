package com.example.solar_power_plant;

import com.example.solar_power_plant.model.User;
import com.example.solar_power_plant.enums.UserRoles;
import com.example.solar_power_plant.service.MessageService;
import com.example.solar_power_plant.service.UsersService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

import java.util.Optional;

@ControllerAdvice
public class GlobalControllerAdvice {

    private final MessageService messageService;
    private final UsersService usersService;

    private Optional<User> authorizedUser = Optional.empty();

    @Autowired
    public GlobalControllerAdvice(MessageService messageService,
                                  UsersService usersService) {
        this.messageService = messageService;
        this.usersService = usersService;
    }

    @ModelAttribute("countUnreadMessages")
    public long getCountUnreadMessages() {
        authorizedUser = AuthorizationAccess.getAuthorisedUser(this.usersService);

        System.out.println(" Count unread message: " + authorizedUser.map(messageService::getCountUnreadMessagesByUser).orElse(0L));

        return authorizedUser.map(messageService::getCountUnreadMessagesByUser).orElse(0L);

        /*authorizedUser.ifPresent(user -> model.addAttribute("countUnreadMessages",
                messageService.getCountUnreadMessagesByUser(user)));*/
    }

    @ModelAttribute("adminAccess")
    public String addAdminAccess() {
        /*Optional<User> user = getAuthorisedUser();

        if (user.isPresent() && user.get().getUserRole() == UserRoles.ROLE_ADMIN) {
            model.addAttribute("adminAccess", "admin");
            //System.out.println("admin access");
        }*/

        authorizedUser = AuthorizationAccess.getAuthorisedUser(this.usersService);

        if (authorizedUser.isPresent() && authorizedUser.get().getUserRole() == UserRoles.ROLE_ADMIN) {
            return "admin";
        } else return null;

    }
}
