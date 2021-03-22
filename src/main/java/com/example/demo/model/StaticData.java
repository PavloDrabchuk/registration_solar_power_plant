package com.example.demo.model;

import javax.persistence.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

@Entity
public class StaticData {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Integer quantity;

    private Date installationDate;

    @OneToOne(cascade = {CascadeType.ALL})
    @Transient
    private SolarPowerPlant solarPowerPlant;

    public StaticData(Integer quantity, Date installationDate, SolarPowerPlant solarPowerPlant) {
        this.quantity = quantity;
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
}
