package com.example.demo.model;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
public class DynamicData {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    private SolarPowerPlant solarPowerPlant;

    private Double producedPower;

    private LocalDateTime collectionDateTime;

    public DynamicData(SolarPowerPlant solarPowerPlant, Double producedPower, LocalDateTime collectionDateTime) {
        this.solarPowerPlant = solarPowerPlant;
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
}
