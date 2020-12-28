package com.example.demo.service;

import com.example.demo.dao.SolarPowerPlantRepository;
import com.example.demo.dao.UserRoleRepository;
import com.example.demo.model.UserRole;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserRoleService {
    private final UserRoleRepository userRoleRepository;

    @Autowired
    public UserRoleService(UserRoleRepository userRoleRepository) {
        this.userRoleRepository = userRoleRepository;
    }

    public UserRole getUserRole(String name) {
        return userRoleRepository.findByName(name);
    }
}
