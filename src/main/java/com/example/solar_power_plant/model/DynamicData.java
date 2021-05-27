package com.example.solar_power_plant.model;

import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
public class DynamicData {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
//    @Cascade(value = {  CascadeType.ALL })
    @OnDelete(action = OnDeleteAction.CASCADE)
    private SolarPowerPlant solarPowerPlant;

    private Weather weather;

    private Double producedPower;

    private LocalDateTime collectionDateTime;

    public DynamicData(SolarPowerPlant solarPowerPlant, Weather weather, Double producedPower, LocalDateTime collectionDateTime) {
        this.solarPowerPlant = solarPowerPlant;
        this.weather = weather;
        this.producedPower = producedPower;
        this.collectionDateTime = collectionDateTime;
    }

    public DynamicData() {

    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public SolarPowerPlant getSolarPowerPlant() {
        return solarPowerPlant;
    }

    public void setSolarPowerPlant(SolarPowerPlant solarPowerPlant) {
        this.solarPowerPlant = solarPowerPlant;
    }

    public Double getProducedPower() {
        return producedPower;
    }

    public void setProducedPower(Double producedPower) {
        this.producedPower = producedPower;
    }

    public LocalDateTime getCollectionDateTime() {
        return collectionDateTime;
    }

    public void setCollectionDateTime(LocalDateTime collectionDateTime) {
        this.collectionDateTime = collectionDateTime;
    }

    public Weather getWeather() {
        return weather;
    }

    public void setWeather(Weather weather) {
        this.weather = weather;
    }
}