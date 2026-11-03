package com.example.nonprofitmanagementsystem.volunteer;

import com.example.nonprofitmanagementsystem.event.EventStatus;
import com.example.nonprofitmanagementsystem.user.Administrator;
import com.example.nonprofitmanagementsystem.user.Volunteer;

import com.example.nonprofitmanagementsystem.communication.CommunicationManager;
import com.example.nonprofitmanagementsystem.event.Event;
import com.example.nonprofitmanagementsystem.event.EventObserver;
import com.example.nonprofitmanagementsystem.event.VolunteerEventObserver;

import java.util.ArrayList;

public class VolunteerManager {
    private ArrayList<Volunteer> Volunteers;
    private ArrayList<VolunteerAssignment> assignments;
    private CommunicationManager communicationManager;
    private static int nextAssignmentId = 1;

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
        // Check if the event is cancelled or completed
        if (event.getEventStatus() == EventStatus.CANCELLED || event.getEventStatus() == EventStatus.COMPLETED) {
            throw new IllegalStateException("Cannot assign volunteers to a cancelled or completed event.");
        }
        // Check if the volunteer is already assigned to this event
        for (VolunteerAssignment existing : assignments) {
            if (existing.getVolunteer() == volunteer && existing.getEvent() == event
                    && existing.getStatus() != AssignmentStatus.CANCELLED) {
                throw new IllegalStateException("Volunteer is already assigned to this event.");
            }
        }
        // Check if the volunteer is available and has the required skills
        if (volunteer.getAvailability().isEmpty()) {
            throw new IllegalArgumentException("Volunteer is not available.");
        }
        for (String skill : event.getRequiredSkills()) {
            if (!volunteer.hasSkill(skill)) {
                throw new IllegalArgumentException("Volunteer does not have the required skills for this event.");
            }
        }
        // Create a new assignment
        int assignmentId = getNextAssignmentId();
        VolunteerAssignment assignment = new VolunteerAssignment(assignmentId, volunteer, event, task, event.getEventDate());
        VolunteerEventObserver observer = new VolunteerEventObserver(volunteer, assignment, communicationManager);
        event.addObserver(observer);
        assignments.add(assignment);
        // Notify the volunteer about the assignment
        communicationManager.notifyUser(volunteer, "Volunteer Assignment", "You have been assigned to the event: " + event.getEventName() + " for task: " + task);
    }

    private static int getNextAssignmentId() {
        return nextAssignmentId++;
    }

    public void removeAssignment(VolunteerAssignment assignment) {
        assignments.remove(assignment);
        Event event = assignment.getEvent();
        for (EventObserver observer : event.getObservers()) {
            if (observer instanceof VolunteerEventObserver
                    && ((VolunteerEventObserver) observer).getAssignment() == assignment) {
                observer.unsubscribe(event);
                break;
            }
        }
        communicationManager.notifyUser(assignment.getVolunteer(), "Volunteer Assignment Removed", "Your assignment for the event: " + assignment.getEvent().getEventName() + " has been removed.");
    }
    public void recordVolunteerHours(VolunteerAssignment assignment, double hours) {
        if (hours <= 0) throw new IllegalArgumentException("Hours must be positive.");
        if (assignment.getStatus() == AssignmentStatus.CANCELLED)
            throw new IllegalStateException("Cannot record hours for a cancelled assignment.");
        assignment.recordHoursCompleted(hours);
        communicationManager.notifyUser(assignment.getVolunteer(), "Volunteer Hours Recorded", "You have recorded " + hours + " hours for the event: " + assignment.getEvent().getEventName());
    }
    public ArrayList<VolunteerAssignment> getAssignmentsForVolunteer(Volunteer volunteer) {
        ArrayList<VolunteerAssignment> userAssignments = new ArrayList<>();
        for (VolunteerAssignment assignment : assignments) {
            if (assignment.getVolunteer().equals(volunteer)) {
                userAssignments.add(assignment);
            }
        }
        return userAssignments;
    }
    public ArrayList<VolunteerAssignment> getAssignmentsForEvent(Event event) {
        ArrayList<VolunteerAssignment> eventAssignments = new ArrayList<>();
        for (VolunteerAssignment assignment : assignments) {
            if (assignment.getEvent().equals(event)) {
                eventAssignments.add(assignment);
            }
        }
        return eventAssignments;
    }



}