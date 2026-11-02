package com.example.nonprofitmanagementsystem.donation;

import com.example.nonprofitmanagementsystem.UserOrgManagement.Donor;
import java.time.LocalDateTime;

public abstract class Donation {
    private int donationId;
    private Donor donor;
    private double amount;
    private LocalDateTime donationDate;
    private DonationStatus status;
    private String description;

    public Donation(int donationId, Donor donor, double amount, String description) {
        this.donationId = donationId;
        this.donor = donor;
        this.amount = amount;
        this.description = description;
        this.donationDate = LocalDateTime.now();
        this.status = DonationStatus.PENDING;
    }

    public int getDonationId() {
        return donationId;
    }

    public Donor getDonor() {
        return donor;
    }

    public double getAmount() {
        return amount;
    }

    protected void setAmount(double amount){
        this.amount = amount;
    }

    public LocalDateTime getDonationDate() {
        return donationDate;
    }

    public String getDescription() {
        return description;
    }

    public DonationStatus getStatus() {
        return status;
    }

    public void updateStatus(DonationStatus status) {
        this.status = status;
    }

    public abstract DonationType getDonationType();
}