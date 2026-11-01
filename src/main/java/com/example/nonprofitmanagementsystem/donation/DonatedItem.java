package com.example.nonprofitmanagementsystem.donation;

public class DonatedItem {
    private String itemName;
    private ItemCategory category;
    private ItemCondition condition;
    private int quantity;
    private double estimatedValue;


    public DonatedItem(String itemName, ItemCategory category, ItemCondition condition, int quantity, double estimatedValue) {
        this.itemName = itemName;
        this.category = category;
        this.condition = condition;
        this.quantity = quantity;
        this.estimatedValue = estimatedValue;
    }

    public String getName() {
        return itemName;
    }

    public ItemCategory getCategory() {
        return category;
    }

    public ItemCondition getCondition() {
        return condition;
    }

    public int getQuantity() {
        return quantity;
    }

    public double getEstimatedValue() {
        return estimatedValue;
    }

    public double getTotalEstimatedValue() {
        return estimatedValue * quantity;
    }
}
