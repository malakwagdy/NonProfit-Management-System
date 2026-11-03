package com.example.nonprofitmanagementsystem.user;

public abstract class UserCreator {
    public User newUser(int userId, SignUpRequest request) {
        return createUser(userId, request);
    }

    protected abstract User createUser(int userId, SignUpRequest request);

    public abstract UserRole getSupportedRole();

    public boolean allowsSelfRegistration() {
        return true;
    }
}
