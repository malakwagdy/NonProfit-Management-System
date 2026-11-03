package com.example.nonprofitmanagementsystem.user;

public class Donor extends User {
    String donationPreference;
    double totalDonated;

    public Donor(int userId, String firstName, String lastName, String email, String phoneNumber, String password, String address) {
        super(userId, firstName, lastName, email, phoneNumber, password, address);
        this.donationPreference = "";
        this.totalDonated = 0.0;
    }
    public String getDonationPreference() {
        return  donationPreference;
    }
    public void updateDonationPreference(String donationPreference) {
        this.donationPreference = donationPreference;
    }
    public double getTotalDonated() {
        return totalDonated;
    }
    public void addToTotal(double totalDonated) {
        this.totalDonated += totalDonated;
    }
    @Override
    public UserRole getRole() {
        return UserRole.DONOR;
    }
}