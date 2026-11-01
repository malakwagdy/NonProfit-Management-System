package com.example.nonprofitmanagementsystem.donation;
import java.util.List;

public class DonationRequest {
    private DonationType type;
    private Donor donor;
    private double amount;
    private String description;
    // Online info
    private String paymentPlatform;
    private String transactionId;
    // Check info
    private String checkNumber;
    private String bankName;
    // In kind info
    private List<DonatedItem> items;

    public DonationRequest(DonationType type, Donor donor, double amount, String description) {
        this.type = type;
        this.donor = donor;
        this.amount = amount;
        this.description = description;
    }

    public void setOnlineDetails(String paymentPlatform, String transactionId) {
        this.paymentPlatform = paymentPlatform;
        this.transactionId = transactionId;
    }

    public void setCheckDetails(String checkNumber, String bankName) {
        this.checkNumber = checkNumber;
        this.bankName = bankName;
    }

    public void setInKindDetails(List<DonatedItem> items) {
        this.items = items;
    }

    public DonationType getType() {
        return type;
    }

    public Donor getDonor() {
        return donor;
    }

    public double getAmount() {
        return amount;
    }

    public String getDescription() {
        return description;
    }

    public String getPaymentPlatform() {
        return paymentPlatform;
    }

    public String getTransactionId() {
        return transactionId;
    }

    public String getCheckNumber() {
        return checkNumber;
    }

    public String getBankName() {
        return bankName;
    }

    public List<DonatedItem> getItems() {
        return items;
    }
}