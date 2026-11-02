package com.example.nonprofitmanagementsystem.UserOrgManagement;

import java.time.LocalDateTime;

public abstract class User {
    private int userId;
    private String firstName;
    private String lastName;
    private String email;
    private String phoneNumber;
    private String passwordHash;
    private String address;
    private LocalDateTime registrationDate;
    private boolean active;
    private String preferredChannelId;

    public User(int userId, String firstName, String lastName, String email, String phoneNumber, String passwordHash, String address) {
        this.userId = userId;
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.phoneNumber = phoneNumber;
        this.passwordHash = passwordHash;
        this.address = address;
        this.registrationDate = LocalDateTime.now();
        this.active = true;
        this.preferredChannelId = new String();
    }

    public int getUserId() {
        return userId;
    }
    public String getFullName() {
        return firstName + " " + lastName;
    }

    public String getEmail() {
        return email;
    }
    public String getPhoneNumber() {
        return phoneNumber;
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

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getPasswordHash() {
        return passwordHash;
    }

    public void setPasswordHash(String passwordHash) {
        this.passwordHash = passwordHash;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public LocalDateTime getRegistrationDate() {
        return registrationDate;
    }

    public void setRegistrationDate(LocalDateTime registrationDate) {
        this.registrationDate = registrationDate;
    }

    public boolean isActive() {
        return active;
    }

    public String getPreferredChannelId() {
        return preferredChannelId;
    }

    public void setPreferredChannelId(String preferredChannelId) {
        this.preferredChannelId = preferredChannelId;
    }
    public void updateProfile(String firstName, String lastName, String email, String phoneNumber, String address) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.phoneNumber = phoneNumber;
        this.address = address;
    }
    public void changePassword(String newPasswordHash) {
        this.passwordHash = newPasswordHash;
    }
    public void deactivateAccount() {
        this.active = false;
    }
    public void activateAccount() {
        this.active = true;
    }
}
