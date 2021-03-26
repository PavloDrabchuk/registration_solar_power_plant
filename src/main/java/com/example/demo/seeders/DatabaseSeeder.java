package com.example.demo.seeders;

import com.example.demo.model.User;
import com.example.demo.model.UserRoles;
import com.example.demo.service.UsersService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
public class DatabaseSeeder {

    private final UsersService usersService;

    @Autowired
    public DatabaseSeeder(UsersService usersService, BCryptPasswordEncoder bCryptPasswordEncoder) {
        this.usersService = usersService;
        this.bCryptPasswordEncoder = bCryptPasswordEncoder;
    }

    private final BCryptPasswordEncoder bCryptPasswordEncoder;

    @EventListener
    public void seed(ContextRefreshedEvent event) {
        seedUsersTable();
    }

    private void seedUsersTable(){
        User user=new User();
        user.setEmail("ravluk2000@gmail.com");
        user.setUsername("qwerty");
        user.setUserRoles(UserRoles.ADMIN);
        user.setActivated(true);
        user.setLocked(false);
        user.setPassword(bCryptPasswordEncoder.encode("qwerty"));
        user.setDateTimeOfCreation(LocalDateTime.now());
        usersService.saveUser(user);

        User user1=new User();
        user1.setEmail("lab2018.home.work@gmail.com");
        user1.setUsername("qwerty123");
        user1.setUserRoles(UserRoles.USER);
        user1.setActivated(true);
        user1.setLocked(false);
        user1.setPassword(bCryptPasswordEncoder.encode("qwerty"));
        user1.setDateTimeOfCreation(LocalDateTime.now());
        usersService.saveUser(user1);
    }
}
