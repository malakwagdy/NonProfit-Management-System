package com.example.nonprofitmanagementsystem.event;

import com.example.nonprofitmanagementsystem.UserOrgManagement.User;
import com.example.nonprofitmanagementsystem.communication.CommunicationManager;
import com.example.nonprofitmanagementsystem.UserOrgManagement.Administrator;
import com.example.nonprofitmanagementsystem.UserOrgManagement.Donor;

import java.time.LocalDateTime;
import java.util.*;

public class EventManager {
    private ArrayList<Event> events;
    private CommunicationManager communicationManager;
    private Set<Integer> remindedEventIds; // prevents sending the same reminder twice


    public EventManager(CommunicationManager communicationManager) {
        this.communicationManager = communicationManager;
        this.events = new ArrayList<>();
        this.remindedEventIds = new HashSet<>();
    }

    public void createEvent(Event event, Administrator admin) {
        validateAdmin(admin);
        if (event == null) {
            throw new IllegalArgumentException("Event cannot be null");
        }
        if (getEventById(event.getEventId()) != null) {
            throw new IllegalArgumentException("An com.example.nonprofitmanagementsystem.event with ID " + event.getEventId() + " already exists");
        }
        if (event.getEventDate().isBefore(LocalDateTime.now())) {
            throw new IllegalArgumentException("Event date must be in the future");
        }
        events.add(event);
    }
    public boolean updateEvent(int eventId, String name, String description, LocalDateTime date,
                               String location, Administrator admin) {
        validateAdmin(admin);
        Event event = getEventById(eventId);
        if (event == null || isClosed(event)) {
            return false;
        }
        if (date.isBefore(LocalDateTime.now())) {
            return false;
        }
        event.updateEventDetails(name, description, date, location); // notifies observers
        remindedEventIds.remove(eventId); // date may have changed, so allow a new reminder
        return true;
    }

    public boolean cancelEvent(int eventId, Administrator admin) {
        validateAdmin(admin);
        Event event = getEventById(eventId);
        if (event == null || isClosed(event)) {
            return false;
        }
        event.updateStatus(EventStatus.CANCELLED); // notifies observers
        return true;
    }


    public Event getEventById(int eventId) {
        for (Event event : events) {
            if (event.getEventId() == eventId) {
                return event;
            }
        }
        return null;
    }

    public ArrayList<Event> getAllEvents() {
        return new ArrayList<>(events); // copy, so callers can't modify the internal ArrayList
    }

    public ArrayList<Event> getUpcomingEvents() {
        ArrayList<Event> result = new ArrayList<>();
        LocalDateTime now = LocalDateTime.now();
        for (Event event : events) {
            if (event.getEventDate().isAfter(now) && !isClosed(event)) {
                result.add(event);
            }
        }
        result.sort(Comparator.comparing(Event::getEventDate)); // soonest first
        return result;
    }

    public ArrayList<Event> getEventsByStatus(EventStatus status) {
        ArrayList<Event> result = new ArrayList<>();
        for (Event event : events) {
            if (event.getEventStatus() == status) {
                result.add(event);
            }
        }
        return result;
    }

    public ArrayList<Event> getEventsByType(EventType type) {
        ArrayList<Event> result = new ArrayList<>();
        for (Event event : events) {
            if (event.getEventType() == type) {
                result.add(event);
            }
        }
        return result;
    }

    // registration & subscriptions

    public EventRegistration registerParticipant(int eventId, User participant) {
        Event event = findEventOrThrow(eventId);

        // Event does the validation (open, not full, no duplicate) and creates the registration
        EventRegistration registration = event.registerParticipant(participant);

        // Subscribe the attendee to this com.example.nonprofitmanagementsystem.event's updates
        AttendeeEventObserver observer = new AttendeeEventObserver(registration, communicationManager);
        event.addObserver(observer);

        // Confirmation goes to this attendee only, not to every observer
        observer.update(event, "Your registration is confirmed.");

        return registration;
    }

    public void subscribeDonor(int eventId, Donor donor) {
        Event event = findEventOrThrow(eventId);
        if (event.getEventType() != EventType.FUNDRAISER) {
            throw new IllegalArgumentException("Donors can only subscribe to fundraiser events");
        }
        if (isClosed(event)) {
            throw new IllegalStateException("Event is cancelled or completed");
        }
        event.addObserver(new DonorEventObserver(donor, communicationManager));
    }

    public ArrayList<EventRegistration> getEventRegistrations(int eventId) {
        Event event = getEventById(eventId);
        if (event == null) {
            return new ArrayList<>();
        }
        return new ArrayList<>(event.getRegistrations());
    }

    // attendance

    public boolean recordAttendance(int eventId, int registrationId, boolean attended) {
        Event event = getEventById(eventId);
        if (event == null || event.getEventStatus() == EventStatus.CANCELLED) {
            return false;
        }
        for (EventRegistration registration : event.getRegistrations()) {
            if (registration.getRegistrationId() == registrationId) {
                if (registration.getAttendanceStatus() == AttendanceStatus.CANCELLED) {
                    return false; // can't record attendance for a cancelled registration
                }
                if (attended) {
                    registration.markAttended();
                } else {
                    registration.markAbsent();
                }
                return true;
            }
        }
        return false; // registration not found in this com.example.nonprofitmanagementsystem.event
    }

    // Returns a value between 0.0 and 1.0 (multiply by 100 for a percentage)
    public double getAttendanceRate(int eventId) {
        Event event = getEventById(eventId);
        if (event == null) {
            return 0.0;
        }
        int active = 0;
        int attended = 0;
        for (EventRegistration registration : event.getRegistrations()) {
            if (registration.getAttendanceStatus() == AttendanceStatus.CANCELLED) {
                continue;
            }
            active++;
            if (registration.getAttendanceStatus() == AttendanceStatus.ATTENDED) {
                attended++;
            }
        }
        return active == 0 ? 0.0 : (double) attended / active;
    }

    // reminders
    // sends a reminder for every open com.example.nonprofitmanagementsystem.event happening within the next 24 hours.
    // meant to be run periodically (e.g. by a scheduled job). Returns how many events were reminded.
    public int sendUpcomingReminders() {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime windowEnd = now.plusHours(24);
        int count = 0;

        for (Event event : events) {
            boolean inWindow = event.getEventDate().isAfter(now)
                    && event.getEventDate().isBefore(windowEnd);
            boolean alreadyReminded = remindedEventIds.contains(event.getEventId());

            if (inWindow && !isClosed(event) && !alreadyReminded) {
                event.notifyObservers("Reminder: \"" + event.getEventName()
                        + "\" starts on " + event.getEventDate()
                        + " at " + event.getLocation() + ".");
                remindedEventIds.add(event.getEventId());
                count++;
            }
        }
        return count;
    }

    //helper functions

    private void validateAdmin(Administrator admin) {
        if (admin == null) {
            throw new SecurityException("Only an administrator can perform this action");
        }
    }

    private Event findEventOrThrow(int eventId) {
        Event event = getEventById(eventId);
        if (event == null) {
            throw new IllegalArgumentException("Event not found: " + eventId);
        }
        return event;
    }

    private boolean isClosed(Event event) {
        return event.getEventStatus() == EventStatus.CANCELLED
                || event.getEventStatus() == EventStatus.COMPLETED;
    }
}
