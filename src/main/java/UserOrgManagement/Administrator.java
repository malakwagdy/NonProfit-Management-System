package UserOrgManagement;

public class Administrator extends User {
    private String department;
    private int accessLevel;

    public Administrator(int userId, String firstName, String lastName, String email, String phoneNumber, String passwordHash, String address) {
        super(userId, firstName, lastName, email, phoneNumber, passwordHash, address);
        this.department = "";
        this.accessLevel = 0;
    }
    public String getDepartment() {
        return department;
    }

    public int getAccessLevel() {
        return accessLevel;
    }
//    public boolean hasPermission(String action) {
//        return this.accessLevel >= requiredLevel;
//    }
}

