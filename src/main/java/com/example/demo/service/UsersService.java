package com.example.demo.service;

import com.example.demo.dao.UsersDao;
import com.example.demo.dao.UsersRepository;
import com.example.demo.model.User;
import org.hibernate.validator.internal.util.stereotypes.Lazy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.text.MessageFormat;
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

    public User getUserByUsername(String username){
        return usersRepository.findByUsername(username);
    }

    /*@Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        final Optional<User> optionalUser = usersRepository.findByUsername(username);

        return (UserDetails) optionalUser.orElseThrow(() -> new UsernameNotFoundException(MessageFormat.format("User with username: {0} cannot be found.", username)));

    }*/

    public void sendMailWithConfirmationCode(String email, String confirmationCode){
        SimpleMailMessage confirmationMessage=new SimpleMailMessage();
        confirmationMessage.setTo(email);
        confirmationMessage.setSubject("Confirmation mail");
        confirmationMessage.setText("Please: http://localhost:8080/confirm/"+confirmationCode);

        emailSenderService.sendEmail(confirmationMessage);
    }
}
