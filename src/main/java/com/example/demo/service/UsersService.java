package com.example.demo.service;

import com.example.demo.dao.UsersDao;
import com.example.demo.dao.UsersRepository;
import com.example.demo.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class UsersService {
    private final UsersDao usersDao;
    private final UsersRepository usersRepository;

    @Autowired
    public UsersService(@Qualifier("fakeDao") UsersDao usersDao,
    UsersRepository usersRepository){
        this.usersDao=usersDao;
        this.usersRepository=usersRepository;
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

    public void addUser(User user){
        //usersRepository.save(new User(user.getName(), user.getSurname()));
        usersRepository.save(user);
    }
}
