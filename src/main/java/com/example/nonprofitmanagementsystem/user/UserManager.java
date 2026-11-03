package com.example.nonprofitmanagementsystem.user;

import java.util.ArrayList;
import java.util.EnumMap;
import java.util.Map;

public class UserManager {
    private static UserManager instance;
    private final ArrayList<User> users = new ArrayList<>();
    private final Map<UserRole, UserCreator> creators = new EnumMap<>(UserRole.class);
    private static int nextUserId = 1;

    private UserManager() {
        registerCreator(new DonorCreator());
        registerCreator(new VolunteerCreator());
        registerCreator(new BeneficiaryCreator());
        registerCreator(new AdministratorCreator());
    }

    private static int generateUserId() {
        return nextUserId++;
    }

    public static synchronized UserManager getInstance() {
        if (instance == null) instance = new UserManager();
        return instance;
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
        if (existing != null && existing != user) throw new IllegalArgumentException("Email is already registered");
        user.updateProfile(firstName, lastName, email, phone, address);
    }

    public User findByEmail(String email) {
        if (email == null) return null;
        for (User u : users)
            if (u.getEmail().equalsIgnoreCase(email.trim())) return u;
        return null;
    }

    public ArrayList<User> getUsersByRole(UserRole role) {
        ArrayList<User> result = new ArrayList<>();
        for (User u : users) {
            if (u.getRole() == role) result.add(u);
        }
        return result;
    }
    public User findById(int userId) {
        for (User u : users) {
            if (u.getUserId() == userId) return u;
        }
        return null;
    }
    public ArrayList<User> getAllUsers() { return new ArrayList<>(users); }
    public void deactivateUser(int userId, Administrator admin) {
        if (admin == null || !admin.isActive())
            throw new SecurityException("Only an active administrator can deactivate users");
        User user = findById(userId);
        if (user != null) user.deactivateAccount();
    }

    private User register(UserCreator creator, SignUpRequest r) {
        validate(r);
        User user = creator.newUser(generateUserId(), r);
        users.add(user);
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

    public void activateUser(int userId, Administrator admin) {
        if (admin == null || !admin.isActive())
            throw new SecurityException("Only an active administrator can activate users");
        User user = findById(userId);
        if (user != null) user.activateAccount();
    }

    private boolean isBlank(String s) { return s == null || s.isBlank(); }
}