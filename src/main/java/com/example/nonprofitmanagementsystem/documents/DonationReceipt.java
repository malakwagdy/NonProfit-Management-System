package com.example.nonprofitmanagementsystem.documents;
import com.example.nonprofitmanagementsystem.donation.Donation;

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
    public String generateContent() {
        return "Donation Receipt\n"
                + "Receipt number: " + receiptNumber + "\n"
                + "Donor: " + donation.getDonor().getFullName() + "\n"
                + "Amount: " + donation.getAmount() + "\n"
                + "Date: " + donation.getDonationDate();
    }

    @Override
    public String getDocumentType() {return "DonationReceipt";}
}
