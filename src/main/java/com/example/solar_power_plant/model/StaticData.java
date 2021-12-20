package com.example.solar_power_plant.model;

import org.springframework.format.annotation.DateTimeFormat;

import javax.persistence.*;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import java.sql.Timestamp;
import java.text.ParseException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Entity
public class StaticData {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    @Min(1)
    private Integer quantity;

    @NotNull
    @Min(1)
    private Integer power;

    @DateTimeFormat(pattern = "yyyy-MM-dd", iso = DateTimeFormat.ISO.DATE_TIME)
    private LocalDate installationDate;

    @OneToOne(cascade = {CascadeType.ALL})
    @Transient
    private SolarPowerPlant solarPowerPlant;

    public StaticData(Integer quantity, Integer power, LocalDate installationDate, SolarPowerPlant solarPowerPlant) {
        this.quantity = quantity;
        this.power = power;
        this.installationDate = installationDate;
    }

    public StaticData(Integer quantity, Integer power, LocalDate installationDate) {
        this.quantity = quantity;
        this.power = power;
        this.installationDate = installationDate;
    }

    public StaticData() {

    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }

    public Integer getPower() {
        return power;
    }

    public void setPower(Integer power) {
        this.power = power;
    }

    public LocalDate getInstallationDate() {
        return installationDate;
    }

    public void setInstallationDate(String installationDate) throws ParseException {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        this.installationDate = LocalDate.parse(installationDate, formatter);
    }

    public SolarPowerPlant getSolarPowerPlant() {
        return solarPowerPlant;
    }

    public void setSolarPowerPlant(SolarPowerPlant solarPowerPlant) {
        this.solarPowerPlant = solarPowerPlant;
    }

    public Timestamp getTimestampInstallationDate() {
        return Timestamp.valueOf(String.valueOf(installationDate));
    }

    public String getStringInstallationDate() {
        return LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
    }
}
