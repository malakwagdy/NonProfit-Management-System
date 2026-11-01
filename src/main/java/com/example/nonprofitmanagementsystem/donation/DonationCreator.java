package com.example.nonprofitmanagementsystem.donation;

public abstract class DonationCreator {
    private static int nextDonationId = 1;

    public Donation newDonation(DonationRequest request){
        if (!validateRequest(request)) {
            throw new IllegalArgumentException("Invalid donation request");
        }
        int donationId = generateDonationId();
        Donation donation = createDonation(donationId, request);
        donation.updateStatus(DonationStatus.PENDING);
        return donation;
    }

    protected abstract Donation createDonation(int donationId, DonationRequest request);

    public abstract DonationType getSupportedType();

    private boolean validateRequest(DonationRequest request) {
        if (request == null) return false;
        if (request.getType() != getSupportedType()) return false;
        if (request.getDonor() == null) return false;
        if (!request.getDonor().isActive()) return false;
        return true;
    }

    private static int generateDonationId() {
        return nextDonationId++;
    }
}
