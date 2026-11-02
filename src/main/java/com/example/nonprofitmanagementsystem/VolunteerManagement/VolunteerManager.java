package com.example.nonprofitmanagementsystem.VolunteerManagement;

import com.example.nonprofitmanagementsystem.UserOrgManagement.Administrator;
import com.example.nonprofitmanagementsystem.UserOrgManagement.Volunteer;

import com.example.nonprofitmanagementsystem.communication.CommunicationManager;
import com.example.nonprofitmanagementsystem.event.Event;

import java.util.ArrayList;

public class VolunteerManager {
    private ArrayList<Volunteer> Volunteers;
    private ArrayList<volunteerAssignment> assignments;
    private CommunicationManager communicationManager;

    public VolunteerManager(CommunicationManager communicationManager) {
        this.Volunteers = new ArrayList<>();
        this.communicationManager = communicationManager;
        this.assignments = new ArrayList<>();
    }
    public void registerVolunteer(Volunteer Volunteer) {
        Volunteers.add(Volunteer);
    }
    public void removeVolunteer(Volunteer Volunteer) {
        Volunteers.remove(Volunteer);
    }
    public ArrayList<Volunteer> getVolunteers() {
        return Volunteers;
    }
    public Volunteer getVolunteerByID(int id) {
        for (Volunteer v : Volunteers) {
            if (v.getUserId() == id) {
                return v;
            }
        }
        return null;
    }
    public ArrayList<Volunteer> findVolunteersBySkill(String skill) {
        ArrayList<Volunteer> skilledVolunteers = new ArrayList<>();
        for (Volunteer v : Volunteers) {
            if (v.hasSkill(skill)) {
                skilledVolunteers.add(v);
            }
        }
        return skilledVolunteers;
    }
    public ArrayList<Volunteer> getAvailableVolunteers() {
        ArrayList<Volunteer> availableVolunteers = new ArrayList<>();
        for (Volunteer v : Volunteers) {
            if (!v.getAvailability().isEmpty()) {
                availableVolunteers.add(v);
            }
        }
        return availableVolunteers;
    }
    public ArrayList<Volunteer> findSuitableVolunteers(Event event) {
        ArrayList<Volunteer> suitableVolunteers = new ArrayList<>();
        ArrayList<String> requiredSkills = event.getRequiredSkills();
        for (Volunteer v : Volunteers) {
            boolean hasAllSkills = true;
            for (String skill : requiredSkills) {
                if (!v.hasSkill(skill)) {
                    hasAllSkills = false;
                    break;
                }
            }
            if (hasAllSkills) {
                suitableVolunteers.add(v);
            }
        }
        return suitableVolunteers;
    }
    public void assignVolunteer(Volunteer volunteer, Event event, String task, Administrator administrator) {
        // Validate administrator privileges
        if (administrator == null) {
            throw new SecurityException("Only an administrator can assign volunteers to events.");
        }
        // Check if the volunteer is available and has the required skills
        if (volunteer.getAvailability().isEmpty()) {
            throw new IllegalArgumentException("Volunteer is not available.");
        }
        for (String skill : event.getRequiredSkills()) {
            if (!volunteer.hasSkill(skill)) {
                throw new IllegalArgumentException("Volunteer does not have the required skills for this com.example.nonprofitmanagementsystem.event.");
            }
        }
        // Create a new assignment
        int assignmentId = assignments.size() + 1; // Simple ID generation
        volunteerAssignment assignment = new volunteerAssignment(assignmentId, volunteer, event, task, event.getEventDate());
        assignments.add(assignment);
        // Notify the volunteer about the assignment
        communicationManager.notifyUser(volunteer, "Volunteer Assignment", "You have been assigned to the event: " + event.getEventName() + " for task: " + task);
        assignments.add(assignment);
    }

    public void removeAssignment(volunteerAssignment assignment) {
        assignments.remove(assignment);
        communicationManager.notifyUser(assignment.getVolunteer(), "Volunteer Assignment Removed", "Your assignment for the event: " + assignment.getEvent().getEventName() + " has been removed.");
    }
    public void recordVolunteerHours(volunteerAssignment assignment, double hours) {
        assignment.recordHoursCompleted(hours);
        communicationManager.notifyUser(assignment.getVolunteer(), "Volunteer Hours Recorded", "You have recorded " + hours + " hours for the event: " + assignment.getEvent().getEventName());
    }
    public ArrayList<volunteerAssignment> getAssignmentsForVolunteer(Volunteer volunteer) {
        ArrayList<volunteerAssignment> userAssignments = new ArrayList<>();
        for (volunteerAssignment assignment : assignments) {
            if (assignment.getVolunteer().equals(volunteer)) {
                userAssignments.add(assignment);
            }
        }
        return userAssignments;
    }
    public ArrayList<volunteerAssignment> getAssignmentsForEvent(Event event) {
        ArrayList<volunteerAssignment> eventAssignments = new ArrayList<>();
        for (volunteerAssignment assignment : assignments) {
            if (assignment.getEvent().equals(event)) {
                eventAssignments.add(assignment);
            }
        }
        return eventAssignments;
    }



}