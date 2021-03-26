package com.example.demo.dao;

import com.example.demo.model.User;
import org.springframework.data.repository.CrudRepository;

import java.util.Optional;


public interface UsersRepository extends CrudRepository<User,Long> {
    Optional<User> findByUsername(String username);

    Optional<User> findByEmail(String email);

}
