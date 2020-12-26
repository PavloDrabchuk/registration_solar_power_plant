package com.example.demo.dao;

import com.example.demo.model.User;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Repository
@Qualifier("fakeDao")
public class UsersDaoAccessFakeRepository implements UsersDao {
    private static List<User> userList =new ArrayList<>();

    @Override
    public List<User> selectAllBooks() {
        return userList;
    }

    @Override
    public Optional<User> selectUserById(Integer id) {
        return userList.stream().filter(
                user->user.getId().equals(id)).findFirst();
    }

    @Override
    public int addUser(User user) {
        /*Integer id;
        if (userList.size() == 0) id = 1;
        else id = userList.get(userList.size() - 1)
                .getId() + 1;
        userList.add(new User(
                id,
                user.getName(),
                user.getSurname()));*/
        return 1;
    }
}
