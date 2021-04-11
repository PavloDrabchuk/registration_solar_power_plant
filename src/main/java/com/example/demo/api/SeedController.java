package com.example.demo.api;

import com.example.demo.model.Product;
import com.example.demo.model.User;
import com.example.demo.model.UserRoles;
import com.example.demo.service.UsersService;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.core.io.Resource;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;

@RestController
public class SeedController {

    private final UsersService usersService;

    @Autowired
    public SeedController(UsersService usersService) {
        this.usersService = usersService;
    }

    @Autowired
    private BCryptPasswordEncoder bCryptPasswordEncoder;

    @GetMapping(path = "/test-json")
    public void testJson() throws IOException {
        //Product product = null;
        //GsonBuilder gsonBuilder = new GsonBuilder();
        /*gsonBuilder.registerTypeAdapter(Product.class);
        Gson gson = gsonBuilder.create();*/

        Gson gson=new Gson();
        String d=Files.readString(Path.of("D:\\My\\Наука\\ПНУ\\Бакалаврська робота\\Test1\\upload-dir\\product.json"));
        Product product= gson.fromJson(d,Product.class);
//        String d=getFileContent("/upload-dir/product.json");
        //File myFile = new File("product.json");
//"D:\\My\\Наука\\ПНУ\\Бакалаврська робота\\Test1\\upload-dir\\product.json"
        //System.out.println(".-.-.-.-.-.-: data: "+new FileReader("D:\\My\\Наука\\ПНУ\\Бакалаврська робота\\Test1\\upload-dir\\product.json"));
        System.out.println(".-.-.-.-.-.-: data: "+ Files.readString(Path.of("D:\\My\\Наука\\ПНУ\\Бакалаврська робота\\Test1\\upload-dir\\product.json")));

        System.out.println("\n product: "+product.getProductId()+" desc: "+product.getDescription());
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
