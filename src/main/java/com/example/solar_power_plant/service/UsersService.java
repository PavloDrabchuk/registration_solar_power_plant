package com.example.solar_power_plant.service;

import com.example.solar_power_plant.dao.UsersRepository;
import com.example.solar_power_plant.model.TypesConfirmationCode;
import com.example.solar_power_plant.model.User;


//import org.json.simple.parser.JSONParser;
//import org.json.simple.JSONArray;
//import org.json.simple.JSONObject;
//import org.json.simple.parser.JSONParser;
//import org.json.simple.parser.ParseException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class UsersService {

    private final UsersRepository usersRepository;

    private final EmailSenderService emailSenderService;

    @Autowired
    private BCryptPasswordEncoder bCryptPasswordEncoder;

    @Autowired
    public UsersService(UsersRepository usersRepository,
                        EmailSenderService emailSenderService) {
        this.usersRepository = usersRepository;
        this.emailSenderService = emailSenderService;
    }

    public Optional<User> getUserById(Long id) {

        return usersRepository.findUserById(id);
    }

    /*public List<User> getAllUsers(){
        return usersDao.selectAllBooks();
    }*/
    public List<User> getAllUsers() {
        return (List<User>) usersRepository.findAll();
    }

    /*public int addUser(User user){
        return  usersDao.addUser(user);
    }*/

    public void saveUser(User user) {
        //usersRepository.save(new User(user.getName(), user.getSurname()));

        //user.setPassword(bCryptPasswordEncoder.encode(user.getPassword()));
        usersRepository.save(user);
    }

    public void deleteUserById(Long id) {
        usersRepository.deleteById(id);
    }

    public void deleteUser(User user) {
        usersRepository.delete(user);
    }


    public Optional<User> getUserByUsername(String username) {
        return usersRepository.findByUsername(username);
    }

    public Optional<User> getUserByEmail(String email) {
        return usersRepository.findByEmail(email);
    }

    /*@Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        final Optional<User> optionalUser = usersRepository.findByUsername(username);

        return (UserDetails) optionalUser.orElseThrow(() -> new UsernameNotFoundException(MessageFormat.format("User with username: {0} cannot be found.", username)));

    }*/

    public List<String> getNumPagesList(List<User> users, double limit) {
        //double limitTracksId = 2;

        //List<String> listTrackId = tracksRepository.getListTrackId();
        //List<String> listTrackId = tracksRepository.getListTrackIdForPage((Integer.parseInt(page) - 1) * (int) limitTracksId, (int) limitTracksId);
        List<String> pageNumList = new ArrayList<>();
        for (int i = 1; i <= ((int) Math.ceil(users.size() / limit)); i++) {
            pageNumList.add(Integer.toString(i));
        }
        return pageNumList;
    }

    public List<User> getUsersForPage(int offset, int limit) {
        return usersRepository.getListUsersForPage(offset, limit);
    }

    public List<User> getUsersByUsernameForPage(String username, int offset, int limit) {
        return usersRepository.getListUsersByUsernameForPage(username, offset, limit);
    }

    public List<User> getUsersByUsername(String username) {
        return usersRepository.getUsersByUsernameContaining(username);
    }

    public void sendMailWithConfirmationCode(String email, String confirmationCode, TypesConfirmationCode typeConfirmationCode) {
        SimpleMailMessage confirmationMessage = new SimpleMailMessage();
        confirmationMessage.setTo(email);


        if (typeConfirmationCode.name().equals("confirmRegistration")) {
            confirmationMessage.setSubject("Confirmation mail");
            confirmationMessage.setText("<html><body><h1>header</h1> Please: http://localhost:8080/confirm/" + confirmationCode + "</body></html>");
        } else if (typeConfirmationCode.name().equals("recoverPassword")) {
            confirmationMessage.setSubject("Recover password mail");
            confirmationMessage.setText("Please: http://localhost:8080/recover/" + confirmationCode);
        }

        emailSenderService.sendEmail(confirmationMessage);
    }

    public void updateUserInformation(User user, User updatedUserInfo) {
        user.setName(updatedUserInfo.getName());
        user.setSurname(updatedUserInfo.getSurname());
        user.setMobilePhoneNumber(updatedUserInfo.getMobilePhoneNumber());

        saveUser(user);
    }


}
