package com.example.demo.dao;

import com.example.demo.model.ConfirmationCode;
import com.example.demo.model.DynamicData;
import com.example.demo.model.SolarPowerPlant;
import com.example.demo.model.User;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;

@Repository
public interface DynamicDataRepository extends CrudRepository<DynamicData, Long> {
    Iterable<DynamicData> findAllBySolarPowerPlant(SolarPowerPlant solarPowerPlant);

    Iterable<DynamicData> findAllByCollectionDateTimeBetweenAndSolarPowerPlant(LocalDateTime startDateTime,
                                                                               LocalDateTime finishDateTime,
                                                                               SolarPowerPlant solarPowerPlant);


}
