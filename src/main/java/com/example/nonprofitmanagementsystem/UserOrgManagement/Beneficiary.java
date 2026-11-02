package com.example.nonprofitmanagementsystem.UserOrgManagement;

import com.example.nonprofitmanagementsystem.beneficiary.NeedCategory;
import java.util.ArrayList;
import java.util.List;

public class Beneficiary extends User {
    private List<NeedCategory> needs;
    private int familySize;
    private EligibilityStatus eligibilityStatus;

    public Beneficiary(int userId, String firstName, String lastName, String email,
                       String phoneNumber, String passwordHash, String address, int familySize) {
        super(userId, firstName, lastName, email, phoneNumber, passwordHash, address);
        if (userId <= 0) {
            throw new IllegalArgumentException("User ID must be positive");
        }
        updateFamilySize(familySize);
        needs = new ArrayList<>();
        eligibilityStatus = EligibilityStatus.PENDING_REVIEW;
    }

    public void addNeed(NeedCategory need) {
        if (need == null) {
            throw new IllegalArgumentException("Need cannot be null");
        }
        for (NeedCategory existing : needs) {
            if (existing.getNeedCategoryId() == need.getNeedCategoryId()) {
                throw new IllegalArgumentException("Need already added");
            }
        }
        needs.add(need);
    }

    public boolean removeNeed(int needId) {
        for (int i = 0; i < needs.size(); i++) {
            if (needs.get(i).getNeedCategoryId() == needId) {
                needs.remove(i);
                return true;
            }
        }
        return false;
    }

    public List<NeedCategory> getNeeds() { return new ArrayList<>(needs); }
    public int getFamilySize() { return familySize; }
    public EligibilityStatus getEligibilityStatus() { return eligibilityStatus; }

    public void updateFamilySize(int size) {
        if (size <= 0) {
            throw new IllegalArgumentException("Family size must be positive");
        }
        familySize = size;
    }

    // Call through BeneficiaryManager when checking administrator permissions.
    public void updateEligibilityStatus(EligibilityStatus status) {
        if (status == null) {
            throw new IllegalArgumentException("Eligibility status cannot be null");
        }
        eligibilityStatus = status;
    }
}
