package com.example.nonprofitmanagementsystem.donation;

public interface DonationStrategy {
    public boolean validateDonation(Donation donation);
    public boolean processDonation(Donation donation);
    public DonationType getSupportedType();
}
