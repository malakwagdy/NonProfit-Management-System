package com.example.nonprofitmanagementsystem.donation;

public class CheckDonationCreator extends DonationCreator {
    @Override
    protected Donation createDonation(int donationId, DonationRequest request) {
        return new CheckDonation(donationId, request.getDonor(), request.getAmount(), request.getDescription(), request.getCheckNumber(), request.getBankName());
    }

    @Override
    public DonationType getSupportedType() {
        return DonationType.CHECK;
    }
}
