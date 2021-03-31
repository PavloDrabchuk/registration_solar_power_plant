package com.example.demo.dao;

import com.example.demo.model.ConfirmationCode;
import com.example.demo.model.DynamicData;
import com.example.demo.model.SolarPowerPlant;
import com.example.demo.model.User;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface DynamicDataRepository extends CrudRepository<DynamicData, Long> {
    Iterable<DynamicData> findAllBySolarPowerPlant(SolarPowerPlant solarPowerPlant);

    Iterable<DynamicData> findAllByCollectionDateTimeBetweenAndSolarPowerPlant(LocalDateTime startDateTime,
                                                                               LocalDateTime finishDateTime,
                                                                               SolarPowerPlant solarPowerPlant);

    @Query(value = "select SUM(d.produced_power) from dynamic_data d where d.solar_power_plant_id = ?1",
            nativeQuery = true)
    Double getTotalPower(Long solarPowerPlantId);

    @Query(value = "select SUM(d.produced_power) from dynamic_data d where d.collection_date_time between ?1 and ?2 and d.solar_power_plant_id = ?3",
            nativeQuery = true)
    Double getTotalPowerForLastThirtyDays(LocalDateTime startDate,
                                          LocalDateTime finishDate,
                                          Long solarPowerPlantId);
}
