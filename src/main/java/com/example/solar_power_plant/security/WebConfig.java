package com.example.solar_power_plant.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {
   @Bean
    public BCryptPasswordEncoder bCryptPasswordEncoder() {

        return new BCryptPasswordEncoder();
    }
    public void addViewControllers(ViewControllerRegistry registry) {
        registry.addViewController("/home").setViewName("home");
        registry.addViewController("/").setViewName("index");
        /*registry.addViewController("/uploadForm").setViewName("uploadForm");*/
        registry.addViewController("/login").setViewName("login");
        registry.addViewController("/about").setViewName("info-pages/about");
        registry.addViewController("/registration-info").setViewName("info-pages/registration-info");
        //registry.addViewController("/admin").setViewName("admin");
    }


}
