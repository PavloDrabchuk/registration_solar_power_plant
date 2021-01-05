package com.example.demo.dao;

import com.example.demo.model.SolarPowerPlant;
import com.example.demo.model.User;
import org.springframework.data.repository.CrudRepository;

import java.util.Optional;

public interface SolarPowerPlantRepository extends CrudRepository<SolarPowerPlant,Long> {
    Iterable<SolarPowerPlant> findAllByUser(User user);
    Optional<SolarPowerPlant> findById(Long id);
}
