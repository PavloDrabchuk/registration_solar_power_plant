package com.example.demo.dao;

import com.example.demo.model.SolarPowerPlant;
import com.example.demo.model.User;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface SolarPowerPlantRepository extends CrudRepository<SolarPowerPlant, Long> {
    Iterable<SolarPowerPlant> findAllByUser(User user);

    Optional<SolarPowerPlant> findById(Long id);

    Optional<SolarPowerPlant> findByStringId(String stringId);

    //List<SolarPowerPlant> find2ByUserAndIdBetween(User user, Long offset, Long limit);

    @Query(value = "select * from solar_power_plant c where c.user_id = ?1 order by c.id limit ?2, ?3 ",
            nativeQuery = true)
    List<SolarPowerPlant> getListSolarPowerPlantForPage(Long id, int offset, int row_count);

    @Query(value = "select * from solar_power_plant c order by c.id limit ?1, ?2 ",
            nativeQuery = true)
    List<SolarPowerPlant> getListOfAllSolarPowerPlantForPage(int offset, int row_count);

    Integer countAllByUser(User user);

    //List<User> getUsersByUsernameContaining(String username);
    List<SolarPowerPlant> getSolarPowerPlantByNameContaining(String name);

    @Query(value = "select * from solar_power_plant c where c.name like %?1% order by c.id limit ?2, ?3 ",
            nativeQuery = true)
    List<SolarPowerPlant> getListSolarPowerPlantsByNameForPage(String name, int offset, int row_count);
}
