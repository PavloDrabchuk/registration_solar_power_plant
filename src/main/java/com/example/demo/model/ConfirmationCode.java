package com.example.demo.model;

import javax.persistence.*;
import java.time.LocalDate;

@Entity
public class ConfirmationCode {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne
    private User user;
    private String confirmationCode;
    private LocalDate dateOfCreation;

    public ConfirmationCode(User user, String confirmationCode, LocalDate dateOfCreation) {
        this.user=user;
        this.confirmationCode = confirmationCode;
        this.dateOfCreation = LocalDate.now();
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

    public LocalDate getDateOfCreation() {
        return dateOfCreation;
    }

    public void setDateOfCreation(LocalDate dateOfCreation) {
        this.dateOfCreation = dateOfCreation;
    }
}
