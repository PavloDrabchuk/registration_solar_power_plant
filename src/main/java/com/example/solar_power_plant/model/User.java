package com.example.solar_power_plant.model;

import com.example.solar_power_plant.enums.UserRoles;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import javax.persistence.*;
import javax.validation.constraints.Email;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

@Entity
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String username;

    private String name;
    private String surname;

    @Email(message = "Введіть правильну адресу електронної пошти")
    private String email;
    private String password;
    private String mobilePhoneNumber;

    @Transient
    private String passwordConfirm;

    private Boolean activated = false;
    private Boolean locked = false;

    private LocalDateTime dateTimeOfCreation = LocalDateTime.now();

    @OnDelete(action = OnDeleteAction.NO_ACTION)
    private UserRoles userRole = UserRoles.ROLE_USER;

    public User(
            @JsonProperty("username") String username,
            @JsonProperty("name") String name,
            @JsonProperty("surname") String surname,
            @JsonProperty("password") String password,
            @JsonProperty("userRoles") UserRoles userRole,
            @JsonProperty("email") String email,
            @JsonProperty("mobilePhoneNumber") String mobilePhoneNumber) {
        this.username = username;
        this.name = name;
        this.surname = surname;
        this.password = password;
        this.userRole = userRole;
        this.email = email;
        this.mobilePhoneNumber = mobilePhoneNumber;
        this.dateTimeOfCreation = LocalDateTime.now(ZoneId.of("UTC"));
    }

    public User() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSurname() {
        return surname;
    }

    public void setSurname(String surname) {
        this.surname = surname;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getPasswordConfirm() {
        return passwordConfirm;
    }

    public void setPasswordConfirm(String passwordConfirm) {
        this.passwordConfirm = passwordConfirm;
    }

    public UserRoles getUserRole() {
        return userRole;
    }

    public void setUserRole(UserRoles userRole) {
        this.userRole = userRole;
    }

    public Boolean getActivated() {
        return activated;
    }

    public void setActivated(Boolean activated) {
        this.activated = activated;
    }

    public Boolean getLocked() {
        return locked;
    }

    public void setLocked(Boolean locked) {
        this.locked = locked;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void getStringInfo() {
        System.out.println("\nUser info: \n" +
                "id: " + id +
                "\nusername: " + username +
                "\nname: " + name +
                "\nsurname: " + surname +
                "\npassword: " + password +
                "\nemail: " + email +
                "\nuserRoles: " + userRole +
                "\nmobilePhoneNumber: " + mobilePhoneNumber);
    }

    public String getMobilePhoneNumber() {
        return mobilePhoneNumber;
    }

    public void setMobilePhoneNumber(String mobilePhoneNumber) {
        this.mobilePhoneNumber = mobilePhoneNumber;
    }

    public LocalDateTime getDateTimeOfCreation() {
        return dateTimeOfCreation;
    }

    public void setDateTimeOfCreation(LocalDateTime dateTimeOfCreation) {
        this.dateTimeOfCreation = dateTimeOfCreation;
    }

    public String getStringRegistrationDateTime() {
        return (dateTimeOfCreation != null)
                ? dateTimeOfCreation.format(DateTimeFormatter.ofPattern("dd.MM.yyyy"))
                : null;
    }

    public String getAccountStatus() {
        if (locked) {
            return "Заблокований";
        } else {
            return activated ? "Активований" : "Не активований";
        }
    }
}
