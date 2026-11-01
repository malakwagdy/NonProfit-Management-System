package com.example.nonprofitmanagementsystem.donation;

public class OnlineDonationStrategy implements DonationStrategy {
    private String paymentGatewayName;

    public OnlineDonationStrategy(String paymentGatewayName) {
        this.paymentGatewayName = paymentGatewayName;
    }

    @Override
    public boolean validateDonation(Donation donation) {
        if (!(donation instanceof OnlineDonation)) return false;
        OnlineDonation online = (OnlineDonation) donation;
        return donation.getAmount() > 0
                && online.getPaymentPlatform() != null && !online.getPaymentPlatform().isEmpty()
                && online.getTransactionId() != null && !online.getTransactionId().isEmpty();
    }

    @Override
    public boolean processDonation(Donation donation) {
        if (!(donation instanceof OnlineDonation)) return false;
        OnlineDonation online = (OnlineDonation) donation;

        if (!verifyPayment(online.getTransactionId())) {
            donation.updateStatus(DonationStatus.REJECTED);
            return false;
        }
        donation.updateStatus(DonationStatus.VALIDATED);
        online.setPaymentReference(paymentGatewayName + " - " + online.getTransactionId());
        donation.updateStatus(DonationStatus.PROCESSED);
        return true;
            }

    @Override
    public DonationType getSupportedType() {
        return DonationType.ONLINE;
    }

    private boolean verifyPayment(String transactionId) {
        return transactionId != null && !transactionId.isEmpty(); // Placeholder
    }

}
