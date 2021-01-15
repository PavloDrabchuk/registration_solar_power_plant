package com.example.demo.dao;

import com.example.demo.model.UserRole;
import org.springframework.data.repository.CrudRepository;

import java.util.Optional;

public interface UserRoleRepository extends CrudRepository<UserRole,Long> {
    Optional<UserRole> findByName(String name);
}
