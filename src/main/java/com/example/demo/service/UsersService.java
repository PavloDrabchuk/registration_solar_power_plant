package com.example.demo.service;

import com.example.demo.dao.UsersDao;
import com.example.demo.dao.UsersRepository;
import com.example.demo.model.TypesConfirmationCode;
import com.example.demo.model.User;


//import org.json.simple.parser.JSONParser;
import org.hibernate.validator.internal.util.stereotypes.Lazy;
//import org.json.simple.JSONArray;
//import org.json.simple.JSONObject;
//import org.json.simple.parser.JSONParser;
//import org.json.simple.parser.ParseException;
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

    public Optional<User> getUserById(Long id){

        return usersRepository.findUserById(id);
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

    public void deleteUserById(Long id){
        usersRepository.deleteById(id);
    }

    public void deleteUser(User user){
        usersRepository.delete(user);
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


}
