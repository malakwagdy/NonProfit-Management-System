package com.example.nonprofitmanagementsystem.config;

import com.example.nonprofitmanagementsystem.user.Administrator;
import com.example.nonprofitmanagementsystem.user.Beneficiary;
import com.example.nonprofitmanagementsystem.user.Donor;
import com.example.nonprofitmanagementsystem.user.EligibilityStatus;
import com.example.nonprofitmanagementsystem.user.User;
import com.example.nonprofitmanagementsystem.user.UserRole;
import com.example.nonprofitmanagementsystem.user.Volunteer;
import com.example.nonprofitmanagementsystem.beneficiary.NeedCategory;
import com.example.nonprofitmanagementsystem.communication.NotificationChannel;
import io.github.cdimascio.dotenv.Dotenv;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

// Singleton: the whole app shares one DatabaseManager, and every database call goes through it.
public class DatabaseManager {

    // ==================== Singleton ====================

    private static DatabaseManager instance;

    private final String url;
    private final String user;
    private final String password;

    private DatabaseManager() {
        Dotenv env = Dotenv.load();
        url = env.get("DB_URL");
        user = env.get("DB_USER");
        password = env.get("DB_PASSWORD");
    }

    public static synchronized DatabaseManager getInstance() {
        if (instance == null) {
            instance = new DatabaseManager();
        }
        return instance;
    }

    public Connection getConnection() throws SQLException {
        return DriverManager.getConnection(url, user, password);
    }

    // ==================== Fetching users ====================

