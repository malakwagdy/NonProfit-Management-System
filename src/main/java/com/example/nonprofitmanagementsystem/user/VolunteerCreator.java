package com.example.nonprofitmanagementsystem.user;

public class VolunteerCreator extends UserCreator {
    @Override
    protected User createUser(int userId, SignUpRequest r) {
        return new Volunteer(userId, r.getFirstName(), r.getLastName(), r.getEmail(), r.getPhoneNumber(), r.getPassword(), r.getAddress());
    }

    @Override
    public UserRole getSupportedRole() {
        return UserRole.VOLUNTEER;
    }
}
