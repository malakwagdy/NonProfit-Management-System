package com.example.nonprofitmanagementsystem.UserOrgManagement;

import java.util.ArrayList;

public class Beneficiary extends User {
    private ArrayList<String> needs;
    private int familySize;
  //  eligibilityStatus eligibilityStatus;

    public Beneficiary(int userId, String firstName, String lastName, String email, String phoneNumber, String passwordHash, String address) {
        super(userId, firstName, lastName, email, phoneNumber, passwordHash, address);
        this.needs = new ArrayList<>();
        this.familySize = 0;
    }

    public void addNeed(String need) {
        this.needs.add(need);
    }

    public void removeNeed(String need) {
        this.needs.remove(need);
    }

    public ArrayList<String> getNeeds() {
        return needs;
    }

    public int getFamilySize() {
        return familySize;
    }

    public void updateFamilySize(int familySize) {
        this.familySize = familySize;
    }
//    public void updateEligibilityStatus(eligibilityStatus eligibilityStatus) {
//        this.eligibilityStatus = eligibilityStatus;
//    }
//    public eligibilityStatus getEligibilityStatus() {
//        return eligibilityStatus;
//    }
}
