package com.example.nonprofitmanagementsystem.donation;

import java.util.ArrayList;
import java.util.List;
import com.example.nonprofitmanagementsystem.user.Donor;

public class InKindDonation extends Donation{
    private List<DonatedItem> items;
    private double estimatedValue;

    public InKindDonation(int donationId, Donor donor, String description, List<DonatedItem> items) {
        super(donationId, donor, calculateInitialValue(items), description);
        this.items = items != null ? items : new ArrayList<>();
        this.estimatedValue = getAmount();
    }

    public void addItem(DonatedItem item) {
        this.items.add(item);
    }

    public void removeItem(DonatedItem item) {
        this.items.remove(item);
    }

    public List<DonatedItem> getItems() {
        return items;
    }

    public List<DonatedItem> getItemsByCategory(ItemCategory category) {
        List<DonatedItem> filteredItems = new ArrayList<>();
        for (DonatedItem item : items) {
            if (item.getCategory().equals(category)) {
                filteredItems.add(item);
            }
        }
        return filteredItems;
    }

    public double getEstimatedValue() {
        return estimatedValue;
    }

    public void updateEstimatedValue( double finalValue) {
        this.estimatedValue = finalValue;
        setAmount(finalValue);
    }

    private static double calculateInitialValue(List<DonatedItem> items) {
        double total = 0.0;
        if (items == null) return total;
        for (DonatedItem item : items) {
            total += item.getTotalEstimatedValue();
        }
        return total;
    }

    public DonationType getDonationType() {
        return DonationType.IN_KIND;
    }
}
