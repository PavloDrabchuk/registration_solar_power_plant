package com.example.demo.service;

import com.example.demo.dao.UsersDao;
import com.example.demo.dao.UsersRepository;
import com.example.demo.model.TypesConfirmationCode;
import com.example.demo.model.User;


//import org.json.simple.parser.JSONParser;
import org.hibernate.validator.internal.util.stereotypes.Lazy;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.web.ServerProperties;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.core.io.Resource;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.io.*;
import java.text.MessageFormat;
import java.util.Iterator;
import java.util.Optional;

@Service
public class UsersService {
    private final UsersDao usersDao;
    private final UsersRepository usersRepository;

    private final EmailSenderService emailSenderService;

    @Autowired
    private BCryptPasswordEncoder bCryptPasswordEncoder;

    @Autowired
    public UsersService(@Qualifier("fakeDao") UsersDao usersDao,
    UsersRepository usersRepository,
                        EmailSenderService emailSenderService){
        this.usersDao=usersDao;
        this.usersRepository=usersRepository;
        this.emailSenderService=emailSenderService;
    }

    public Optional<User> getUserById(Integer id){
        return usersDao.selectUserById(id);
    }

    /*public List<User> getAllUsers(){
        return usersDao.selectAllBooks();
    }*/
    public Iterable<User> getAllUsers(){
        return usersRepository.findAll();
    }

    /*public int addUser(User user){
        return  usersDao.addUser(user);
    }*/

    public void saveUser(User user){
        //usersRepository.save(new User(user.getName(), user.getSurname()));

        //user.setPassword(bCryptPasswordEncoder.encode(user.getPassword()));
        usersRepository.save(user);
    }

    public Optional<User> getUserByUsername(String username){
        return usersRepository.findByUsername(username);
    }

    public Optional<User> getUserByEmail(String email){
        return usersRepository.findByEmail(email);
    }

    /*@Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        final Optional<User> optionalUser = usersRepository.findByUsername(username);

        return (UserDetails) optionalUser.orElseThrow(() -> new UsernameNotFoundException(MessageFormat.format("User with username: {0} cannot be found.", username)));

    }*/

    public void sendMailWithConfirmationCode(String email, String confirmationCode, TypesConfirmationCode typeConfirmationCode){
        SimpleMailMessage confirmationMessage=new SimpleMailMessage();
        confirmationMessage.setTo(email);


        if(typeConfirmationCode.name().equals("confirmRegistration")) {
            confirmationMessage.setSubject("Confirmation mail");
            confirmationMessage.setText("<html><body><h1>header</h1> Please: http://localhost:8080/confirm/" + confirmationCode+"</body></html>");
        } else if(typeConfirmationCode.name().equals("recoverPassword")){
            confirmationMessage.setSubject("Recover password mail");
            confirmationMessage.setText("Please: http://localhost:8080/recover/" + confirmationCode);
        }

        emailSenderService.sendEmail(confirmationMessage);
    }

    public void updateUserInformation(User user, User updatedUserInfo){
        user.setName(updatedUserInfo.getName());
        user.setSurname(updatedUserInfo.getSurname());
        user.setMobilePhoneNumber(updatedUserInfo.getMobilePhoneNumber());

        saveUser(user);
    }

    public  void getFileContent(String filePath) throws IOException {
        /*ApplicationContext appContext =
                new ClassPathXmlApplicationContext(new String[] {});

        Resource resource = appContext.getResource(filePath);

        StringBuilder sb = new StringBuilder();
        BufferedReader br = null;
        try{
            br = new BufferedReader(
                    new InputStreamReader(resource.getInputStream(), "UTF-8"));
            String line;
            while ((line = br.readLine()) != null) {
                sb.append(line);
            }
        }finally {
            if(br != null) try {
                br.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return sb.toString();*/
        JSONParser parser = new JSONParser();

        try {
            Object obj = parser.parse(new FileReader(filePath));

            JSONObject jsonObject =  (JSONObject) obj;

            String name = (String) jsonObject.get("results");
            System.out.println(name);

            /*String city = (String) jsonObject.get("city");
            System.out.println(city);

            String job = (String) jsonObject.get("job");
            System.out.println(job);*/

            // loop array
            /*JSONArray cars = (JSONArray) jsonObject.get("cars");
            Iterator<String> iterator = cars.iterator();
            while (iterator.hasNext()) {
                System.out.println(iterator.next());
            }*/
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }
}
