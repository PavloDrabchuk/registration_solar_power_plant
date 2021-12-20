package com.example.solar_power_plant.model;

import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

@Entity
public class SolarPowerPlant {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String stringId;

    @NotNull
    @Size(min = 2, max = 30)
    private String name;

    @OneToOne(cascade = {CascadeType.ALL})
    private Location location;

    @OneToOne(cascade = {CascadeType.ALL})
    private StaticData staticData;

    private LocalDateTime registrationDateTime;

    @ManyToOne
    @OnDelete(action = OnDeleteAction.CASCADE)
    private User user;

    public SolarPowerPlant(String stringId, String name, Location location, User user) {
        this.stringId = stringId;
        this.name = name;
        this.location = location;
        this.user = user;
        this.registrationDateTime = LocalDateTime.now(ZoneId.of("UTC"));
    }

    public SolarPowerPlant() {

    }

    public String getStringId() {
        return stringId;
    }

    public void setStringId(String stringId) {
        this.stringId = stringId;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Location getLocation() {
        return location;
    }

    public void setLocation(Location location) {
        this.location = location;
    }

    public StaticData getStaticData() {
        return staticData;
    }

    public void setStaticData(StaticData staticData) {
        this.staticData = staticData;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public void setRegistrationDateTime(LocalDateTime registrationDateTime) {
        this.registrationDateTime = registrationDateTime;
    }

    public LocalDateTime getRegistrationDateTime() {
        return registrationDateTime;
    }

    public String getStringRegistrationDateTime() {
        return (registrationDateTime != null)
                ? registrationDateTime.format(DateTimeFormatter.ofPattern("dd.MM.yyyy"))
                : null;
    }
}

