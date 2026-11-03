package com.example.nonprofitmanagementsystem.donation;

import java.util.ArrayList;
import java.util.List;

public class InKindDonationStrategy implements DonationStrategy{
    private List<ItemCategory> acceptedCategories;

    public InKindDonationStrategy(List<ItemCategory> acceptedCategories) {
        this.acceptedCategories = new ArrayList<>(acceptedCategories);
    }

    @Override
    public boolean validateDonation(Donation donation) {
        if (!(donation instanceof InKindDonation)) return false;
        InKindDonation inKind = (InKindDonation) donation;
        if(inKind.getItems() == null || inKind.getItems().isEmpty()) return false;
        for (DonatedItem item : inKind.getItems()) {
            if (item == null
                    || item.getCategory() == null
                    || item.getCondition() == null
                    || !acceptedCategories.contains(item.getCategory())
                    || item.getQuantity() <= 0
                    || item.getEstimatedValue() < 0)
                return false;
            }
        return true;
    }

    @Override
    public boolean processDonation(Donation donation) {
        if (!(donation instanceof InKindDonation)) return false;
        InKindDonation inKind = (InKindDonation) donation;
        donation.updateStatus(DonationStatus.VALIDATED);

        double finalValue = evaluateItems(inKind);
        inKind.updateEstimatedValue(finalValue);   // also updates amount
        donation.updateStatus(DonationStatus.PROCESSED);
        return true;    }

    @Override
    public DonationType getSupportedType() {
        return DonationType.IN_KIND;
    }

    private double evaluateItems(InKindDonation donation) {
        double total = 0;
        for (DonatedItem item : donation.getItems()) {
            double factor;
            switch (item.getCondition()) {
                case NEW:
                    factor = 1.0;
                    break;
                case GOOD:
                    factor = 0.8;
                    break;
                case FAIR:
                    factor = 0.5;
                    break;
                case POOR:
                    factor = 0.2;
                    break;
                default:
                    throw new IllegalStateException(
                            "Unsupported item condition: "
                                    + item.getCondition()
                    );
            }
            total += item.getTotalEstimatedValue() * factor;
        }
        return total;
    }
}