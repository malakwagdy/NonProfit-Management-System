package com.example.nonprofitmanagementsystem.documents;

public class DonationReceipt extends Document {
    private Donation donation;
    private String receiptNumber;

    public DonationReceipt(int documentId, Donation donation, String receiptNumber) {
        super(documentId);
        this.donation = donation;
        this.receiptNumber = receiptNumber;
    }

    public Donation getDonation() {return donation;}

    public String getReceiptNumber() {return receiptNumber;}

    @Override
    public String generateContent() {return "";}

    @Override
    public String getDocumentType() {return "DonationReceipt";}
}
