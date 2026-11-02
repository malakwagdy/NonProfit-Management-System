package com.example.nonprofitmanagementsystem.beneficiary;

import com.example.nonprofitmanagementsystem.UserOrgManagement.Administrator;
import com.example.nonprofitmanagementsystem.UserOrgManagement.Beneficiary;
import java.time.LocalDateTime;

public class ResourceDistribution {
    private int distributionId;
    private Beneficiary beneficiary;
    private Resource resource;
    private int quantity;
    private LocalDateTime requestDate;
    private LocalDateTime reviewedAt;
    private Administrator reviewedBy;
    private String decisionReason;
    private LocalDateTime distributionDate;
    private Administrator distributedBy;
    private String notes;
    private DistributionStatus status;

    public ResourceDistribution(int distributionId, Beneficiary beneficiary, Resource resource, int quantity) {
        if (distributionId <= 0 || beneficiary == null || resource == null || quantity <= 0) {
            throw new IllegalArgumentException("Invalid distribution details");
        }
        this.distributionId = distributionId;
        this.beneficiary = beneficiary;
        this.resource = resource;
        this.quantity = quantity;
        requestDate = LocalDateTime.now();
        notes = "";
        status = DistributionStatus.PENDING;
    }

    public int getDistributionId() { return distributionId; }
    public Beneficiary getBeneficiary() { return beneficiary; }
    public Resource getResource() { return resource; }
    public int getQuantity() { return quantity; }
    public LocalDateTime getRequestDate() { return requestDate; }
    public LocalDateTime getReviewedAt() { return reviewedAt; }
    public Administrator getReviewedBy() { return reviewedBy; }
    public String getDecisionReason() { return decisionReason; }
    public LocalDateTime getDistributionDate() { return distributionDate; }
    public Administrator getDistributedBy() { return distributedBy; }
    public DistributionStatus getStatus() { return status; }

    public void approve(Administrator admin, String reason) {
        review(admin, reason, DistributionStatus.APPROVED);
    }

    public void reject(Administrator admin, String reason) {
        review(admin, reason, DistributionStatus.REJECTED);
    }

    private void review(Administrator admin, String reason, DistributionStatus decision) {
        if (admin == null || reason == null || reason.isBlank()) {
            throw new IllegalArgumentException("Administrator and reason are required");
        }
        if (status != DistributionStatus.PENDING) {
            throw new IllegalStateException("Request must be pending");
        }
        reviewedBy = admin;
        reviewedAt = LocalDateTime.now();
        decisionReason = reason;
        status = decision;
    }

    // Records the delivery only. The manager checks permission and deducts stock.
    public void markDistributed(Administrator admin) {
        if (admin == null) {
            throw new IllegalArgumentException("Administrator is required");
        }
        if (status != DistributionStatus.APPROVED) {
            throw new IllegalStateException("Request must be approved");
        }
        distributedBy = admin;
        distributionDate = LocalDateTime.now();
        status = DistributionStatus.DISTRIBUTED;
    }

    public void updateNotes(String notes) {
        if (notes == null) {
            throw new IllegalArgumentException("Notes cannot be null");
        }
        this.notes = notes;
    }
}
