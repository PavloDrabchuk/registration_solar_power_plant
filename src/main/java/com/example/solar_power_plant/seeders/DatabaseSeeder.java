package com.example.solar_power_plant.seeders;

import com.example.solar_power_plant.model.*;
import com.example.solar_power_plant.service.DynamicDataService;
import com.example.solar_power_plant.service.MessageService;
import com.example.solar_power_plant.service.SolarPowerPlantService;
import com.example.solar_power_plant.service.UsersService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.text.ParseException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Optional;

@Component
public class DatabaseSeeder {

    private final UsersService usersService;
    private final SolarPowerPlantService solarPowerPlantService;
    private final DynamicDataService dynamicDataService;
    private final MessageService messageService;


    @Autowired
    public DatabaseSeeder(UsersService usersService,
                          BCryptPasswordEncoder bCryptPasswordEncoder,
                          SolarPowerPlantService solarPowerPlantService,
                          DynamicDataService dynamicDataService,
                          MessageService messageService) {
        this.usersService = usersService;
        this.bCryptPasswordEncoder = bCryptPasswordEncoder;
        this.solarPowerPlantService = solarPowerPlantService;
        this.dynamicDataService = dynamicDataService;
        this.messageService = messageService;
    }

    private final BCryptPasswordEncoder bCryptPasswordEncoder;

    @EventListener
    public void seed(ContextRefreshedEvent event) throws ParseException, IOException {
        seedUsersTable();
        seedSolarPowerPlantTable();
        seedDynamicDataTable();
        seedMessageTable();
    }

    private void seedUsersTable() {
        User user = new User();
        user.setEmail("ravluk2000@gmail.com");
        user.setUsername("qwerty");
        user.setUserRole(UserRoles.ADMIN);
        user.setActivated(true);
        user.setLocked(false);
        user.setPassword(bCryptPasswordEncoder.encode("qwerty"));
        user.setDateTimeOfCreation(LocalDateTime.now().minusDays(35));
        usersService.saveUser(user);

        User user1 = new User();
        user1.setEmail("example1@gmail.com");
        user1.setUsername("qwerty123");
        user1.setUserRole(UserRoles.USER);
        user1.setActivated(true);
        user1.setLocked(false);
        user1.setPassword(bCryptPasswordEncoder.encode("qwerty"));
        user1.setDateTimeOfCreation(LocalDateTime.now());
        usersService.saveUser(user1);

        User user2 = new User();
        user2.setEmail("example2@gmail.com");
        user2.setUsername("qwerty1231");
        user2.setUserRole(UserRoles.USER);
        user2.setActivated(true);
        user2.setLocked(false);
        user2.setPassword(bCryptPasswordEncoder.encode("qwerty"));
        user2.setDateTimeOfCreation(LocalDateTime.now().minusDays(12));
        usersService.saveUser(user2);

        User user3 = new User();
        user3.setEmail("example3@gmail.com");
        user3.setUsername("qwerty1232");
        user3.setUserRole(UserRoles.USER);
        user3.setActivated(true);
        user3.setLocked(false);
        user3.setPassword(bCryptPasswordEncoder.encode("qwerty3"));
        user3.setDateTimeOfCreation(LocalDateTime.now().minusDays(1));
        usersService.saveUser(user3);

        User user4 = new User();
        user4.setEmail("lab2018.home.work3@gmail.com");
        user4.setUsername("qwerty1233");
        user4.setUserRole(UserRoles.USER);
        user4.setActivated(true);
        user4.setLocked(false);
        user4.setPassword(bCryptPasswordEncoder.encode("qwerty4"));
        user4.setDateTimeOfCreation(LocalDateTime.now());
        usersService.saveUser(user4);

        User user5 = new User();
        user5.setEmail("lab2018.home.work3@gmail.com");
        user5.setUsername("qwerty1234");
        user5.setUserRole(UserRoles.USER);
        user5.setActivated(true);
        user5.setLocked(false);
        user5.setPassword(bCryptPasswordEncoder.encode("qwerty5"));
        user5.setDateTimeOfCreation(LocalDateTime.now().minusDays(19));
        usersService.saveUser(user5);

        User user6 = new User();
        user6.setEmail("lab2018.home.work3@gmail.com");
        user6.setUsername("qwerty1235");
        user6.setUserRole(UserRoles.EDITOR);
        user6.setActivated(true);
        user6.setLocked(false);
        user6.setPassword(bCryptPasswordEncoder.encode("qwerty6"));
        user6.setDateTimeOfCreation(LocalDateTime.now().minusDays(24));
        usersService.saveUser(user6);

        User user7 = new User();
        user7.setEmail("lab2018.home.work3@gmail.com");
        user7.setUsername("qwerty1236");
        user7.setUserRole(UserRoles.USER);
        user7.setActivated(true);
        user7.setLocked(false);
        user7.setPassword(bCryptPasswordEncoder.encode("qwerty7"));
        user7.setDateTimeOfCreation(LocalDateTime.now().minusDays(2));
        usersService.saveUser(user7);

        User user8 = new User();
        user8.setEmail("lab2018.home.work3@gmail.com");
        user8.setUsername("qwerty1237");
        user8.setUserRole(UserRoles.USER);
        user8.setActivated(true);
        user8.setLocked(false);
        user8.setPassword(bCryptPasswordEncoder.encode("qwerty8"));
        user8.setDateTimeOfCreation(LocalDateTime.now());
        usersService.saveUser(user8);
    }

