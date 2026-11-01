package UserOrgManagement;

import java.util.ArrayList;

public class Volunteer extends User {
    private String availability;
    private ArrayList<String> skills;
    private ArrayList<String> preferredActivities;
    private double totalHoursVolunteered;

    public Volunteer(int userId, String firstName, String lastName, String email, String phoneNumber, String passwordHash, String address) {
        super(userId, firstName, lastName, email, phoneNumber, passwordHash, address);
        this.availability = "";
        this.skills = new ArrayList<>();
        this.preferredActivities = new ArrayList<>();
        this.totalHoursVolunteered = 0.0;
    }

    public String getAvailability() {
        return availability;
    }

    public void updateAvailability(String availability) {
        this.availability = availability;
    }

    public void addSkill(String skill) {
        this.skills.add(skill);
    }
    public void removeSkill(String skill) {
        this.skills.remove(skill);
    }
    public void addPreferredActivity(String activity) {
        this.preferredActivities.add(activity);
    }
    public void removePreferredActivity(String activity) {
        this.preferredActivities.remove(activity);
    }
    public double getTotalHoursVolunteered() {
        return totalHoursVolunteered;
    }
    public void addToTotalHours(double hours) {
        this.totalHoursVolunteered += hours;
    }
    public ArrayList<String> getSkills() {
        return skills;
    }
    public ArrayList<String> getPreferredActivities() {
        return preferredActivities;
    }
    public boolean hasSkill(String skill) {
        return skills.contains(skill);
    }
}
