package com.example.solar_power_plant;

import com.example.solar_power_plant.dao.UsersRepository;
import com.example.solar_power_plant.enums.UserRoles;
import com.example.solar_power_plant.model.User;
import com.example.solar_power_plant.security.WebConfig;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.AutoConfigurationPackage;
import org.springframework.boot.test.autoconfigure.core.AutoConfigureCache;
import org.springframework.boot.test.autoconfigure.orm.jpa.AutoConfigureDataJpa;
import org.springframework.boot.test.autoconfigure.orm.jpa.AutoConfigureTestEntityManager;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;
//import static org.hamcrest.MatcherAssert.assertThat;
import static org.assertj.core.api.Assertions.*;

import java.util.Optional;


@DataJpaTest
@AutoConfigurationPackage
public class UserRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private UsersRepository usersRepository;

    @Test
    public void testWithUserEntity() {
        User user = new User(
                "qwerty",
                "name",
                "surname",
                "pass",
                UserRoles.ROLE_USER,
                "example@example.com",
                "+380123456789");
        entityManager.persist(user);
        entityManager.flush();

        Optional<User> found = usersRepository.findByUsername(user.getUsername());

        if(found.isPresent()) {
            assertThat(found.get().getUsername()).isEqualTo(user.getUsername());
        }

        /*Optional<User> user=this.usersRepository.findByUsername("qwerty");
        //assertThat(user.get().getUsername()).isEqualTo("sboot");
        if(user.isPresent()) {
            assertThat((user.get().getUsername()).equals("sboot"), true);
            //assertThat(user.getVin()).isEqualTo("1234");
        }*/

        // given

    }
}
