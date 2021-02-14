package com.example.demo.model;

import javax.persistence.*;
import java.util.UUID;

@Entity
public class SolarPowerPlant {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String stringId;

    private String name;

    @OneToOne(cascade = {CascadeType.ALL})
    private Location location;

    @OneToOne(cascade = {CascadeType.ALL})
    private StaticData staticData;

    //private int quantity;

    @ManyToOne
    private User user;

    public SolarPowerPlant(String stringId,String name, Location location, User user) {
        this.stringId=stringId;
        this.name = name;
        this.location = location;
        //this.quantity = quantity;
        this.user = user;
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

    /*public int getQuantity() {
        return quantity;
    }*/

    /*public void setQuantity(int quantity) {
        this.quantity = quantity;
    }*/

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
}

