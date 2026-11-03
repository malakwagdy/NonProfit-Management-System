package com.example.nonprofitmanagementsystem.user;

public class Administrator extends User {
    private String department;
    private int accessLevel;

    public Administrator(int userId, String firstName, String lastName, String email, String phoneNumber, String password, String address) {
        super(userId, firstName, lastName, email, phoneNumber, password, address);
        this.department = "";
        this.accessLevel = 0;
    }
    public String getDepartment() {
        return department;
    }

    public int getAccessLevel() {
        return accessLevel;
    }

    public void setDepartment(String department) {
        this.department = department;
    }

    public void setAccessLevel(int accessLevel) {
        this.accessLevel = accessLevel;
    }

    @Override
    public UserRole getRole() {
        return UserRole.ADMINISTRATOR;
    }
//    public boolean hasPermission(String action) {
//        return this.accessLevel >= requiredLevel;
//    }
}

