package com.example.solar_power_plant.service;

import com.example.solar_power_plant.repository.UsersRepository;
import com.example.solar_power_plant.enums.TypesConfirmationCode;
import com.example.solar_power_plant.model.User;

import com.example.solar_power_plant.enums.UserRoles;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class UsersService {

    private final UsersRepository usersRepository;

    private final EmailSenderService emailSenderService;


    @Value("${ADMIN_EMAIL}")
    private String ADMIN_EMAIL;

    @Autowired
    public UsersService(UsersRepository usersRepository,
                        EmailSenderService emailSenderService) {
        this.usersRepository = usersRepository;
        this.emailSenderService = emailSenderService;
    }

    public Optional<User> getUserById(Long id) {
        return usersRepository.findUserById(id);
    }

    public List<User> getAllUsers() {
        return (List<User>) usersRepository.findAll();
    }

    public void saveUser(User user) {
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

    public Optional<User> getUserByUserRole(UserRoles userRoles) {
        return usersRepository.findUserByUserRole(userRoles);
    }

    public List<User> getAllUsersByUserRole(UserRoles userRoles) {
        return usersRepository.findAllByUserRole(userRoles);
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

        // TODO: 10.08.2021 Text mail.

        if (typeConfirmationCode.name().equals("ConfirmRegistration")) {
            confirmationMessage.setSubject("Confirmation mail");
            confirmationMessage.setText("<html><body><h1>header</h1> Please: http://localhost:8080/confirm/" + confirmationCode + "</body></html>");
        } else if (typeConfirmationCode.name().equals("RecoverPassword")) {
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


    public void sendRemovingUserEmail(String email) {
        String subject = "Видалення аккаунту";
        String text = "Доброго дня. Ваш аккаунт видалено з системи. У разі виникнення питань звертайтесь до адміністратора: "
                + ADMIN_EMAIL + ".";

        emailSenderService.sendEmail(emailSenderService.createSimpleMail(email, subject, text));
    }
}
