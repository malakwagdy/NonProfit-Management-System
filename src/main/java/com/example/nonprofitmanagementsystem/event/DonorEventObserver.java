package com.example.nonprofitmanagementsystem.event;

import com.example.nonprofitmanagementsystem.user.Donor;
import com.example.nonprofitmanagementsystem.communication.CommunicationManager;

public class DonorEventObserver implements EventObserver {
    private Donor donor;
    private CommunicationManager communicationManager;
    public DonorEventObserver(Donor donor, CommunicationManager communicationManager) {
        this.donor = donor;
        this.communicationManager = communicationManager;
    }

    public Donor getDonor() {
        return donor;
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

        communicationManager.notifyUser(donor, "Fundraiser Event Details", text);

    }
}
