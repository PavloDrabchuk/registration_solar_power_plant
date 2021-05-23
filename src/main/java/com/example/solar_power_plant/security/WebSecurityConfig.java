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
//@AllArgsConstructor
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {


    private final UserDetailsService userDetailsService;

    private final BCryptPasswordEncoder bCryptPasswordEncoder;

    @Autowired
    public WebSecurityConfig(@Qualifier("myUserDetailsService") UserDetailsService userDetailsService,
                             BCryptPasswordEncoder bCryptPasswordEncoder) {
        this.userDetailsService = userDetailsService;
        this.bCryptPasswordEncoder = bCryptPasswordEncoder;
    }

    /*@Autowired
    public WebSecurityConfig (UsersService usersService,BCryptPasswordEncoder bCryptPasswordEncoder){
        this.usersService=usersService;
        this.bCryptPasswordEncoder=bCryptPasswordEncoder;
    }*/

    @Override
    protected void configure(HttpSecurity http) throws Exception {

        http.authorizeRequests()
                .antMatchers("/", "/index", "/add_user/**", "/new", "/addUser", "/registration/**", "/confirm_registration", "/success_user_registration", "/confirm/**", "/recover/**", "/recover_password", "/recoverPassword", "/updatePassword")
                .permitAll()
                .antMatchers("/styles/**").permitAll()
                .antMatchers("/data/seed").permitAll()
                .antMatchers("/test-json").permitAll()
                .antMatchers("/scripts/**").permitAll()
                .antMatchers("/images/**").permitAll()
                .antMatchers("/admin/**")
                .hasAuthority("ADMIN")
                .antMatchers("/sign-up/**", "/home/**",
                        "/add_solar_power_plant/**", "/messages")
                .hasAnyAuthority("USER", "ADMIN", "EDITOR")
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

       /* auth.inMemoryAuthentication()
                .withUser("nameSurname").password(bCryptPasswordEncoder.encode("qwerty123")).roles("USER")
                .and()
                .withUser("user2").password(bCryptPasswordEncoder.encode("user2Pass")).roles("USER")
                .and()
                .withUser("admin").password(bCryptPasswordEncoder.encode("adminPass")).roles("ADMIN")
                .and()
                .withUser("admin1").password(bCryptPasswordEncoder.encode("admin")).authorities("ADMIN");
*/
        auth.userDetailsService(userDetailsService)
                .passwordEncoder(bCryptPasswordEncoder);
    }
    /*@Bean
    public BCryptPasswordEncoder bCryptPasswordEncoder() {
        return new BCryptPasswordEncoder();
    }*/


}
