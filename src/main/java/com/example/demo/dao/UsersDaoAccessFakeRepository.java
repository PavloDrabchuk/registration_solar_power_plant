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

    @Override
    public int updateUserById(Integer id, User updatedUser) {
        /*Optional<User> user = selectUserById(id);
        if (user.isPresent())
            jdbcTemplate.update(
                    "UPDATE `home_library` SET `id` = ?, `name` = ?, `author` = ?,`year` = ?,`publishingHouse` = ?,`countPage` = ? WHERE id = ?",
                    id,
                    (updateBook.getName() == null) ? book.get().getName() : updateBook.getName(),
                    (updateBook.getAuthor() == null) ? book.get().getAuthor() : updateBook.getAuthor(),
                    (updateBook.getYear() == null) ? book.get().getYear() : updateBook.getYear(),
                    (updateBook.getPublishingHouse() == null) ? book.get().getPublishingHouse() : updateBook.getPublishingHouse(),
                    (updateBook.getCountPage() == null) ? book.get().getCountPage() : updateBook.getCountPage(),

                    id
            );*/
        return 1;
    }
}
