package com.example.demo.model;

import javax.persistence.*;

@Entity
public class SolarPowerPlant {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Integer id;

    private String name;
    @OneToOne(cascade = {CascadeType.ALL})
    private Location location;

    private int number;

    @ManyToOne
    private User user;

    public SolarPowerPlant(String name, Location location, int number, User user) {
        this.name = name;
        this.location = location;
        this.number = number;
        this.user = user;
    }

    public SolarPowerPlant() {

    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
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

    public int getNumber() {
        return number;
    }

    public void setNumber(int number) {
        this.number = number;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }
}

