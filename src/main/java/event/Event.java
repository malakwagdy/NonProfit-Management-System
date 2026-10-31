package event;


import users.User;

import java.time.LocalDateTime;
import java.util.List;

public class Event implements EventSubject{
    private int eventId;
    private String eventName;
    private String description;
    private EventType eventType;
    private LocalDateTime eventDate;
    private String location;
    private int capacity;
    private List<String> requiredSkills;
    private EventStatus eventStatus;
    private List<EventObserver> observers;
    private List<EventRegistration> registrations;

    public Event(int eventId, String eventName, String description, EventType eventType, LocalDateTime eventDate, String location, int capacity) {
        this.eventId = eventId;
        this.eventName = eventName;
        this.description = description;
        this.eventType = eventType;
        this.eventDate = eventDate;
        this.location = location;
        this.capacity = capacity;
    }

    public int getEventId() {
        return eventId;
    }

    public String getEventName() {
        return eventName;
    }

    public String getDescription() {
        return description;
    }

    public EventType getEventType() {
        return eventType;
    }

    public LocalDateTime getEventDate() {
        return eventDate;
    }

    public String getLocation() {
        return location;
    }

    public int getCapacity() {
        return capacity;
    }

    public EventStatus getEventStatus() {
        return eventStatus;
    }

    public int getRegisteredCount(){
        return registrations.size();
    }

    public boolean isFull(){
        return registrations.size() == capacity;
    }

    public void addRequiredSkill(String skill){
        requiredSkills.add(skill);
    }

    public List<String> getRequiredSkills() {
        return requiredSkills;
    }

    public void updateEventDetails(String name, String description, LocalDateTime eventDate, String location) {
        this.eventName = name;
        this.description = description;
        this.eventDate = eventDate;
        this.location = location;
    }

    public void updateStatus(EventStatus eventStatus){
        this.eventStatus = eventStatus;
    }

    public EventRegistration registerParticipant(User user) {
        // 1. Event must be open for registration
        if (eventStatus == EventStatus.CANCELLED || eventStatus == EventStatus.COMPLETED
                || eventDate.isBefore(LocalDateTime.now())) {
            throw new IllegalStateException("Event is not open for registration");
        }

        // 2. Capacity check
        if (isFull()) {
            throw new IllegalStateException("Event is full");
        }

        // 3. No duplicate registration (ignore cancelled ones)
        for (EventRegistration r : registrations) {
            if (r.getParticipant().equals(user)
                    && r.getAttendanceStatus() != AttendanceStatus.CANCELLED) {
                throw new IllegalStateException("User is already registered");
            }
        }

        // 4. Create the registration (constructor generates the ticket number)
        EventRegistration registration =
                new EventRegistration(this, user);

        // 5. Store it and return it
        registrations.add(registration);
        return registration;
    }

    public boolean cancelRegistration(int registrationId) {
        for (EventRegistration r : registrations) {
            if (r.getRegistrationId() == registrationId) {
                registrations.remove(r);
                return true;
            }
        }
        return false;
    }

    public List<EventRegistration> getRegistrations() {
        return registrations;
    }

    @Override
    public void addObserver(EventObserver observer) {
        observers.add(observer);
    }

    @Override
    public void removeObserver(EventObserver observer) {
        observers.remove(observer);
    }

    @Override
    public void notifyObservers(String message) {
        for (EventObserver observer : observers) {
            observer.update(this, message);
        }
    }
}
