package com.example.nonprofitmanagementsystem.beneficiary;

public class ResourceCategory {
    private int resourceCategoryId;
    private String name;
    private String description;

    public ResourceCategory(int id, String name, String description) {
        if (id <= 0 || name == null || name.isBlank() || description == null) {
            throw new IllegalArgumentException("Invalid category details");
        }
        this.resourceCategoryId = id;
        this.name = name;
        this.description = description;
    }

    public int getResourceCategoryId() { return resourceCategoryId; }
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
