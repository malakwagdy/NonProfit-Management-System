package event;

import communication.CommunicationManager;
import UserOrgManagement.Volunteer;
import VolunteerManagement.AssignmentStatus;
import VolunteerManagement.volunteerAssignment;

public class VolunteerEventObserver implements EventObserver {
    private Volunteer volunteer;
    private volunteerAssignment assignment;
    private CommunicationManager communicationManager;

    public VolunteerEventObserver(Volunteer volunteer, volunteerAssignment assignment, CommunicationManager communicationManager) {
        this.volunteer = volunteer;
        this.assignment = assignment;
        this.communicationManager = communicationManager;
    }

    @Override
    public void update(Event event, String message) {
        // React to cancellation: update the volunteer's assignment
        if (event.getEventStatus() == EventStatus.CANCELLED) {
            assignment.updateStatus(AssignmentStatus.CANCELLED);
        }

        String text = "Hello " + volunteer.getFullName() + ",\n"
                + "Update for \"" + event.getEventName() + "\" (your volunteer assignment):\n"
                + message + "\n"
                + "Date: " + event.getEventDate() + " | Location: " + event.getLocation();

        communicationManager.send(volunteer, "Update","Event Update",text);
    }
}
