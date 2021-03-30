package com.example.demo.seeders;

import com.example.demo.model.*;
import com.example.demo.service.SolarPowerPlantService;
import com.example.demo.service.UsersService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;

import java.text.ParseException;
import java.time.LocalDateTime;
import java.util.UUID;

@Component
public class DatabaseSeeder {

    private final UsersService usersService;
    private final SolarPowerPlantService solarPowerPlantService;
    //private final StaticDataSe staticData;

    @Autowired
    public DatabaseSeeder(UsersService usersService,
                          BCryptPasswordEncoder bCryptPasswordEncoder,
                          SolarPowerPlantService solarPowerPlantService) {
        this.usersService = usersService;
        this.bCryptPasswordEncoder = bCryptPasswordEncoder;
        this.solarPowerPlantService = solarPowerPlantService;
    }

    private final BCryptPasswordEncoder bCryptPasswordEncoder;

    @EventListener
    public void seed(ContextRefreshedEvent event) throws ParseException {
        seedUsersTable();
        seedSolarPowerPlantTable();
    }

    private void seedUsersTable() {
        User user = new User();
        user.setEmail("ravluk2000@gmail.com");
        user.setUsername("qwerty");
        user.setUserRoles(UserRoles.ADMIN);
        user.setActivated(true);
        user.setLocked(false);
        user.setPassword(bCryptPasswordEncoder.encode("qwerty"));
        user.setDateTimeOfCreation(LocalDateTime.now());
        usersService.saveUser(user);

        User user1 = new User();
        user1.setEmail("lab2018.home.work@gmail.com");
        user1.setUsername("qwerty123");
        user1.setUserRoles(UserRoles.USER);
        user1.setActivated(true);
        user1.setLocked(false);
        user1.setPassword(bCryptPasswordEncoder.encode("qwerty"));
        user1.setDateTimeOfCreation(LocalDateTime.now());
        usersService.saveUser(user1);

        User user2 = new User();
        user2.setEmail("lab2018.home.work1@gmail.com");
        user2.setUsername("qwerty1231");
        user2.setUserRoles(UserRoles.USER);
        user2.setActivated(true);
        user2.setLocked(false);
        user2.setPassword(bCryptPasswordEncoder.encode("qwerty"));
        user2.setDateTimeOfCreation(LocalDateTime.now());
        usersService.saveUser(user2);

        User user3 = new User();
        user3.setEmail("lab2018.home.work2@gmail.com");
        user3.setUsername("qwerty1232");
        user3.setUserRoles(UserRoles.USER);
        user3.setActivated(true);
        user3.setLocked(false);
        user3.setPassword(bCryptPasswordEncoder.encode("qwerty"));
        user3.setDateTimeOfCreation(LocalDateTime.now());
        usersService.saveUser(user3);

        User user4 = new User();
        user4.setEmail("lab2018.home.work3@gmail.com");
        user4.setUsername("qwerty1233");
        user4.setUserRoles(UserRoles.USER);
        user4.setActivated(true);
        user4.setLocked(false);
        user4.setPassword(bCryptPasswordEncoder.encode("qwerty"));
        user4.setDateTimeOfCreation(LocalDateTime.now());
        usersService.saveUser(user4);
    }

    private void seedSolarPowerPlantTable() throws ParseException {

        for (int i = 1; i <= 5; i++) {
            for (int j = 0; j < 5; j++) {
                StaticData staticData=new StaticData();
                staticData.setQuantity(i*(j+1));
                staticData.setInstallationDate("2021-03-29");

                Location location1 = new Location("Україна",
                        Region.IvanoFrankivsk,
                        "Івано-Франківськ", "Грушевського", Integer.toString(i*j), 42.14512, 38.65412);
                SolarPowerPlant solarPowerPlant1 = new SolarPowerPlant("qwedfv"+ i +"_"+ j,
                        "Name"+ i +"_"+ j, location1, usersService.getUserById((long) i).get());
                solarPowerPlant1.setStaticData(staticData);
                solarPowerPlantService.addSolarPowerPlant(solarPowerPlant1);

                //staticData.setQuantity(i*(j+1));
                //staticData.setInstallationDate("2021-03-29");
                //staticData.setSolarPowerPlant(solarPowerPlantService.getSolarPowerPlantById((long)i).get());

            }
        }
    }


}
