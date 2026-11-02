package com.example.nonprofitmanagementsystem.beneficiary;

public class NeedCategory {
    private int needCategoryId;
    private String name;
    private String description;

    public NeedCategory(int id, String name, String description) {
        if (id <= 0 || name == null || name.isBlank() || description == null) {
            throw new IllegalArgumentException("Invalid category details");
        }
        this.needCategoryId = id;
        this.name = name;
        this.description = description;
    }

    public int getNeedCategoryId() { return needCategoryId; }
    public String getName() { return name; }
    public String getDescription() { return description; }

    public void updateDetails(String name, String description) {
        if (name == null || name.isBlank() || description == null) {
            throw new IllegalArgumentException("Invalid category details");
        }
        this.name = name;
        this.description = description;
    }
}
