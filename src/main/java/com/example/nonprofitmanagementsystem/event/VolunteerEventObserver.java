package com.example.nonprofitmanagementsystem.event;

import com.example.nonprofitmanagementsystem.communication.CommunicationManager;
import com.example.nonprofitmanagementsystem.user.Volunteer;
import com.example.nonprofitmanagementsystem.volunteer.AssignmentStatus;
import com.example.nonprofitmanagementsystem.volunteer.VolunteerAssignment;

public class VolunteerEventObserver implements EventObserver {
    private Volunteer volunteer;
    private VolunteerAssignment assignment;
    private CommunicationManager communicationManager;

    public VolunteerEventObserver(Volunteer volunteer, VolunteerAssignment assignment, CommunicationManager communicationManager) {
        this.volunteer = volunteer;
        this.assignment = assignment;
        this.communicationManager = communicationManager;
    }

    public VolunteerAssignment getAssignment() {
        return assignment;
    }

    @Override
    public void update(Event event, String message) {
        // React to cancellation: update the volunteer's assignment
        if (event.getEventStatus() == EventStatus.CANCELLED) {
            assignment.updateStatus(AssignmentStatus.CANCELLED);
        }

        if (event.getEventStatus() == EventStatus.COMPLETED
                && assignment.getStatus() != AssignmentStatus.CANCELLED) {
            assignment.updateStatus(AssignmentStatus.COMPLETED);
        }

        String text = "Hello " + volunteer.getFullName() + ",\n"
                + "Update for \"" + event.getEventName() + "\" (your volunteer assignment):\n"
                + message + "\n"
                + "Date: " + event.getEventDate() + " | Location: " + event.getLocation();

        communicationManager.notifyUser(volunteer, "Event Update", text);
    }
}
