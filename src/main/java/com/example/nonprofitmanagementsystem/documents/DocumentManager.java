package com.example.nonprofitmanagementsystem.documents;
import java.util.List;
import java.util.ArrayList;
import com.example.nonprofitmanagementsystem.config.ConfigurationManager;
import com.example.nonprofitmanagementsystem.donation.DonationManager;
import com.example.nonprofitmanagementsystem.donation.Donation;
import com.example.nonprofitmanagementsystem.user.Volunteer;
import com.example.nonprofitmanagementsystem.user.Donor;

public class DocumentManager {
    private List<Document> documents = new ArrayList<>();
    private DonationManager donationManager;
    private int nextDocumentID;

    public DocumentManager(DonationManager donationManager) {this.donationManager = donationManager;}

    public DonationReceipt generateDonationReceipt(Donation donation) {
        for (Document document : documents) {
            if (document instanceof DonationReceipt) {
                DonationReceipt receipt = (DonationReceipt) document;

                if (receipt.getDonation() == donation) {
                    return receipt;
                }
            }
        }

        int id = nextDocumentID++;
        String prefix = ConfigurationManager.getInstance().getReceiptPrefix();

        DonationReceipt receipt =
                new DonationReceipt(id, donation, prefix + id);

        documents.add(receipt);
        return receipt;
    }

    public VolunteerCertificate generateVolunteerCertificate(Volunteer volunteer) {return null;}

    public TaxDocument generateTaxDocument(Donor donor, int taxYear) {return null;}

    public Document getDocumentById(int documentId) {return null;}

    public List<Document> getAllDocuments() {return documents;}

    public boolean deleteDocument(int documentId) {return false;}

}
