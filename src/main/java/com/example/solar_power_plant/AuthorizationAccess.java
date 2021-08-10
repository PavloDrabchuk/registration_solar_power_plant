package com.example.solar_power_plant;

import com.example.solar_power_plant.model.SolarPowerPlant;
import com.example.solar_power_plant.model.User;
import com.example.solar_power_plant.enums.UserRoles;
import com.example.solar_power_plant.service.UsersService;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.core.io.Resource;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.ui.Model;

import javax.validation.constraints.NotNull;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.util.*;

public class AuthorizationAccess {

    /*private final UsersService usersService;

    public AuthorizationAccess(UsersService usersService) {
        this.usersService = usersService;
    }*/

    public static Optional<User> getAuthorisedUser(UsersService usersService) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();

        //System.out.println("auth:"+auth);
        String username = auth.getName(); //get logged in username
        //System.out.println(".... username: "+auth.getName());

        //System.out.println("-- userService: "+usersService);
        //System.out.println(" --- userService: "+usersService.getClass());

        //String username="qwerty";

        //Optional<User> user=usersService.getUserByUsername(username);
        List<User> users = usersService.getAllUsers();

        /*for(User u:users){
            System.out.println("uu: "+u.getUsername());
        }*/
        /*if(user.isPresent()){
            System.out.println("us: "+user.get().getUsername());
        }
        else System.out.println("no no no :)");*/

        //System.out.println(" yyy: "+usersService.getUserByUsername(username));
        return usersService.getUserByUsername(username);
    }

    public static void addAdminAccessToModel(Model model, UsersService usersService) {
        Optional<User> user = getAuthorisedUser(usersService);

        if (user.isPresent() && user.get().getUserRole() == UserRoles.ROLE_ADMIN) {
            model.addAttribute("adminAccess", "admin");
            //System.out.println("admin access");
        }
    }

    public static ArrayList<Integer> getUsageTime(@NotNull SolarPowerPlant solarPowerPlant) {
        ArrayList<Integer> usageTime = new ArrayList<>();

        Calendar calendar = new GregorianCalendar();
        calendar.setTime(solarPowerPlant.getStaticData().getInstallationDate());

        LocalDate date = LocalDate.now();

        int year = date.getYear(),
                month = date.getMonthValue(),
                day = date.getDayOfMonth();

        /*year = date.getYear();
        month = date.getMonthValue();
        day = date.getDayOfMonth();*/

        day -= calendar.get(Calendar.DAY_OF_MONTH);
        if (day < 0) month--;
        month -= (calendar.get(Calendar.MONTH) + 1);
        if (month < 0) year--;
        year -= calendar.get(Calendar.YEAR);

        usageTime.add(0, year);
        usageTime.add(1, month);
        usageTime.add(2, day);

        return usageTime;
    }

    public static String getFileContent(String filePath) throws IOException {

        ApplicationContext appContext =
                new ClassPathXmlApplicationContext(new String[]{});

        Resource resource = appContext.getResource(filePath);

        StringBuilder sb = new StringBuilder();
        BufferedReader br = null;
        try {
            br = new BufferedReader(
                    new InputStreamReader(resource.getInputStream(), StandardCharsets.UTF_8));
            String line;
            while ((line = br.readLine()) != null) {
                sb.append(line);
            }
        } finally {
            if (br != null) try {
                br.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return sb.toString();
    }

}
