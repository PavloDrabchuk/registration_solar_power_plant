package com.example.demo.dao;

import com.example.demo.model.SolarPowerPlant;
import com.example.demo.model.User;
import org.springframework.data.repository.CrudRepository;

public interface SolarPowerPlantRepository extends CrudRepository<SolarPowerPlant,Integer> {
    Iterable<SolarPowerPlant> findAllByUser(User user);
}