    public User getUserById(int userId) throws SQLException {
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement("SELECT * FROM Users WHERE user_id = ?")) {
            ps.setInt(1, userId);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return buildUser(conn, rs);
            }
            return null;
        }
    }

    public User getUserByEmail(String email) throws SQLException {
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement("SELECT * FROM Users WHERE LOWER(email) = LOWER(?)")) {
            ps.setString(1, email);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return buildUser(conn, rs);
            }
            return null;
        }
    }

    public List<User> getAllUsers() throws SQLException {
        List<User> users = new ArrayList<>();
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement("SELECT * FROM Users ORDER BY user_id")) {
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                users.add(buildUser(conn, rs));
            }
        }
        return users;
    }

    // userType is "ADMIN", "DONOR", "VOLUNTEER" or "BENEFICIARY"
    public List<User> getUsersByType(String userType) throws SQLException {
        List<User> users = new ArrayList<>();
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement("SELECT * FROM Users WHERE user_type = ? ORDER BY user_id")) {
            ps.setString(1, userType);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                users.add(buildUser(conn, rs));
            }
        }
        return users;
    }

    public List<User> getUsersByRole(UserRole role) throws SQLException {
        return getUsersByType(toDbType(role));
    }

    // Takes one row from the Users table, creates the right kind of object, then loads its details.
    private User buildUser(Connection conn, ResultSet rs) throws SQLException {
        int id = rs.getInt("user_id");
        String firstName = rs.getString("first_name");
        String lastName = rs.getString("last_name");
        String email = rs.getString("email");
        String phone = rs.getString("phone_number");
        String passwordHash = rs.getString("password_hash");
        String address = rs.getString("address");
        String type = rs.getString("user_type");

        User user;
        if (type.equals("ADMIN")) {
            Administrator admin = new Administrator(id, firstName, lastName, email, phone, passwordHash, address);
            loadAdministratorDetails(conn, admin);
            user = admin;
        } else if (type.equals("DONOR")) {
            Donor donor = new Donor(id, firstName, lastName, email, phone, passwordHash, address);
            loadDonorDetails(conn, donor);
            user = donor;
        } else if (type.equals("VOLUNTEER")) {
            Volunteer volunteer = new Volunteer(id, firstName, lastName, email, phone, passwordHash, address);
            loadVolunteerDetails(conn, volunteer);
            user = volunteer;
        } else {
            // Placeholder family size of 1; loadBeneficiaryDetails sets the real one.
            Beneficiary beneficiary = new Beneficiary(id, firstName, lastName, email, phone, passwordHash, address, 1);
            loadBeneficiaryDetails(conn, beneficiary);
            user = beneficiary;
        }

        user.setRegistrationDate(rs.getTimestamp("registration_date").toLocalDateTime());
        user.setPreferredChannel(NotificationChannel.valueOf(rs.getString("preferred_channel")));
        if (!rs.getBoolean("active")) {
            user.deactivateAccount();
        }
        return user;
    }

    private void loadAdministratorDetails(Connection conn, Administrator admin) throws SQLException {
        try (PreparedStatement ps = conn.prepareStatement("SELECT * FROM AdministratorDetails WHERE user_id = ?")) {
            ps.setInt(1, admin.getUserId());
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                admin.setDepartment(rs.getString("department"));
                admin.setAccessLevel(rs.getInt("access_level"));
            }
        }
    }

    private void loadDonorDetails(Connection conn, Donor donor) throws SQLException {
        try (PreparedStatement ps = conn.prepareStatement("SELECT * FROM DonorDetails WHERE user_id = ?")) {
            ps.setInt(1, donor.getUserId());
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                donor.updateDonationPreference(rs.getString("donation_preference"));
                donor.setTotalDonated(rs.getDouble("total_donated"));
            }
        }
    }

    private void loadVolunteerDetails(Connection conn, Volunteer volunteer) throws SQLException {
        try (PreparedStatement ps = conn.prepareStatement("SELECT * FROM VolunteerDetails WHERE user_id = ?")) {
            ps.setInt(1, volunteer.getUserId());
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                volunteer.updateAvailability(rs.getString("availability"));
                volunteer.setTotalHoursVolunteered(rs.getDouble("total_volunteer_hours"));
            }
        }
        for (String skill : getSkills(conn, volunteer.getUserId())) {
            volunteer.addSkill(skill);
        }
        for (String activity : getActivities(conn, volunteer.getUserId())) {
            volunteer.addPreferredActivity(activity);
        }
    }

    private void loadBeneficiaryDetails(Connection conn, Beneficiary beneficiary) throws SQLException {
        try (PreparedStatement ps = conn.prepareStatement("SELECT * FROM BeneficiaryDetails WHERE user_id = ?")) {
            ps.setInt(1, beneficiary.getUserId());
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                beneficiary.updateFamilySize(rs.getInt("family_size"));
                beneficiary.updateEligibilityStatus(EligibilityStatus.valueOf(rs.getString("eligibility_status")));
            }
        }
        for (NeedCategory need : getNeeds(conn, beneficiary.getUserId())) {
            beneficiary.addNeed(need);
        }
    }

    private List<String> getSkills(Connection conn, int userId) throws SQLException {
        List<String> skills = new ArrayList<>();
        try (PreparedStatement ps = conn.prepareStatement(
                "SELECT skill FROM VolunteerSkills WHERE user_id = ? ORDER BY skill")) {
            ps.setInt(1, userId);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                skills.add(rs.getString("skill"));
            }
        }
        return skills;
    }

    private List<String> getActivities(Connection conn, int userId) throws SQLException {
        List<String> activities = new ArrayList<>();
        try (PreparedStatement ps = conn.prepareStatement(
                "SELECT activity FROM VolunteerActivities WHERE user_id = ? ORDER BY activity")) {
            ps.setInt(1, userId);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                activities.add(rs.getString("activity"));
            }
        }
        return activities;
    }

    private List<NeedCategory> getNeeds(Connection conn, int userId) throws SQLException {
        List<NeedCategory> needs = new ArrayList<>();
        try (PreparedStatement ps = conn.prepareStatement(
                "SELECT need_id, category FROM BeneficiaryNeeds WHERE user_id = ? ORDER BY need_id")) {
            ps.setInt(1, userId);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                needs.add(new NeedCategory(rs.getInt("need_id"), rs.getString("category"), ""));
            }
        }
        return needs;
    }

    // ==================== Saving a new user ====================

    // Inserts the user, then sets the id the database generated on the object.
    public void saveUser(User user) throws SQLException {
        String sql = "INSERT INTO Users (user_type, first_name, last_name, email, phone_number, password_hash, " +
                "address, registration_date, active, preferred_channel) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?) RETURNING user_id";
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, getUserType(user));
            ps.setString(2, user.getFirstName());
            ps.setString(3, user.getLastName());
            ps.setString(4, user.getEmail());
            ps.setString(5, user.getPhoneNumber());
            ps.setString(6, user.getPassword());
            ps.setString(7, user.getAddress());
            ps.setTimestamp(8, Timestamp.valueOf(user.getRegistrationDate()));
            ps.setBoolean(9, user.isActive());
            ps.setString(10, user.getPreferredChannel().name());
            ResultSet rs = ps.executeQuery();
            rs.next();
            user.setUserId(rs.getInt("user_id"));

            saveDetails(conn, user);
        }
    }

    private void saveDetails(Connection conn, User user) throws SQLException {
        if (user instanceof Administrator) {
            Administrator admin = (Administrator) user;
            try (PreparedStatement ps = conn.prepareStatement(
                    "INSERT INTO AdministratorDetails (user_id, department, access_level) VALUES (?, ?, ?)")) {
                ps.setInt(1, admin.getUserId());
                ps.setString(2, admin.getDepartment());
                ps.setInt(3, admin.getAccessLevel());
                ps.executeUpdate();
            }
        } else if (user instanceof Donor) {
            Donor donor = (Donor) user;
            try (PreparedStatement ps = conn.prepareStatement(
                    "INSERT INTO DonorDetails (user_id, donation_preference, total_donated) VALUES (?, ?, ?)")) {
                ps.setInt(1, donor.getUserId());
                ps.setString(2, donor.getDonationPreference());
                ps.setDouble(3, donor.getTotalDonated());
                ps.executeUpdate();
            }
        } else if (user instanceof Volunteer) {
            Volunteer volunteer = (Volunteer) user;
            try (PreparedStatement ps = conn.prepareStatement(
                    "INSERT INTO VolunteerDetails (user_id, availability, total_volunteer_hours) VALUES (?, ?, ?)")) {
                ps.setInt(1, volunteer.getUserId());
                ps.setString(2, volunteer.getAvailability());
                ps.setDouble(3, volunteer.getTotalHoursVolunteered());
                ps.executeUpdate();
            }
            saveSkillsAndActivities(conn, volunteer);
        } else if (user instanceof Beneficiary) {
            Beneficiary beneficiary = (Beneficiary) user;
            try (PreparedStatement ps = conn.prepareStatement(
                    "INSERT INTO BeneficiaryDetails (user_id, family_size, eligibility_status) VALUES (?, ?, ?)")) {
                ps.setInt(1, beneficiary.getUserId());
                ps.setInt(2, beneficiary.getFamilySize());
                ps.setString(3, beneficiary.getEligibilityStatus().name());
                ps.executeUpdate();
            }
            saveNeeds(conn, beneficiary);
        }
    }

    private void saveSkillsAndActivities(Connection conn, Volunteer volunteer) throws SQLException {
        for (String skill : volunteer.getSkills()) {
            try (PreparedStatement ps = conn.prepareStatement(
                    "INSERT INTO VolunteerSkills (user_id, skill) VALUES (?, ?)")) {
                ps.setInt(1, volunteer.getUserId());
                ps.setString(2, skill);
                ps.executeUpdate();
            }
        }
        for (String activity : volunteer.getPreferredActivities()) {
            try (PreparedStatement ps = conn.prepareStatement(
                    "INSERT INTO VolunteerActivities (user_id, activity) VALUES (?, ?)")) {
                ps.setInt(1, volunteer.getUserId());
                ps.setString(2, activity);
                ps.executeUpdate();
            }
        }
    }

    // Only the need's name is stored (in the category column).
    private void saveNeeds(Connection conn, Beneficiary beneficiary) throws SQLException {
        for (NeedCategory need : beneficiary.getNeeds()) {
            try (PreparedStatement ps = conn.prepareStatement(
                    "INSERT INTO BeneficiaryNeeds (user_id, category) VALUES (?, ?)")) {
                ps.setInt(1, beneficiary.getUserId());
                ps.setString(2, need.getName());
                ps.executeUpdate();
            }
        }
    }

    // ==================== Updating a user ====================

    public void updateUser(User user) throws SQLException {
        String sql = "UPDATE Users SET first_name = ?, last_name = ?, email = ?, phone_number = ?, " +
                "password_hash = ?, address = ?, active = ?, preferred_channel = ? WHERE user_id = ?";
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, user.getFirstName());
            ps.setString(2, user.getLastName());
            ps.setString(3, user.getEmail());
            ps.setString(4, user.getPhoneNumber());
            ps.setString(5, user.getPassword());
            ps.setString(6, user.getAddress());
            ps.setBoolean(7, user.isActive());
            ps.setString(8, user.getPreferredChannel().name());
            ps.setInt(9, user.getUserId());
            ps.executeUpdate();

            updateDetails(conn, user);
        }
    }

    private void updateDetails(Connection conn, User user) throws SQLException {
        if (user instanceof Administrator) {
            Administrator admin = (Administrator) user;
            try (PreparedStatement ps = conn.prepareStatement(
                    "UPDATE AdministratorDetails SET department = ?, access_level = ? WHERE user_id = ?")) {
                ps.setString(1, admin.getDepartment());
                ps.setInt(2, admin.getAccessLevel());
                ps.setInt(3, admin.getUserId());
                ps.executeUpdate();
            }
        } else if (user instanceof Donor) {
            Donor donor = (Donor) user;
            try (PreparedStatement ps = conn.prepareStatement(
                    "UPDATE DonorDetails SET donation_preference = ?, total_donated = ? WHERE user_id = ?")) {
                ps.setString(1, donor.getDonationPreference());
                ps.setDouble(2, donor.getTotalDonated());
                ps.setInt(3, donor.getUserId());
                ps.executeUpdate();
            }
        } else if (user instanceof Volunteer) {
            Volunteer volunteer = (Volunteer) user;
            try (PreparedStatement ps = conn.prepareStatement(
                    "UPDATE VolunteerDetails SET availability = ?, total_volunteer_hours = ? WHERE user_id = ?")) {
                ps.setString(1, volunteer.getAvailability());
                ps.setDouble(2, volunteer.getTotalHoursVolunteered());
                ps.setInt(3, volunteer.getUserId());
                ps.executeUpdate();
            }
            // Easiest way to sync the lists: delete the old rows and save the current ones.
            deleteRows(conn, "DELETE FROM VolunteerSkills WHERE user_id = ?", volunteer.getUserId());
            deleteRows(conn, "DELETE FROM VolunteerActivities WHERE user_id = ?", volunteer.getUserId());
            saveSkillsAndActivities(conn, volunteer);
        } else if (user instanceof Beneficiary) {
            Beneficiary beneficiary = (Beneficiary) user;
            try (PreparedStatement ps = conn.prepareStatement(
                    "UPDATE BeneficiaryDetails SET family_size = ?, eligibility_status = ? WHERE user_id = ?")) {
                ps.setInt(1, beneficiary.getFamilySize());
                ps.setString(2, beneficiary.getEligibilityStatus().name());
                ps.setInt(3, beneficiary.getUserId());
                ps.executeUpdate();
            }
            deleteRows(conn, "DELETE FROM BeneficiaryNeeds WHERE user_id = ?", beneficiary.getUserId());
            saveNeeds(conn, beneficiary);
        }
    }

    // ==================== Deleting a user ====================

    // ON DELETE CASCADE also removes the user's details, skills, activities and needs.
    public void deleteUser(int userId) throws SQLException {
        try (Connection conn = getConnection()) {
            deleteRows(conn, "DELETE FROM Users WHERE user_id = ?", userId);
        }
    }

    // ==================== Helpers ====================

    private void deleteRows(Connection conn, String sql, int userId) throws SQLException {
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, userId);
            ps.executeUpdate();
        }
    }

    // The database stores "ADMIN"; every other role has the same name in Java and in the database.
    private String toDbType(UserRole role) {
        if (role == UserRole.ADMINISTRATOR) return "ADMIN";
        return role.name();
    }

    private String getUserType(User user) {
        return toDbType(user.getRole());
    }
}
