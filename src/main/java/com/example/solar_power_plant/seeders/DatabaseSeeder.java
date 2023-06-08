package com.example.solar_power_plant.seeders;

import com.example.solar_power_plant.enums.MessageType;
import com.example.solar_power_plant.enums.Region;
import com.example.solar_power_plant.enums.UserRoles;
import com.example.solar_power_plant.enums.Weather;
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
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.Random;

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
    }

    private void seedUsersTable() {
        // TODO: 16.12.2021 Дані в масив об'єднай і створи функцію. І в циклі збережи дані.

        User user;
        for (int i = 0; i < 9; i++) {
            user = new User();
            user.setEmail(i == 0 ? "ravluk2000@gmail.com" : ("example" + i + "@gmail.com"));
            user.setUsername(i == 1 ? "editor" : ("qwerty" + i));
            user.setUserRole(UserRoles.ROLE_USER);

            if (i == 0) user.setUserRole(UserRoles.ROLE_ADMIN);
            else if (i == 1) user.setUserRole(UserRoles.ROLE_EDITOR);
            else user.setUserRole(UserRoles.ROLE_USER);

            user.setActivated(i != 2);
            user.setLocked(false);
            user.setPassword(bCryptPasswordEncoder.encode("qwerty" + i));
            user.setDateTimeOfCreation(LocalDateTime.now().minusDays((int) (1.0 + Math.random() * 50)));
            usersService.saveUser(user);
        }
    }

    private void seedSolarPowerPlantTable(int userId, int solarPowerPlantQuantity) throws ParseException {

        for (int j = 0; j < solarPowerPlantQuantity; j++) {
            StaticData staticData = new StaticData();
            staticData.setQuantity(1 + (int) (Math.random() * 30));
            staticData.setPower((int) (350 + Math.random() * 200));

            staticData.setInstallationDate("20"
                    + getStringNumberForDate(1, 20) + "-"
                    + getStringNumberForDate(1, 12) + "-"
                    + getStringNumberForDate(1, 28));

            Location location1 = new Location("Україна",
                    Region.IvanoFrankivsk,
                    "Івано-Франківськ", "Грушевського", Integer.toString((userId + 1) * (j + 1)),
                    23.92065 + (1 + Math.random() * 5),
                    44.71355 + (1 + Math.random() * 5));
            SolarPowerPlant solarPowerPlant1 = new SolarPowerPlant("qwerty" + userId + "_" + j,
                    "Назва станції № " + userId + "_" + j, location1, usersService.getUserById((long) userId).orElseThrow());
            solarPowerPlant1.setStaticData(staticData);

            solarPowerPlantService.addSolarPowerPlant(solarPowerPlant1, 0);
        }
    }

    private void seedDynamicDataTable() throws IOException {
        String str = "2021-06-10 00:00:00";
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        LocalDateTime dateTime = LocalDateTime.parse(str, formatter);

        int a = 21, b = 22;
        Optional<SolarPowerPlant> solarPowerPlant = solarPowerPlantService.getSolarPowerPlantById(1L);

        if (solarPowerPlant.isPresent()) {
            for (int i = 0; i < 13; i++) {
                LocalDateTime dateTime1 = LocalDateTime.now().minusDays(i * 31);

                dynamicDataService.addDynamicData(new DynamicData(solarPowerPlant.get(),
                        Weather.values()[new Random().nextInt(Weather.values().length)],
                        (((Math.sin(0.4 * 5 - 1.02)) + 1) / 2) * solarPowerPlant.get().getStaticData().getQuantity() * solarPowerPlant.get().getStaticData().getPower() * ((a + (int) (Math.random() * b)) / 10),
                        dateTime1));
            }
        }
        dynamicDataService.saveDynamicData(dateTime, true);
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
        Optional<User> user1 = usersService.getUserByUsername("qwerty0");
        Optional<User> user2 = usersService.getUserByUsername("qwerty2");
        Optional<User> user3 = usersService.getUserByUsername("qwerty3");
        Optional<User> editor = usersService.getUserByUsername("editor");

        Message message1 = new Message("title1", "Text 1 - Lorem ipsum dolor sit amet, consectetur adipiscing elit. Sed vel tellus a elit ultrices tempor. Donec vel augue congue, elementum dui quis, porta ligula. Ut eros est, tempus eget turpis non, lacinia mattis eros. Curabitur facilisis, ipsum eu blandit tempor, ligula sapien finibus ligula, vel lobortis velit libero et ligula. Nunc a velit a nulla commodo fringilla. Curabitur dapibus euismod condimentum. Phasellus vel mauris facilisis, tempus purus ut, porttitor tellus. Integer bibendum volutpat metus, sed vulputate lectus. Donec fermentum dignissim eros vulputate auctor. Morbi dictum dui enim, tincidunt gravida neque tincidunt eu. Nulla sodales, tortor in congue mollis, est dolor posuere velit, convallis interdum libero massa vitae dolor. Ut mollis risus vitae metus luctus, nec pharetra nunc pharetra. Aliquam faucibus lacus ut erat scelerisque, eu tempus nisl feugiat.",
                user1.get(), editor.get(), MessageType.INFORMATION, true);
        message1.setDateTime(LocalDateTime.now().minusDays(3).minusMinutes(153));
        messageService.save(message1);

        Message message2 = new Message("title2", "Text 2 - Lorem ipsum dolor sit amet, consectetur adipiscing elit. Sed vel tellus a elit ultrices tempor. Donec vel augue congue, elementum dui quis, porta ligula. Ut eros est, tempus eget turpis non, lacinia mattis eros. Curabitur facilisis, ipsum eu blandit tempor, ligula sapien finibus ligula, vel lobortis velit libero et ligula. Nunc a velit a nulla commodo fringilla. Curabitur dapibus euismod condimentum. Phasellus vel mauris facilisis, tempus purus ut, porttitor tellus. Integer bibendum volutpat metus, sed vulputate lectus. Donec fermentum dignissim eros vulputate auctor. Morbi dictum dui enim, tincidunt gravida neque tincidunt eu. Nulla sodales, tortor in congue mollis, est dolor posuere velit, convallis interdum libero massa vitae dolor. Ut mollis risus vitae metus luctus, nec pharetra nunc pharetra. Aliquam faucibus lacus ut erat scelerisque, eu tempus nisl feugiat.",
                user2.get(), editor.get(), MessageType.UPDATE, false);
        message2.setDateTime(LocalDateTime.now().minusMinutes(12));
        messageService.save(message2);

        Message message3 = new Message("title3", "Text 3 - Lorem ipsum dolor sit amet, consectetur adipiscing elit. Sed vel tellus a elit ultrices tempor. Donec vel augue congue, elementum dui quis, porta ligula. Ut eros est, tempus eget turpis non, lacinia mattis eros. Curabitur facilisis, ipsum eu blandit tempor, ligula sapien finibus ligula, vel lobortis velit libero et ligula. Nunc a velit a nulla commodo fringilla. Curabitur dapibus euismod condimentum. Phasellus vel mauris facilisis, tempus purus ut, porttitor tellus. Integer bibendum volutpat metus, sed vulputate lectus. Donec fermentum dignissim eros vulputate auctor. Morbi dictum dui enim, tincidunt gravida neque tincidunt eu. Nulla sodales, tortor in congue mollis, est dolor posuere velit, convallis interdum libero massa vitae dolor. Ut mollis risus vitae metus luctus, nec pharetra nunc pharetra. Aliquam faucibus lacus ut erat scelerisque, eu tempus nisl feugiat.",
                user3.get(), editor.get(), MessageType.FOR_ROLE_EDITOR, true);
        message3.setDateTime(LocalDateTime.now().minusHours(10).minusMinutes(17));
        messageService.save(message3);

        Message message4 = new Message("title4", "Text 4 - Lorem ipsum dolor sit amet, consectetur adipiscing elit. Sed vel tellus a elit ultrices tempor. Donec vel augue congue, elementum dui quis, porta ligula. Ut eros est, tempus eget turpis non, lacinia mattis eros. Curabitur facilisis, ipsum eu blandit tempor, ligula sapien finibus ligula, vel lobortis velit libero et ligula. Nunc a velit a nulla commodo fringilla. Curabitur dapibus euismod condimentum. Phasellus vel mauris facilisis, tempus purus ut, porttitor tellus. Integer bibendum volutpat metus, sed vulputate lectus. Donec fermentum dignissim eros vulputate auctor. Morbi dictum dui enim, tincidunt gravida neque tincidunt eu. Nulla sodales, tortor in congue mollis, est dolor posuere velit, convallis interdum libero massa vitae dolor. Ut mollis risus vitae metus luctus, nec pharetra nunc pharetra. Aliquam faucibus lacus ut erat scelerisque, eu tempus nisl feugiat.",
                editor.get(), user3.get(), MessageType.FOR_ROLE_USER, false);
        message4.setDateTime(LocalDateTime.now().minusHours(2).minusMinutes(12));
        messageService.save(message4);

        Message message5 = new Message("title5", "Text 5 - Lorem ipsum dolor sit amet, consectetur adipiscing elit. Sed vel tellus a elit ultrices tempor. Donec vel augue congue, elementum dui quis, porta ligula. Ut eros est, tempus eget turpis non, lacinia mattis eros. Curabitur facilisis, ipsum eu blandit tempor, ligula sapien finibus ligula, vel lobortis velit libero et ligula. Nunc a velit a nulla commodo fringilla. Curabitur dapibus euismod condimentum. Phasellus vel mauris facilisis, tempus purus ut, porttitor tellus. Integer bibendum volutpat metus, sed vulputate lectus. Donec fermentum dignissim eros vulputate auctor. Morbi dictum dui enim, tincidunt gravida neque tincidunt eu. Nulla sodales, tortor in congue mollis, est dolor posuere velit, convallis interdum libero massa vitae dolor. Ut mollis risus vitae metus luctus, nec pharetra nunc pharetra. Aliquam faucibus lacus ut erat scelerisque, eu tempus nisl feugiat.",
                editor.get(), user2.get(), MessageType.ERROR, true);
        message5.setDateTime(LocalDateTime.now().minusMonths(1).minusMinutes(63));
        messageService.save(message5);

    }


}
