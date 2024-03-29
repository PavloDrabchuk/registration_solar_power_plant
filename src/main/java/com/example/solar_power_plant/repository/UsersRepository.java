package com.example.solar_power_plant.repository;

import com.example.solar_power_plant.model.User;
import com.example.solar_power_plant.enums.UserRoles;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import java.util.List;
import java.util.Optional;


public interface UsersRepository extends CrudRepository<User, Long> {
    Optional<User> findByUsername(String username);

    Optional<User> findByEmail(String email);

    Optional<User> findUserById(Long id);

    Optional<User> findUserByUserRole(UserRoles userRoles);

    List<User> findAllByUserRole(UserRoles userRoles);

    @Query(value = "select * from user c order by c.id limit ?1, ?2 ",
            nativeQuery = true)
    List<User> getListUsersForPage(int offset, int row_count);

    @Query(value = "select * from user c where c.username like %?1% order by c.id limit ?2, ?3 ",
            nativeQuery = true)
    List<User> getListUsersByUsernameForPage(String username, int offset, int row_count);

    List<User> getUsersByUsernameContaining(String username);
}
