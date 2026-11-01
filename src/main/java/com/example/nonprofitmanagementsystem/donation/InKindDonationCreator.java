package com.example.nonprofitmanagementsystem.donation;

public class InKindDonationCreator extends DonationCreator {
    @Override
    protected Donation createDonation(int donationId, DonationRequest request) {
        return new InKindDonation(donationId, request.getDonor(), request.getDescription(), request.getItems());
    }

    @Override
    public DonationType getSupportedType() {
        return DonationType.IN_KIND;
    }
}
