package com.example.demo.dao;

import com.example.demo.model.SolarPowerPlant;
import com.example.demo.model.User;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import java.util.List;
import java.util.Optional;

public interface SolarPowerPlantRepository extends CrudRepository<SolarPowerPlant, Long> {
    Iterable<SolarPowerPlant> findAllByUser(User user);

    Optional<SolarPowerPlant> findById(Long id);

    //List<SolarPowerPlant> find2ByUserAndIdBetween(User user, Long offset, Long limit);


    @Query(value = "select * from solar_power_plant c order by c.id limit ?1, ?2",
            nativeQuery = true)
    List<SolarPowerPlant> getListSolarPowerPlantForPage(int offset, int row_count);
}
