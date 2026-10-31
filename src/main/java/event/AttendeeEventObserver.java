package event;

import communication.CommunicationManager;
import users.User;

public class AttendeeEventObserver implements EventObserver {
    private EventRegistration registration;
    private CommunicationManager communicationManager;

    public AttendeeEventObserver(EventRegistration registration, CommunicationManager communicationManager) {
        this.registration = registration;
        this.communicationManager = communicationManager;
    }

    @Override
    public void update(Event event, String message) {
        // Don't message someone who cancelled their registration
        if (registration.getAttendanceStatus() == AttendanceStatus.CANCELLED) {
            return;
        }

        User attendee = registration.getParticipant();

        String text = "Hello " + attendee.getFullName() + ",\n"
                + "Update for \"" + event.getEventName() + "\":\n"
                + message + "\n"
                + "Date: " + event.getEventDate() + " | Location: " + event.getLocation() + "\n"
                + "Your ticket number: " + registration.getTicketNumber();
        communicationManager.send(attendee, "Event Details", "Reminder for Upcoming Event",text);

    }
}
