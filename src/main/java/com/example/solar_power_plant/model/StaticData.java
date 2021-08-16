package com.example.solar_power_plant.model;

import org.springframework.format.annotation.DateTimeFormat;

import javax.persistence.*;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
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

//    @NotNull(message = "Виберіть дату встановлення")
    //    @DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm")iso = DateTimeFormat.ISO.DATE_TIME
    @DateTimeFormat(pattern = "yyyy-MM-dd",iso = DateTimeFormat.ISO.DATE_TIME)
    private LocalDate installationDate;

    @OneToOne(cascade = {CascadeType.ALL})
    @Transient
    private SolarPowerPlant solarPowerPlant;

    public StaticData(Integer quantity, Integer power, LocalDate installationDate, SolarPowerPlant solarPowerPlant) {
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

    /*public void setInstallationDate(Date installationDate) {
        this.installationDate = installationDate;
    }*/

    public void setInstallationDate(String installationDate) throws ParseException {
        //SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");

        //this.installationDate = formatter.parse(installationDate);

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        //formatter = formatter.withLocale(  );  // Locale specifies human language for translating, and cultural norms for lowercase/uppercase and abbreviations and such. Example: Locale.US or Locale.CANADA_FRENCH
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
