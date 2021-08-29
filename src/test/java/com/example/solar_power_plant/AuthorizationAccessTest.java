package com.example.solar_power_plant;

import com.example.solar_power_plant.enums.Region;
import com.example.solar_power_plant.enums.UserRoles;
import com.example.solar_power_plant.model.Location;
import com.example.solar_power_plant.model.SolarPowerPlant;
import com.example.solar_power_plant.model.StaticData;
import com.example.solar_power_plant.model.User;
import org.junit.Test;
import org.springframework.boot.test.context.SpringBootTest;

import static org.assertj.core.api.Assertions.*;


import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;

@SpringBootTest
public class AuthorizationAccessTest {

    @Test
    public void testReturningUsageTime() {
        Location location = new Location(
                "country", Region.IvanoFrankivsk,
                "city", "street", "number", 48.2, 32.5);

        User user = new User(
                "qwerty",
                "name",
                "surname",
                "pass",
                UserRoles.ROLE_USER,
                "example@example.com",
                "+380123456789");

        SolarPowerPlant solarPowerPlant = new SolarPowerPlant(
                "43c2792f-3015-4832-b482-e0c4fbaad086",
                "Spp",
                location,
                user
        );

        StaticData staticData = new StaticData(15, 125,
                LocalDate.parse("2010-06-12", DateTimeFormatter.ofPattern("yyyy-MM-dd")));

        solarPowerPlant.setStaticData(staticData);

        //System.out.println("ll: " + AuthorizationAccess.getUsageTime(solarPowerPlant).get(0));
        ArrayList<Integer> usageTime = AuthorizationAccess.getUsageTime(solarPowerPlant);
        assertThat(usageTime.get(0)).isEqualTo(11);
        assertThat(usageTime.get(1)).isEqualTo(2);
        assertThat(usageTime.get(2)).isEqualTo(17);

    }
}
