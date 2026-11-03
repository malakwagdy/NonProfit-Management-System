package com.example.nonprofitmanagementsystem.user;

public class AdministratorCreator extends UserCreator {
    @Override
    protected User createUser(int userId, SignUpRequest r) {
        return new Administrator(userId, r.getFirstName(), r.getLastName(), r.getEmail(), r.getPhoneNumber(), r.getPassword(), r.getAddress());
    }

    @Override
    public UserRole getSupportedRole() {
        return UserRole.ADMINISTRATOR;
    }

    @Override
    public boolean allowsSelfRegistration() {
        return false;
    }
}
