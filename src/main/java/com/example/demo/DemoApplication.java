package com.example.demo;

import com.example.demo.dao.UsersRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;

@SpringBootApplication
@EnableAsync
public class DemoApplication implements CommandLineRunner {
@Autowired
    private   UsersRepository usersRepository;


    public static void main(String[] args) {
        SpringApplication.run(DemoApplication.class, args);
    }


    @Override
    public void run(String... args) throws Exception {
/*usersRepository.save(new User("username","name","surname","$10$NS9mwzj5sm9Vx5le/zoUeOBKjmsnPwyvme9c.mdyrpZOHQMSGlmcm",
        UserRole.USER,false,false));*/
    }
}
