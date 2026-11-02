package com.example.nonprofitmanagementsystem.donation;
import com.example.nonprofitmanagementsystem.UserOrgManagement.Donor;

import java.time.LocalDate;

public class CheckDonation extends Donation{
    private String checkNumber;
    private String bankName;
    private LocalDate clearanceDate;
    private boolean cleared;

    public CheckDonation(int donationId, Donor donor, double amount, String description, String checkNumber, String bankName) {
        super(donationId, donor, amount, description);
        this.checkNumber = checkNumber;
        this.bankName = bankName;
    }

    public String getCheckNumber() {
        return checkNumber;
    }

    public String getBankName() {
        return bankName;
    }

    public void markAsCleared(LocalDate clearanceDate) {
        this.cleared = true;
        this.clearanceDate = clearanceDate;
    }

    public boolean isCleared() {
        return cleared;
    }

    public DonationType getDonationType() {
        return DonationType.CHECK;
    }
}
