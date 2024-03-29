package com.example.solar_power_plant.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

@Configuration
@EnableWebSecurity
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {

    private final UserDetailsService userDetailsService;

    private final BCryptPasswordEncoder bCryptPasswordEncoder;

    @Autowired
    public WebSecurityConfig(@Qualifier("myUserDetailsService") UserDetailsService userDetailsService,
                             BCryptPasswordEncoder bCryptPasswordEncoder) {
        this.userDetailsService = userDetailsService;
        this.bCryptPasswordEncoder = bCryptPasswordEncoder;
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {

        // TODO: 31.08.2021 Optimise this.
        http.authorizeRequests()
                .antMatchers("/", "/index", "/add_user/**", "/new", "/addUser", "/registration/**", "/confirm-registration", "/success_user_registration", "/confirm/**", "/recover/**", "/recover-password", "/recoverPassword", "/updatePassword")
                .permitAll()
                .antMatchers("/styles/**").permitAll()
                .antMatchers("/data/seed").permitAll()
                .antMatchers("/test-json").permitAll()
                .antMatchers("/scripts/**").permitAll()
                .antMatchers("/images/**").permitAll()
                .antMatchers("/about", "/registration-info", "/data-collection", "/user-registration-info", "/developer", "/rules", "/support", "/data_sets/**", "/data-sets").permitAll()
                .antMatchers("/admin/**")
                .hasAuthority("ROLE_ADMIN")
                .antMatchers("/sign-up/**", "/home/**",
                        "/add_solar_power_plant/**", "/messages")
                .hasAnyAuthority("ROLE_USER", "ROLE_ADMIN", "ROLE_EDITOR")
                .anyRequest()
                .authenticated()
                .and()
                .formLogin()
                .loginPage("/login")
                .permitAll()
                .and()
                .logout()
                .permitAll();
    }

    @Autowired
    public void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.userDetailsService(userDetailsService)
                .passwordEncoder(bCryptPasswordEncoder);
    }
}
