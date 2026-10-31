package event;

import communication.CommunicationManager;
import users.Volunteer;
import volunteer.AssignmentStatus;
import volunteer.VolunteerAssignment;

public class VolunteerEventObserver implements EventObserver {
    private Volunteer voluteer;
    private VolunteerAssignment assignment;
    private CommunicationManager communicationManager;

    public VolunteerEventObserver(Volunteer volunteer, VolunteerAssignment assignment, CommunicationManager communicationManager) {
        this.voluteer = volunteer;
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
