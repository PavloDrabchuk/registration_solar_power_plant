package com.example.demo.model;

import javax.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
public class ConfirmationCode {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne
    private User user;
    private String confirmationCode;
    private Boolean valid;
    private LocalDateTime dateTimeOfCreation;

    public ConfirmationCode(User user, String confirmationCode) {
        this.user=user;
        this.valid=true;
        this.confirmationCode = confirmationCode;
        this.dateTimeOfCreation = LocalDateTime.now();
    }

    public ConfirmationCode() {

    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public String getConfirmationCode() {
        return confirmationCode;
    }

    public void setConfirmationCode(String confirmationCode) {
        this.confirmationCode = confirmationCode;
    }

    public LocalDateTime getDateOfCreation() {
        return dateTimeOfCreation;
    }

    public void setDateTimeOfCreation(LocalDateTime dateTimeOfCreation) {
        this.dateTimeOfCreation = dateTimeOfCreation;
    }

    public Boolean getValid() {
        return valid;
    }

    public void setValid(Boolean valid) {
        this.valid = valid;
    }
}
