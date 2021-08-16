package com.example.solar_power_plant.model;

import com.example.solar_power_plant.enums.Region;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Entity
public class Location {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    @Size(min = 1, max = 10)
    private String country = "Україна";

    @NotNull
    private Region region;

    @NotNull
    @Size(min = 1, max = 60)
    private String city;

    @NotNull
    @Size(min = 1, max = 60)
    private String street;

    @NotNull
    @Size(min = 1, max = 50)
    private String number;

    @NotNull
    private Double longitude;

    @NotNull
    private Double latitude;

    public Location(String country, Region region, String city, String street, String number, Double longitude, Double latitude) {
        this.country = country;
        this.region = region;
        this.city = city;
        this.street = street;
        this.number = number;
        this.longitude = longitude;
        this.latitude = latitude;
    }

    public Location() {

    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getStreet() {
        return street;
    }

    public void setStreet(String street) {
        this.street = street;
    }

    public Double getLongitude() {
        return longitude;
    }

    public void setLongitude(Double longitude) {
        this.longitude = longitude;
    }

    public Double getLatitude() {
        return latitude;
    }

    public void setLatitude(Double latitude) {
        this.latitude = latitude;
    }

    public Region getRegion() {
        return region;
    }

    public void setRegion(Region region) {
        this.region = region;
    }

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    public String getStringLocation() {
        //(region.equals(Region.AutonomousRepublicOfCrimea)) ? ", " : " область, ";
        //return region.getName() + " область, " + city + ", вул. " + street + ", " + number;
        return region.getName()
                + (region.equals(Region.AutonomousRepublicOfCrimea) ? ", " : " область, ")
                + city + ", вул. " + street + ", " + number;
    }
}
