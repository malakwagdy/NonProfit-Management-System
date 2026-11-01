package com.example.nonprofitmanagementsystem.donation;

public class OnlineDonationCreator extends DonationCreator{
    @Override
    protected Donation createDonation(int donationId, DonationRequest request) {
        return new OnlineDonation(donationId, request.getDonor(), request.getAmount(), request.getDescription(), request.getPaymentPlatform(), request.getTransactionId());
    }

    @Override
    public DonationType getSupportedType() {
        return DonationType.ONLINE;
    }
}
