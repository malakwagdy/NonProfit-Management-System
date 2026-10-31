package event;

import communication.CommunicationManager;
import users.Donor;
import volunteer.AssignmentStatus;

public class DonorEventObserver implements EventObserver {
    private Donor donor;
    private CommunicationManager communicationManager;
    public DonorEventObserver(Donor donor, CommunicationManager communicationManager) {
        this.donor = donor;
        this.communicationManager = communicationManager;
    }

    @Override
    public void update(Event event, String message) {
        // Donors only care about fundraisers
        if (event.getEventType() != EventType.FUNDRAISER) {
            return;
        }

        String text = "Dear " + donor.getFullName() + ",\n"
                + "Update on the fundraiser \"" + event.getEventName() + "\":\n"
                + message + "\n"
                + "Date: " + event.getEventDate() + " | Location: " + event.getLocation();

        communicationManager.send(donor, "Upcoming Fundraiser", "Fundraiser Event Details",text);

    }
}
