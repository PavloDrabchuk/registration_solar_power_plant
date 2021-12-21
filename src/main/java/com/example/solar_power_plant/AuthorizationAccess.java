package com.example.solar_power_plant;

import com.example.solar_power_plant.model.User;
import com.example.solar_power_plant.service.UsersService;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.core.io.Resource;
import org.springframework.security.core.Authentication;

import org.springframework.security.core.context.SecurityContextHolder;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.*;

public class AuthorizationAccess {

    public static Optional<User> getAuthorisedUser(UsersService usersService) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String username = auth.getName(); //get logged username

        return usersService.getUserByUsername(username);
    }

    public static ArrayList<Integer> getUsageTime(LocalDate startDate, LocalDate endDate) {
        ArrayList<Integer> usageTime = new ArrayList<>();

        int year = endDate.getYear(),
                month = endDate.getMonthValue(),
                day = endDate.getDayOfMonth();

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
        return (int) Math.ceil(arrayList.size() / limit);
    }

    public static int getPage(String page, int maxPage) {
        int pageInt;
        try {
            pageInt = Integer.parseInt(page);
        } catch (NumberFormatException ex) {
            pageInt = 1;
        }

        if (pageInt > maxPage || pageInt <= 0) pageInt = 1;

        return pageInt;
    }
}
