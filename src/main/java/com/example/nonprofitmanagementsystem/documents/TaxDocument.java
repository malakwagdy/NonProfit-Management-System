package com.example.nonprofitmanagementsystem.documents;
import com.example.nonprofitmanagementsystem.UserOrgManagement.Donor;

public class TaxDocument extends Document {
    private Donor donor;
    private int taxYear;
    private double totalEligibleDonations;

    public TaxDocument(int documentId, Donor donor, int taxYear, double totalEligibleDonations) {
        super(documentId);
        this.donor = donor;
        this.taxYear = taxYear;
        this.totalEligibleDonations = totalEligibleDonations;
    }

    public Donor getDonor() {return donor;}

    public int getTaxYear() {return taxYear;}

    public double getTotalEligibleDonations() {return totalEligibleDonations;}

    @Override
    public String generateContent() {return "";}

    @Override
    public String getDocumentType() {return "TaxDocument";}
}