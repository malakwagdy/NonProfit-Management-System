package com.example.nonprofitmanagementsystem.user;

public class DonorCreator extends UserCreator {
    @Override
    protected User createUser(int userId, SignUpRequest r) {
        return new Donor(userId, r.getFirstName(), r.getLastName(), r.getEmail(), r.getPhoneNumber(), r.getPassword(), r.getAddress());
    }

    @Override
    public UserRole getSupportedRole() {
        return UserRole.DONOR;
    }
}
