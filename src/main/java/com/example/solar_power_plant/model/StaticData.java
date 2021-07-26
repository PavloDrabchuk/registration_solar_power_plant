package com.example.solar_power_plant.model;

import org.springframework.format.annotation.DateTimeFormat;

import javax.persistence.*;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;

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

    //@DateTimeFormat(pattern = "yyyy-MM-dd")
    @NotNull(message = "Виберіть дату встановлення")
    private Date installationDate;

    @OneToOne(cascade = {CascadeType.ALL})
    @Transient
    private SolarPowerPlant solarPowerPlant;

    public StaticData(Integer quantity,Integer power, Date installationDate, SolarPowerPlant solarPowerPlant) {
        this.quantity = quantity;
        this.power=power;
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

    public Date getInstallationDate() {
        return installationDate;
    }

    /*public void setInstallationDate(Date installationDate) {
        this.installationDate = installationDate;
    }*/

    public void setInstallationDate(String installationDate) throws ParseException {
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");

        this.installationDate = formatter.parse(installationDate);
    }

    public SolarPowerPlant getSolarPowerPlant() {
        return solarPowerPlant;
    }

    public void setSolarPowerPlant(SolarPowerPlant solarPowerPlant) {
        this.solarPowerPlant = solarPowerPlant;
    }

    public Timestamp getTimestampInstallationDate(){
        return Timestamp.valueOf(String.valueOf(installationDate));
    }

    public String getStringInstallationDate(){
        return LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
    }
}
