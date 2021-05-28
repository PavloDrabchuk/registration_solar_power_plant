package com.example.solar_power_plant.controllers;

import com.example.solar_power_plant.model.Product;
import com.example.solar_power_plant.model.Weather;
import com.example.solar_power_plant.service.UsersService;
import com.example.weather.OpenWeather;
import com.google.gson.Gson;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.core.io.Resource;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;

@Controller
public class SeedController {

    private final UsersService usersService;

    @Autowired
    public SeedController(UsersService usersService) {
        this.usersService = usersService;
    }

    @Autowired
    private BCryptPasswordEncoder bCryptPasswordEncoder;

    @GetMapping(path="/say-green-hello")
    public String sayH(){
return "jj";
    }

    @GetMapping(path="/hh")
    public String sayH1(){

        return "hh";
    }

    @GetMapping(path = "/test-json")
    public void testJson() throws IOException {

        String API_KEY = "ad4112f68d38350518e7c19239012a75";


        //Product product = null;
        //GsonBuilder gsonBuilder = new GsonBuilder();
        /*gsonBuilder.registerTypeAdapter(Product.class);
        Gson gson = gsonBuilder.create();*/

        Gson gson = new Gson();
//        String d=Files.readString(Path.of("D:\\My\\Наука\\ПНУ\\Бакалаврська робота\\Test1\\upload-dir\\product.json"));
        String d = getFileContent("http://api.openweathermap.org/data/2.5/weather?lat=48.924569&lon=24.723712&appid=" + API_KEY);
        Product product = gson.fromJson(d, Product.class);
        OpenWeather openWeather = gson.fromJson(d, OpenWeather.class);
//        String d=getFileContent("/upload-dir/product.json");
        //File myFile = new File("product.json");
//"D:\\My\\Наука\\ПНУ\\Бакалаврська робота\\Test1\\upload-dir\\product.json"
        //System.out.println(".-.-.-.-.-.-: data: "+new FileReader("D:\\My\\Наука\\ПНУ\\Бакалаврська робота\\Test1\\upload-dir\\product.json"));
        System.out.println(".-.-.-.-.-.-: data: " + getFileContent("http://api.openweathermap.org/data/2.5/weather?lat=48.924569&lon=24.723712&appid=" + API_KEY));
//        System.out.println(".-.-.-.-.-.-: data: "+ Files.readString(Path.of("D:\\My\\Наука\\ПНУ\\Бакалаврська робота\\Test1\\upload-dir\\product.json")));

        System.out.println("\n product: " + openWeather.getName() + " desc: " + openWeather.getWeather().get(0).getMain());
        System.out.println("==== data: " + d);

        String description = openWeather.getWeather().get(0).getDescription();
//        Double coef= Weather.values().equals()
        double coefficient = -1;
        for (Weather weather : Weather.values()) {
            if (weather.getDescription().equals(description)) {
                coefficient = weather.getCoefficient();
                break;
            }

            /*else {
                coefficient=0.01D;
                //зберігати в файли інформацію про невідомі description
               *//* FileWriter myWriter = new FileWriter("system-information-files/descriptions.txt");

                myWriter.write("Main: \""+openWeather.getWeather().get(0).getMain()+"\", "+
                        "description: \""+description+"\", "+
                        "dt: "+openWeather.getDt()+"\n");
                myWriter.close();*//*

                Path filePath = Paths.get("system-information-files/descriptions.txt");
                Files.writeString(filePath, "Hello World !!\n", StandardOpenOption.APPEND);

                //Verify file content
                String content = Files.readString(filePath);

                System.out.println(content);
            }*/
        }
        if (coefficient == -1) {

            coefficient = 0.01D;
            //зберігати в файли інформацію про невідомі description
               /* FileWriter myWriter = new FileWriter("system-information-files/descriptions.txt");

                myWriter.write("Main: \""+openWeather.getWeather().get(0).getMain()+"\", "+
                        "description: \""+description+"\", "+
                        "dt: "+openWeather.getDt()+"\n");
                myWriter.close();*/

            Path filePath = Paths.get("system-information-files/descriptions.txt");

            String message = "Main: \"" + openWeather.getWeather().get(0).getMain() + "\", " +
                    "description: \"" + description + "\", " +
                    "dt: " + openWeather.getDt() + "\n";

            Files.writeString(filePath, message, StandardOpenOption.APPEND);

            //Verify file content
            //String content = Files.readString(filePath);

            //System.out.println(content);

        }

        System.out.println("description: " + description + " coefficient: " + coefficient);
        /*try(Reader reader = new InputStreamReader(GsonUtil.class.getResourceAsStream("/json/product.json"))){
            product = gson.fromJson(reader, Product.class);
            System.out.println(product.getProductId());
            System.out.println(product.getDescription());
            System.out.println(product.getImageUrl());
            System.out.println(product.getPrice());
        }*/
        //return product;
    }

   /* @GetMapping(path = "/data/seed")
    public String seedData(){
        User user=new User();
        user.setEmail("ravluk2000@gmail.com");
        user.setUsername("qwerty");
        user.setUserRoles(UserRoles.ADMIN);
        user.setActivated(true);
        user.setLocked(false);
        user.setPassword(bCryptPasswordEncoder.encode("qwerty"));
        user.setDateTimeOfCreation(LocalDateTime.now());
        usersService.saveUser(user);

        User user1=new User();
        user1.setEmail("lab2018.home.work@gmail.com");
        user1.setUsername("qwerty123");
        user1.setUserRoles(UserRoles.USER);
        user1.setActivated(true);
        user1.setLocked(false);
        user1.setPassword(bCryptPasswordEncoder.encode("qwerty"));
        user1.setDateTimeOfCreation(LocalDateTime.now());
        usersService.saveUser(user1);

        return "Ok";
    }*/

    public String getFileContent(String filePath) throws IOException {
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
