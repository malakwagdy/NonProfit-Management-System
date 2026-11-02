package com.example.nonprofitmanagementsystem.beneficiary;

public class Resource {
    private int resourceId;
    private String resourceName;
    private ResourceCategory category;
    private AssistanceType assistanceType;
    private int availableQuantity;
    private String unit;
    private String description;
    private boolean active;

    public Resource(int resourceId, String resourceName, ResourceCategory category,
                    AssistanceType assistanceType, int availableQuantity, String unit, String description) {
        if (resourceId <= 0 || category == null || assistanceType == null || availableQuantity < 0
                || unit == null || unit.isBlank()) {
            throw new IllegalArgumentException("Invalid resource details");
        }
        updateDetails(resourceName, description);
        this.resourceId = resourceId;
        this.category = category;
        this.assistanceType = assistanceType;
        this.availableQuantity = availableQuantity;
        this.unit = unit;
        this.active = true;
    }

    public int getResourceId() { return resourceId; }
    public String getResourceName() { return resourceName; }
    public ResourceCategory getCategory() { return category; }
    public AssistanceType getAssistanceType() { return assistanceType; }
    public int getAvailableQuantity() { return availableQuantity; }
    public String getUnit() { return unit; }
    public boolean isActive() { return active; }

    public void updateDetails(String name, String description) {
        if (name == null || name.isBlank() || description == null) {
            throw new IllegalArgumentException("Invalid resource name or description");
        }
        resourceName = name;
        this.description = description;
    }

    public void changeCategory(ResourceCategory category) {
        if (category == null) {
            throw new IllegalArgumentException("Category cannot be null");
        }
        this.category = category;
    }

    public void addStock(int quantity) {
        if (quantity <= 0 || quantity > Integer.MAX_VALUE - availableQuantity) {
            throw new IllegalArgumentException("Invalid stock quantity");
        }
        availableQuantity += quantity;
    }

    public boolean reduceStock(int quantity) {
        if (quantity <= 0) {
            throw new IllegalArgumentException("Quantity must be positive");
        }
        if (!active || quantity > availableQuantity) {
            return false;
        }
        availableQuantity -= quantity;
        return true;
    }

    public void deactivate() { active = false; }
}
