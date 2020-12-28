package com.example.demo.dao;

import com.example.demo.model.UserRole;
import org.springframework.data.repository.CrudRepository;

public interface UserRoleRepository extends CrudRepository<UserRole,Integer> {
    UserRole findByName(String name);
}
