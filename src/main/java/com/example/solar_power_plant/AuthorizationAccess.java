package com.example.solar_power_plant;

import com.example.solar_power_plant.model.SolarPowerPlant;
import com.example.solar_power_plant.model.User;
import com.example.solar_power_plant.enums.UserRoles;
import com.example.solar_power_plant.service.UsersService;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.core.io.Resource;
import org.springframework.security.core.Authentication;
//import org.springframework.security.core.context.SecurityContextHolder;


import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.ui.Model;

//import javax.validation.constraints.NotNull;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.time.Month;
import java.time.YearMonth;
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

    public static ArrayList<Integer> getUsageTime(LocalDate startDate, LocalDate endDate) {
        ArrayList<Integer> usageTime = new ArrayList<>();

        //LocalDate localDate = solarPowerPlant.getStaticData().getInstallationDate();

        //Calendar calendar = new GregorianCalendar();
        //calendar.setTime(solarPowerPlant.getStaticData().getInstallationDate());

        //LocalDate date = LocalDate.now();

        int year = endDate.getYear(),
                month = endDate.getMonthValue(),
                day = endDate.getDayOfMonth();

        /*year = date.getYear();
        month = date.getMonthValue();
        day = date.getDayOfMonth();*/

//        System.out.println("1) day: " + day + ", month: " + month + ", year: " + year);


        //int daysInMonth = YearMonth.of(year, month).lengthOfMonth();
        //System.out.println("days: " + daysInMonth);

        day -= startDate.getDayOfMonth();
        if (day < 0) {
            day += YearMonth.of(year, month).lengthOfMonth();
            month--;
        }
        month -= startDate.getMonthValue();
        if (month < 0) {
            month += 12;
            year--;
        }
        year -= startDate.getYear();

//        System.out.println("2) day: " + localDate.getDayOfMonth() + ", month: " + localDate.getMonthValue() + ", year: " + localDate.getYear());

//        System.out.println("3) day: " + day + ", month: " + month + ", year: " + year);


        usageTime.add(0, Math.max(year, 0));
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

    public static int getNumPagesList(List arrayList, double limit) {
        //double limitTracksId = 2;

        //List<String> listTrackId = tracksRepository.getListTrackId();
        //List<String> listTrackId = tracksRepository.getListTrackIdForPage((Integer.parseInt(page) - 1) * (int) limitTracksId, (int) limitTracksId);
        /*List<String> pageNumList = new ArrayList<>();
        for (int i = 1; i <= ((int) Math.ceil(getSolarPowerPlantsByUser(user).size() / limit)); i++) {
            pageNumList.add(Integer.toString(i));
        }*/
        return (int) Math.ceil(arrayList.size() / limit);
    }

    public static int getPage(String page, int maxPage) {
        int pageInt;
        try {
            pageInt = Integer.parseInt(page);
        } catch (NumberFormatException ex) {
            //System.err.println("Invalid string in argument");
            pageInt = 1;
        }

        if (pageInt > maxPage || pageInt <= 0) pageInt = 1;

        return pageInt;
    }


}
