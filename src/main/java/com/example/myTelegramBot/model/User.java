package com.example.myTelegramBot.model;



import jakarta.persistence.Entity;
import jakarta.persistence.Id;

import java.sql.Timestamp;

@Entity(name = "usersDataTable")
public class User {
    @Id
    private Long chatID;

    private String firstName;

    private String lastName;


    private String userName;

    private Timestamp registeredAt;



    public Long getChatID() {
        return chatID;
    }

    public void setChatID(Long chatID) {
        this.chatID = chatID;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public Timestamp getRegisteredAt() {
        return registeredAt;
    }

    public void setRegisteredAt(Timestamp registeredAt) {
        this.registeredAt = registeredAt;
    }

    @Override
    public String toString() {
        return firstName+"," + lastName+"{" + '\n' +
                "chatID=" + chatID + + ','+ '\n' +
                "firstName='" + firstName + ','+ '\n' +
                "lastName='" + lastName + ','+ '\n' +
                "userName='" + userName + ','+ '\n' +
                "registeredAt=" + registeredAt +
                '}';
    }
}
