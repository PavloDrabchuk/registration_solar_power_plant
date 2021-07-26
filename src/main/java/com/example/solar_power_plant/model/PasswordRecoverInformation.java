package com.example.solar_power_plant.model;

public class PasswordRecoverInformation {
    private String username;
    private String email;

    public PasswordRecoverInformation(String username, String email) {
        this.username = username;
        this.email = email;
    }

    public PasswordRecoverInformation() {
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}
