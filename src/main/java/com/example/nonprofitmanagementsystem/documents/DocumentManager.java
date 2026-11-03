package com.example.nonprofitmanagementsystem.documents;
import java.util.List;
import java.util.ArrayList;
import com.example.nonprofitmanagementsystem.config.ConfigurationManager;
import com.example.nonprofitmanagementsystem.donation.DonationManager;
import com.example.nonprofitmanagementsystem.donation.Donation;
import com.example.nonprofitmanagementsystem.user.Volunteer;
import com.example.nonprofitmanagementsystem.user.Donor;
import com.example.nonprofitmanagementsystem.donation.DonationStatus;

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

        receipt.refreshContent();
        documents.add(receipt);
        return receipt;
    }

    public VolunteerCertificate generateVolunteerCertificate(Volunteer volunteer) {
        VolunteerCertificate certificate = new VolunteerCertificate(
                nextDocumentID++,
                volunteer,
                volunteer.getTotalHoursVolunteered()
        );

        certificate.refreshContent();
        documents.add(certificate);
        return certificate;
    }

    public TaxDocument generateTaxDocument(Donor donor, int taxYear) {
        double total = 0;

        for (Donation donation : donationManager.getDonationsByDonorAndYear(
                donor.getUserId(), taxYear)) {
            if (donation.getStatus() == DonationStatus.PROCESSED) {
                total += donation.getAmount();
            }
        }

        TaxDocument document = new TaxDocument(
                nextDocumentID++, donor, taxYear, total
        );

        document.refreshContent();
        documents.add(document);
        return document;
    }

    public Document getDocumentById(int documentId) {
        for (Document document : documents) {
            if (document.getDocumentId() == documentId) {
                return document;
            }
        }
        System.out.println("Document not found");
        return null;
    }

    public List<Document> getAllDocuments() {return documents;}

    public boolean deleteDocument(int documentId) {
        for (int i = 0; i < documents.size(); i++) {
            if (documents.get(i).getDocumentId() == documentId) {
                documents.remove(i);
                return true;
            }
        }
        return false;
    }

}
