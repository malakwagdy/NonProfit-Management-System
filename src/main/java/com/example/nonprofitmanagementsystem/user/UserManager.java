package com.example.nonprofitmanagementsystem.user;

import com.example.nonprofitmanagementsystem.config.DatabaseManager;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.EnumMap;
import java.util.Map;

public class UserManager {
    private final DatabaseManager db = DatabaseManager.getInstance();
    private final Map<UserRole, UserCreator> creators = new EnumMap<>(UserRole.class);

    public UserManager() {
        registerCreator(new DonorCreator());
        registerCreator(new VolunteerCreator());
        registerCreator(new BeneficiaryCreator());
        registerCreator(new AdministratorCreator());
    }

    public void registerCreator(UserCreator creator) {
        creators.put(creator.getSupportedRole(), creator);
    }

    private UserCreator getCreator(SignUpRequest request) {
        if (request == null || request.getRole() == null) throw new IllegalArgumentException("Role is required");
        UserCreator creator = creators.get(request.getRole());
        if (creator == null) throw new IllegalArgumentException("No creator registered for role: " + request.getRole());
        return creator;
    }

    public User signUp(SignUpRequest request) {
        UserCreator creator = getCreator(request);
        if (!creator.allowsSelfRegistration())
            throw new SecurityException("This account type cannot be self-registered");
        return register(creator, request);
    }

    public User createAdministrator(SignUpRequest request, Administrator createdBy) {
        if (createdBy == null || !createdBy.isActive())
            throw new SecurityException("Only an administrator can create administrators");
        if (request.getRole() != UserRole.ADMINISTRATOR)
            throw new IllegalArgumentException("Request is not for an administrator");
        return register(getCreator(request), request);
    }

    public User login(String email, String password) {
        User user = findByEmail(email);
        // Same message for every failure so you don't reveal which emails exist
        if (user == null || !user.isActive() || !user.checkPassword(password))
            throw new IllegalArgumentException("Invalid email or password");
        return user;
    }

    public void updateProfile(User user, String firstName, String lastName, String email, String phone, String address) {
        if (isBlank(firstName) || isBlank(lastName)) throw new IllegalArgumentException("Name is required");
        if (email == null || !email.matches("[^@\\s]+@[^@\\s]+\\.[^@\\s]+"))
            throw new IllegalArgumentException("Invalid email address");
        User existing = findByEmail(email);
        // Compare ids, not objects: every database fetch returns a new object
        if (existing != null && existing.getUserId() != user.getUserId())
            throw new IllegalArgumentException("Email is already registered");
        user.updateProfile(firstName, lastName, normalizeEmail(email), phone, address);
        save(user);
    }

    public void changePassword(User user, String oldPassword, String newPassword) {
        if (newPassword == null || newPassword.length() < 8)
            throw new IllegalArgumentException("Password must be at least 8 characters");
        user.changePassword(oldPassword, newPassword);
        save(user);
    }

    public User findByEmail(String email) {
        if (email == null) return null;
        try {
            return db.getUserByEmail(normalizeEmail(email));
        } catch (SQLException e) {
            throw new RuntimeException("Database error", e);
        }
    }

    public ArrayList<User> getUsersByRole(UserRole role) {
        try {
            return new ArrayList<>(db.getUsersByRole(role));
        } catch (SQLException e) {
            throw new RuntimeException("Database error", e);
        }
    }

    public User findById(int userId) {
        try {
            return db.getUserById(userId);
        } catch (SQLException e) {
            throw new RuntimeException("Database error", e);
        }
    }

    public ArrayList<User> getAllUsers() {
        try {
            return new ArrayList<>(db.getAllUsers());
        } catch (SQLException e) {
            throw new RuntimeException("Database error", e);
        }
    }

    // Other managers call this after changing a user (donation total, hours, eligibility...)
    public void save(User user) {
        try {
            db.updateUser(user);
        } catch (SQLException e) {
            throw new RuntimeException("Database error", e);
        }
    }

    public void deactivateUser(int userId, Administrator admin) {
        if (admin == null || !admin.isActive())
            throw new SecurityException("Only an active administrator can deactivate users");
        User user = findById(userId);
        if (user != null) {
            user.deactivateAccount();
            save(user);
        }
    }

    public void activateUser(int userId, Administrator admin) {
        if (admin == null || !admin.isActive())
            throw new SecurityException("Only an active administrator can activate users");
        User user = findById(userId);
        if (user != null) {
            user.activateAccount();
            save(user);
        }
    }

    // The user is created with id 0; saveUser sets the real id from the database.
    private User register(UserCreator creator, SignUpRequest r) {
        validate(r);
        User user = creator.newUser(0, r);
        user.setEmail(normalizeEmail(user.getEmail()));
        try {
            db.saveUser(user);
        } catch (SQLException e) {
            throw new RuntimeException("Database error", e);
        }
        return user;
    }

    private void validate(SignUpRequest r) {
        if (r == null || r.getRole() == null) throw new IllegalArgumentException("Role is required");
        if (isBlank(r.getFirstName()) || isBlank(r.getLastName())) throw new IllegalArgumentException("Name is required");
        if (r.getEmail() == null || !r.getEmail().matches("[^@\\s]+@[^@\\s]+\\.[^@\\s]+"))
            throw new IllegalArgumentException("Invalid email address");
        if (findByEmail(r.getEmail()) != null) throw new IllegalArgumentException("Email is already registered");
        if (r.getPassword() == null || r.getPassword().length() < 8)
            throw new IllegalArgumentException("Password must be at least 8 characters");
        if (r.getRole() == UserRole.BENEFICIARY && r.getFamilySize() <= 0)
            throw new IllegalArgumentException("Family size must be positive");
    }

    // Store and look up emails the same way, so "Sara@X.com" and "sara@x.com" match
    private String normalizeEmail(String email) {
        return email.trim().toLowerCase();
    }

    private boolean isBlank(String s) { return s == null || s.isBlank(); }
}
