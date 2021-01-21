package com.example.demo.model;

import javax.persistence.*;

@Entity
public class SolarPowerPlant {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;
    @OneToOne(cascade = {CascadeType.ALL})
    private Location location;

    private int quantity;

    @ManyToOne
    private User user;

    public SolarPowerPlant(String name, Location location, int quantity, User user) {
        this.name = name;
        this.location = location;
        this.quantity = quantity;
        this.user = user;
    }

    public SolarPowerPlant() {

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

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }
}

