package com.example.demo.dao;

import com.example.demo.model.User;

import java.util.List;
import java.util.Optional;

public interface UsersDao {

    List<User> selectAllBooks();
    Optional<User> selectUserById(Integer id);

    int addUser(User user);
    int updateUserById(Integer id, User updatedUser);
}
