package com.example.solar_power_plant.repository;

import com.example.solar_power_plant.model.DynamicData;
import com.example.solar_power_plant.model.SolarPowerPlant;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

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

    @Query(value = "select avg(q.s) from (select SUM(d.produced_power) as s from dynamic_data d where d.collection_date_time between ?1 and ?2 and d.solar_power_plant_id = ?3 group by DAY(d.collection_date_time) ) q",
            nativeQuery = true)
    Double getAveragePowerPerDay(LocalDateTime startDate,
                                 LocalDateTime finishDate,
                                 Long solarPowerPlantId);

    @Query(value = "select month(d.collection_date_time) as `period`, SUM(d.produced_power) as `total` from dynamic_data d " +
            "where d.solar_power_plant_id = ?1 and (d.collection_date_time between ?2 and ?3)" +
            "group by month (d.collection_date_time)",
            nativeQuery = true)
    List<DataByPeriodAndSolarPowerPlant> getDataByMonthAndSolarPowerPlant(Long solarPowerPlantId,
                                                                          LocalDateTime startDay,
                                                                          LocalDateTime finishDay);

    @Query(value = "select hour(d.collection_date_time) as `period`, AVG(d.produced_power) as `total` from dynamic_data d " +
            "where d.solar_power_plant_id = ?1 and (d.collection_date_time between ?2 and ?3)" +
            "group by hour (d.collection_date_time)",
            nativeQuery = true)
    List<DataByPeriodAndSolarPowerPlant> getDataByHourAndSolarPowerPlant(Long solarPowerPlantId,
                                                                         LocalDateTime startDay,
                                                                         LocalDateTime finishDay);

    Optional<DynamicData> findFirstBySolarPowerPlant(SolarPowerPlant solarPowerPlant);
}
