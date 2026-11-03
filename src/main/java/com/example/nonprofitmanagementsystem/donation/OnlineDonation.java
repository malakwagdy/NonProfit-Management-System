package com.example.nonprofitmanagementsystem.donation;

import com.example.nonprofitmanagementsystem.user.Donor;

public class OnlineDonation extends Donation {
    private String paymentPlatform;
    private String transactionId;
    private String paymentReference;


    public OnlineDonation(int donationId, Donor donor,  double amount, String description, String paymentPlatform, String transactionId) {
        super(donationId, donor, amount, description);
        this.paymentPlatform = paymentPlatform;
        this.transactionId = transactionId;
    }
    public String getPaymentPlatform() {
        return paymentPlatform;
    }

    public String getTransactionId() {
        return transactionId;
    }

    public String getPaymentReference() {
        return paymentReference;
    }

    public void setPaymentReference(String paymentReference) {
        this.paymentReference = paymentReference;
    }

    public DonationType getDonationType() {
        return DonationType.ONLINE;
    }
}