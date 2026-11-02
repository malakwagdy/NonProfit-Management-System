package com.example.nonprofitmanagementsystem.beneficiary;

public class ImpactIndicator {
    private int indicatorId;
    private String name;
    private String unit;
    private boolean higherIsBetter;

    public ImpactIndicator(int id, String name, String unit, boolean higherIsBetter) {
        if (id <= 0 || name == null || name.isBlank() || unit == null || unit.isBlank()) {
            throw new IllegalArgumentException("Invalid indicator details");
        }
        this.indicatorId = id;
        this.name = name;
        this.unit = unit;
        this.higherIsBetter = higherIsBetter;
    }

    public int getIndicatorId() { return indicatorId; }
    public String getName() { return name; }
    public String getUnit() { return unit; }
    public boolean isHigherBetter() { return higherIsBetter; }
}
