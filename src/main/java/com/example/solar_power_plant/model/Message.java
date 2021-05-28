package com.example.solar_power_plant.model;

import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;
import org.hibernate.annotations.Type;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.UUID;

@Entity
public class Message {
    @Id
    @GeneratedValue(generator = "UUID")
    @Type(type = "org.hibernate.type.UUIDCharType")
    private UUID id;
//    @GeneratedValue(strategy = GenerationType.IDENTITY)
//    private Long id;

    private String title;

    @Column(length = 1024)
    private String text;
    private LocalDateTime dateTime;

    @ManyToOne
    @OnDelete(action = OnDeleteAction.CASCADE)
    private User sender;

    @ManyToOne
    @OnDelete(action = OnDeleteAction.CASCADE)
    private User recipient;

    @OnDelete(action = OnDeleteAction.NO_ACTION)
    private MessageType messageType;
    private Boolean isRead;

    public Message(String title, String text, User sender, User recipient, MessageType messageType, Boolean isRead) {
        //this.id=UUID.fromString(title);
        this.title = title;
        this.text = text;
        this.dateTime = LocalDateTime.now();
        this.sender = sender;
        this.recipient = recipient;
        this.messageType = messageType;
        this.isRead = isRead;
    }

    public Message() {

    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public LocalDateTime getDateTime() {
        return dateTime;
    }

    public void setDateTime(LocalDateTime dateTime) {
        this.dateTime = dateTime;
    }

    public User getSender() {
        return sender;
    }

    public void setSender(User sender) {
        this.sender = sender;
    }

    public User getRecipient() {
        return recipient;
    }

    public void setRecipient(User recipient) {
        this.recipient = recipient;
    }

    public MessageType getMessageType() {
        return messageType;
    }

    public void setMessageType(MessageType messageType) {
        this.messageType = messageType;
    }

    public Boolean getRead() {
        return isRead;
    }

    public void setRead(Boolean read) {
        isRead = read;
    }

    public String getStringDateTime() {
        return (dateTime != null)
                ? dateTime.format(DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm"))
                : null;
    }

    public String getFormattedUsernameAndNameSurname() {
        StringBuilder result = new StringBuilder();

        result.append("Від: ");
        result.append(sender.getUsername());

        if (sender.getName() != null || sender.getSurname() != null) result.append("| ");

        if (sender.getName() != null) result.append(sender.getName()).append(" ");
        if (sender.getSurname() != null) result.append(sender.getSurname());

        return result.toString();
    }

    public String getFormattedUsernameAndNameSurnameOfRecipient() {
        StringBuilder result = new StringBuilder();

        result.append("Кому: ");
        result.append(recipient.getUsername());

        if (recipient.getName() != null || recipient.getSurname() != null) result.append("| ");

        if (recipient.getName() != null) result.append(recipient.getName()).append(" ");
        if (recipient.getSurname() != null) result.append(recipient.getSurname());

        return result.toString();
    }

    public boolean isAnswering() {
        return messageType != MessageType.INFORMATION &&
                messageType != MessageType.UPDATE &&
                messageType != MessageType.ERROR;
    }
}
