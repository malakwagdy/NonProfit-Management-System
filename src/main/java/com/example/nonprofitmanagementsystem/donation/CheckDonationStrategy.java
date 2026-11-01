package com.example.nonprofitmanagementsystem.donation;

import java.time.LocalDate;

public class CheckDonationStrategy implements DonationStrategy {
    private boolean bankVerificationRequired;

    public CheckDonationStrategy(boolean bankVerificationRequired) {
        this.bankVerificationRequired = bankVerificationRequired;
    }

    @Override
    public boolean validateDonation(Donation donation) {
        if (!(donation instanceof CheckDonation)) return false;
        CheckDonation check = (CheckDonation) donation;
        return donation.getAmount() > 0
                && check.getCheckNumber() != null && !check.getCheckNumber().isEmpty()
                && check.getBankName() != null && !check.getBankName().isEmpty();
    }

    @Override
    public boolean processDonation(Donation donation) {
        if (!(donation instanceof CheckDonation)) return false;
        CheckDonation check = (CheckDonation) donation;

        if (bankVerificationRequired && !verifyCheck(check.getCheckNumber(), check.getBankName())) {
            donation.updateStatus(DonationStatus.PENDING);   // waits for the bank
            return false;
        }
        donation.updateStatus(DonationStatus.VALIDATED);
        check.markAsCleared(LocalDate.now());
        donation.updateStatus(DonationStatus.PROCESSED);
        return true;    }

    @Override
    public DonationType getSupportedType() {
        return DonationType.CHECK;
    }

    private boolean verifyCheck(String checkNumber, String bankName) {
        return checkNumber != null && !checkNumber.isEmpty() && bankName != null && !bankName.isEmpty();
    }
}
