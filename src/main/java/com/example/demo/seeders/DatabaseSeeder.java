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
        //seedUsersTable();
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

        User user2=new User();
        user2.setEmail("lab2018.home.work1@gmail.com");
        user2.setUsername("qwerty1231");
        user2.setUserRoles(UserRoles.USER);
        user2.setActivated(true);
        user2.setLocked(false);
        user2.setPassword(bCryptPasswordEncoder.encode("qwerty"));
        user2.setDateTimeOfCreation(LocalDateTime.now());
        usersService.saveUser(user2);

        User user3=new User();
        user3.setEmail("lab2018.home.work2@gmail.com");
        user3.setUsername("qwerty1232");
        user3.setUserRoles(UserRoles.USER);
        user3.setActivated(true);
        user3.setLocked(false);
        user3.setPassword(bCryptPasswordEncoder.encode("qwerty"));
        user3.setDateTimeOfCreation(LocalDateTime.now());
        usersService.saveUser(user3);

        User user4=new User();
        user4.setEmail("lab2018.home.work3@gmail.com");
        user4.setUsername("qwerty1233");
        user4.setUserRoles(UserRoles.USER);
        user4.setActivated(true);
        user4.setLocked(false);
        user4.setPassword(bCryptPasswordEncoder.encode("qwerty"));
        user4.setDateTimeOfCreation(LocalDateTime.now());
        usersService.saveUser(user4);
    }
}
