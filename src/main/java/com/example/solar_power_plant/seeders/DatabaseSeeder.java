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
        seedSolarPowerPlantTable(7, 5);
        seedDynamicDataTable();
        seedMessageTable();
        seedSolarPowerPlantTable(1, 2);
        //seedSolarPowerPlantTable(2,1);

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
        user1.setActivated(false);
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
        user4.setEmail("example4@gmail.com");
        user4.setUsername("qwerty1233");
        user4.setUserRole(UserRoles.USER);
        user4.setActivated(true);
        user4.setLocked(false);
        user4.setPassword(bCryptPasswordEncoder.encode("qwerty4"));
        user4.setDateTimeOfCreation(LocalDateTime.now());
        usersService.saveUser(user4);

        User user5 = new User();
        user5.setEmail("example5@gmail.com");
        user5.setUsername("qwerty1234");
        user5.setUserRole(UserRoles.USER);
        user5.setActivated(true);
        user5.setLocked(false);
        user5.setPassword(bCryptPasswordEncoder.encode("qwerty5"));
        user5.setDateTimeOfCreation(LocalDateTime.now().minusDays(19));
        usersService.saveUser(user5);

        User user6 = new User();
        user6.setEmail("editor@gmail.com");
        user6.setUsername("editor");
        user6.setUserRole(UserRoles.EDITOR);
        user6.setActivated(true);
        user6.setLocked(false);
        user6.setPassword(bCryptPasswordEncoder.encode("qwerty"));
        user6.setDateTimeOfCreation(LocalDateTime.now().minusDays(24));
        usersService.saveUser(user6);

        User user7 = new User();
        user7.setEmail("example6@gmail.com");
        user7.setUsername("qwerty1236");
        user7.setUserRole(UserRoles.USER);
        user7.setActivated(true);
        user7.setLocked(false);
        user7.setPassword(bCryptPasswordEncoder.encode("qwerty7"));
        user7.setDateTimeOfCreation(LocalDateTime.now().minusDays(2));
        usersService.saveUser(user7);

        User user8 = new User();
        user8.setEmail("example7@gmail.com");
        user8.setUsername("qwerty1237");
        user8.setUserRole(UserRoles.USER);
        user8.setActivated(true);
        user8.setLocked(false);
        user8.setPassword(bCryptPasswordEncoder.encode("qwerty8"));
        user8.setDateTimeOfCreation(LocalDateTime.now());
        usersService.saveUser(user8);
    }

    private void seedSolarPowerPlantTable(int userId, int solarPowerPlantQuantity) throws ParseException {

        /*int userQuantity = 2,
                solarPowerPlantQuantity = 1;*/

        int i = userId;
        //for (int i = 1; i <= userQuantity; i++) {
        for (int j = 0; j < solarPowerPlantQuantity; j++) {
            StaticData staticData = new StaticData();
            staticData.setQuantity(5 + (int) (Math.random() * 30));
            staticData.setPower((int) (350 + Math.random() * 200));

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
        // }
    }

    private void seedDynamicDataTable() throws IOException {
        String str = "2021-06-10 00:00:00";
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        LocalDateTime dateTime = LocalDateTime.parse(str, formatter);

        //for (int k = 0; k < 20; k++) {

        //   dateTime = dateTime.plusMinutes(30);
        //dateTime = dateTime.plusMinutes(60*12);
        int a = 21, b = 22;
        Optional<SolarPowerPlant> solarPowerPlant = solarPowerPlantService.getSolarPowerPlantById(1L);

        if (solarPowerPlant.isPresent()) {
            dynamicDataService.addDynamicData(new DynamicData(solarPowerPlant.get(), Weather.FewClouds,
                    (((Math.sin(0.4 * 5 - 1.02)) + 1) / 2) * solarPowerPlant.get().getStaticData().getQuantity() * solarPowerPlant.get().getStaticData().getPower() * ((a + (int) (Math.random() * b)) / 10),
                    LocalDateTime.parse("2020-05-02 10:00:00", formatter)));

            dynamicDataService.addDynamicData(new DynamicData(solarPowerPlant.get(), Weather.FewClouds,
                    (((Math.sin(0.4 * 6 - 1.02)) + 1) / 2) * solarPowerPlant.get().getStaticData().getQuantity() * solarPowerPlant.get().getStaticData().getPower() * ((a + (int) (Math.random() * b)) / 10),
                    LocalDateTime.parse("2020-06-02 10:00:00", formatter)));

            dynamicDataService.addDynamicData(new DynamicData(solarPowerPlant.get(), Weather.FewClouds,
                    (((Math.sin(0.4 * 7 - 1.02)) + 1) / 2) * solarPowerPlant.get().getStaticData().getQuantity() * solarPowerPlant.get().getStaticData().getPower() * ((a + (int) (Math.random() * b)) / 10),
                    LocalDateTime.parse("2020-07-02 10:00:00", formatter)));

            dynamicDataService.addDynamicData(new DynamicData(solarPowerPlant.get(), Weather.FewClouds,
                    (((Math.sin(0.4 * 8 - 1.02)) + 1) / 2) * solarPowerPlant.get().getStaticData().getQuantity() * solarPowerPlant.get().getStaticData().getPower() * ((a + (int) (Math.random() * b)) / 10),
                    LocalDateTime.parse("2020-08-02 10:00:00", formatter)));

            dynamicDataService.addDynamicData(new DynamicData(solarPowerPlant.get(), Weather.ClearSky,
                    (((Math.sin(0.4 * 9 - 1.02)) + 1) / 2) * solarPowerPlant.get().getStaticData().getQuantity() * solarPowerPlant.get().getStaticData().getPower() * ((a + (int) (Math.random() * b)) / 10),
                    LocalDateTime.parse("2020-09-02 10:00:00", formatter)));

            dynamicDataService.addDynamicData(new DynamicData(solarPowerPlant.get(), Weather.BrokenClouds,
                    (((Math.sin(0.4 * 10 - 1.02)) + 1) / 2) * solarPowerPlant.get().getStaticData().getQuantity() * solarPowerPlant.get().getStaticData().getPower() * ((a + (int) (Math.random() * b)) / 10),
                    LocalDateTime.parse("2020-10-02 10:00:00", formatter)));

            dynamicDataService.addDynamicData(new DynamicData(solarPowerPlant.get(), Weather.LightRain,
                    (((Math.sin(0.4 * 11 - 1.02)) + 1) / 2) * solarPowerPlant.get().getStaticData().getQuantity() * solarPowerPlant.get().getStaticData().getPower() * ((a + (int) (Math.random() * b)) / 10),
                    LocalDateTime.parse("2020-11-02 10:00:00", formatter)));

            dynamicDataService.addDynamicData(new DynamicData(solarPowerPlant.get(), Weather.OvercastClouds,
                    (((Math.sin(0.4 * 12 - 1.02)) + 1) / 2) * solarPowerPlant.get().getStaticData().getQuantity() * solarPowerPlant.get().getStaticData().getPower() * ((a + (int) (Math.random() * b)) / 10),
                    LocalDateTime.parse("2020-12-02 10:00:00", formatter)));

            dynamicDataService.addDynamicData(new DynamicData(solarPowerPlant.get(), Weather.ShowerRain,
                    (((Math.sin(0.4 * 1 - 1.02)) + 1) / 2) * solarPowerPlant.get().getStaticData().getQuantity() * solarPowerPlant.get().getStaticData().getPower() * ((a + (int) (Math.random() * b)) / 10),
                    LocalDateTime.parse("2021-01-02 10:00:00", formatter)));

            dynamicDataService.addDynamicData(new DynamicData(solarPowerPlant.get(), Weather.FewClouds,
                    (((Math.sin(0.4 * 2 - 1.02)) + 1) / 2) * solarPowerPlant.get().getStaticData().getQuantity() * solarPowerPlant.get().getStaticData().getPower() * ((a + (int) (Math.random() * b)) / 10),
                    LocalDateTime.parse("2021-02-02 10:00:00", formatter)));

            dynamicDataService.addDynamicData(new DynamicData(solarPowerPlant.get(), Weather.Thunderstorm,
                    (((Math.sin(0.4 * 3 - 1.02)) + 1) / 2) * solarPowerPlant.get().getStaticData().getQuantity() * solarPowerPlant.get().getStaticData().getPower() * ((a + (int) (Math.random() * b)) / 10),
                    LocalDateTime.parse("2021-03-02 10:00:00", formatter)));

            dynamicDataService.addDynamicData(new DynamicData(solarPowerPlant.get(), Weather.FewClouds,
                    (((Math.sin(0.4 * 4 - 1.02)) + 1) / 2) * solarPowerPlant.get().getStaticData().getQuantity() * solarPowerPlant.get().getStaticData().getPower() * ((a + (int) (Math.random() * b)) / 10),
                    LocalDateTime.parse("2021-04-02 10:00:00", formatter)));

            dynamicDataService.addDynamicData(new DynamicData(solarPowerPlant.get(), Weather.FewClouds,
                    (((Math.sin(0.4 * 5 - 1.02)) + 1) / 2) * solarPowerPlant.get().getStaticData().getQuantity() * solarPowerPlant.get().getStaticData().getPower() * ((a + (int) (Math.random() * b)) / 10),
                    LocalDateTime.parse("2021-05-02 10:00:00", formatter)));

            /*dynamicDataService.addDynamicData(new DynamicData(solarPowerPlant.get(), Weather.FewClouds, 35.1,
                    LocalDateTime.parse("2021-04-02 10:00:00", formatter)));*/

        }
        dynamicDataService.saveDynamicData(dateTime, true);

        /*solarPowerPlant.ifPresent(powerPlant -> dynamicDataService.addDynamicData(new DynamicData(powerPlant, Weather.ClearSky, 229.9,
                LocalDateTime.parse("2021-05-04 10:00:00", formatter))));*/

        /*solarPowerPlant.ifPresent(powerPlant -> dynamicDataService.addDynamicData(new DynamicData(powerPlant, Weather.ClearSky, 153.1,
                LocalDateTime.parse("2021-06-04 10:00:00", formatter))));*/

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
        Optional<User> user3 = usersService.getUserByUsername("qwerty1231");
        Optional<User> editor = usersService.getUserByUsername("editor");

        Message message1 = new Message("title1", "Text 1 - Lorem ipsum dolor sit amet, consectetur adipiscing elit. Sed vel tellus a elit ultrices tempor. Donec vel augue congue, elementum dui quis, porta ligula. Ut eros est, tempus eget turpis non, lacinia mattis eros. Curabitur facilisis, ipsum eu blandit tempor, ligula sapien finibus ligula, vel lobortis velit libero et ligula. Nunc a velit a nulla commodo fringilla. Curabitur dapibus euismod condimentum. Phasellus vel mauris facilisis, tempus purus ut, porttitor tellus. Integer bibendum volutpat metus, sed vulputate lectus. Donec fermentum dignissim eros vulputate auctor. Morbi dictum dui enim, tincidunt gravida neque tincidunt eu. Nulla sodales, tortor in congue mollis, est dolor posuere velit, convallis interdum libero massa vitae dolor. Ut mollis risus vitae metus luctus, nec pharetra nunc pharetra. Aliquam faucibus lacus ut erat scelerisque, eu tempus nisl feugiat.", user1.get(), editor.get(), MessageType.INFORMATION, true);
        message1.setDateTime(LocalDateTime.now().minusDays(3).minusMinutes(153));
        messageService.save(message1);

        Message message2 = new Message("title2", "Text 2 - Lorem ipsum dolor sit amet, consectetur adipiscing elit. Sed vel tellus a elit ultrices tempor. Donec vel augue congue, elementum dui quis, porta ligula. Ut eros est, tempus eget turpis non, lacinia mattis eros. Curabitur facilisis, ipsum eu blandit tempor, ligula sapien finibus ligula, vel lobortis velit libero et ligula. Nunc a velit a nulla commodo fringilla. Curabitur dapibus euismod condimentum. Phasellus vel mauris facilisis, tempus purus ut, porttitor tellus. Integer bibendum volutpat metus, sed vulputate lectus. Donec fermentum dignissim eros vulputate auctor. Morbi dictum dui enim, tincidunt gravida neque tincidunt eu. Nulla sodales, tortor in congue mollis, est dolor posuere velit, convallis interdum libero massa vitae dolor. Ut mollis risus vitae metus luctus, nec pharetra nunc pharetra. Aliquam faucibus lacus ut erat scelerisque, eu tempus nisl feugiat.", user2.get(), editor.get(), MessageType.UPDATE, false);
        message2.setDateTime(LocalDateTime.now().minusMinutes(12));
        messageService.save(message2);

        Message message3 = new Message("title3", "Text 3 - Lorem ipsum dolor sit amet, consectetur adipiscing elit. Sed vel tellus a elit ultrices tempor. Donec vel augue congue, elementum dui quis, porta ligula. Ut eros est, tempus eget turpis non, lacinia mattis eros. Curabitur facilisis, ipsum eu blandit tempor, ligula sapien finibus ligula, vel lobortis velit libero et ligula. Nunc a velit a nulla commodo fringilla. Curabitur dapibus euismod condimentum. Phasellus vel mauris facilisis, tempus purus ut, porttitor tellus. Integer bibendum volutpat metus, sed vulputate lectus. Donec fermentum dignissim eros vulputate auctor. Morbi dictum dui enim, tincidunt gravida neque tincidunt eu. Nulla sodales, tortor in congue mollis, est dolor posuere velit, convallis interdum libero massa vitae dolor. Ut mollis risus vitae metus luctus, nec pharetra nunc pharetra. Aliquam faucibus lacus ut erat scelerisque, eu tempus nisl feugiat.", user3.get(), editor.get(), MessageType.FOR_EDITOR, true);
        message3.setDateTime(LocalDateTime.now().minusHours(10).minusMinutes(17));
        messageService.save(message3);

        Message message4 = new Message("title4", "Text 4 - Lorem ipsum dolor sit amet, consectetur adipiscing elit. Sed vel tellus a elit ultrices tempor. Donec vel augue congue, elementum dui quis, porta ligula. Ut eros est, tempus eget turpis non, lacinia mattis eros. Curabitur facilisis, ipsum eu blandit tempor, ligula sapien finibus ligula, vel lobortis velit libero et ligula. Nunc a velit a nulla commodo fringilla. Curabitur dapibus euismod condimentum. Phasellus vel mauris facilisis, tempus purus ut, porttitor tellus. Integer bibendum volutpat metus, sed vulputate lectus. Donec fermentum dignissim eros vulputate auctor. Morbi dictum dui enim, tincidunt gravida neque tincidunt eu. Nulla sodales, tortor in congue mollis, est dolor posuere velit, convallis interdum libero massa vitae dolor. Ut mollis risus vitae metus luctus, nec pharetra nunc pharetra. Aliquam faucibus lacus ut erat scelerisque, eu tempus nisl feugiat.", editor.get(), user3.get(), MessageType.FOR_USER, false);
        message4.setDateTime(LocalDateTime.now().minusHours(2).minusMinutes(12));
        messageService.save(message4);

        Message message5 = new Message("title5", "Text 5 - Lorem ipsum dolor sit amet, consectetur adipiscing elit. Sed vel tellus a elit ultrices tempor. Donec vel augue congue, elementum dui quis, porta ligula. Ut eros est, tempus eget turpis non, lacinia mattis eros. Curabitur facilisis, ipsum eu blandit tempor, ligula sapien finibus ligula, vel lobortis velit libero et ligula. Nunc a velit a nulla commodo fringilla. Curabitur dapibus euismod condimentum. Phasellus vel mauris facilisis, tempus purus ut, porttitor tellus. Integer bibendum volutpat metus, sed vulputate lectus. Donec fermentum dignissim eros vulputate auctor. Morbi dictum dui enim, tincidunt gravida neque tincidunt eu. Nulla sodales, tortor in congue mollis, est dolor posuere velit, convallis interdum libero massa vitae dolor. Ut mollis risus vitae metus luctus, nec pharetra nunc pharetra. Aliquam faucibus lacus ut erat scelerisque, eu tempus nisl feugiat.", editor.get(), user2.get(), MessageType.ERROR, true);
        message5.setDateTime(LocalDateTime.now().minusMonths(1).minusMinutes(63));
        messageService.save(message5);

    }


}
