package com.example.solar_power_plant.repository;

import com.example.solar_power_plant.model.Location;
import org.springframework.data.repository.CrudRepository;

public interface LocationRepository extends CrudRepository<Location, Long> {
}