    private void seedSolarPowerPlantTable() throws ParseException {

        int userQuantity = 2,
                solarPowerPlantQuantity = 1;

        for (int i = 1; i <= userQuantity; i++) {
            for (int j = 0; j < solarPowerPlantQuantity; j++) {
                StaticData staticData = new StaticData();
                staticData.setQuantity(1 + (int) (Math.random() * 20));
                staticData.setPower(350 + Math.random() * 200);

                /*String stringYear="20";
                int year=1+(int)(Math.random()*20);
                if(year<10)stringYear+="0"+year;
                else stringYear+=year;*/

                staticData.setInstallationDate("20" + getStringNumberForDate(1, 20) + "-" + getStringNumberForDate(1, 12) + "-" + getStringNumberForDate(1, 28));

                Location location1 = new Location("Україна",
                        Region.IvanoFrankivsk,
                        "Івано-Франківськ", "Грушевського", Integer.toString((i + 1) * (j + 1)),
                        23.92065 + (1 + Math.random() * 5),
                        44.71355 + (1 + Math.random() * 5));
                SolarPowerPlant solarPowerPlant1 = new SolarPowerPlant("qwedfv" + i + "_" + j,
                        "Назва станції № " + i + "_" + j, location1, usersService.getUserById((long) i).get());
                solarPowerPlant1.setStaticData(staticData);
                solarPowerPlantService.addSolarPowerPlant(solarPowerPlant1, 0);

                String str = "2021-04-02 00:00:00";
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
                LocalDateTime dateTime = LocalDateTime.parse(str, formatter);


                //staticData.setQuantity(i*(j+1));
                //staticData.setInstallationDate("2021-03-29");
                //staticData.setSolarPowerPlant(solarPowerPlantService.getSolarPowerPlantById((long)i).get());

            }
        }
    }

    private void seedDynamicDataTable() throws IOException {
        String str = "2021-04-06 00:00:00";
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        LocalDateTime dateTime = LocalDateTime.parse(str, formatter);

        //for (int k = 0; k < 20; k++) {

        //   dateTime = dateTime.plusMinutes(30);
        //dateTime = dateTime.plusMinutes(60*12);

        dynamicDataService.saveDynamicData(dateTime, true);

        // }
    }

    private String getStringNumberForDate(int startNum, int finishNum) {
        //String stringYear="20";
        String result = "";
        int num = startNum + (int) (Math.random() * finishNum);
        if (num < 10) result += "0" + num;
        else result += num;

        return result;
    }

    private void seedMessageTable() {
        Optional<User> user1 = usersService.getUserByUsername("qwerty");
        Optional<User> user2 = usersService.getUserByUsername("qwerty123");
        Optional<User> editor = usersService.getUserByUsername("qwerty1235");

        Message message1 = new Message("title1", "text1", user1.get(), editor.get(), MessageType.UPDATE, true);
        messageService.save(message1);

        Message message2 = new Message("title2", "text2", user2.get(), editor.get(), MessageType.UPDATE, true);
        messageService.save(message2);
    }


}
