package com.example.nonprofitmanagementsystem.event;



import com.example.nonprofitmanagementsystem.UserOrgManagement.User;

import java.time.LocalDateTime;
import java.util.EnumMap;
import java.util.Map;

public class EventRegistration {
    private static int nextId = 1;
    private int registrationId;
    private Event event;
    private User participant;
    private LocalDateTime registrationDate;
    private String ticketNumber;
    private AttendanceStatus attendanceStatus;

    public EventRegistration( Event event, User participant){
        this.registrationId = nextId++;
        this.event = event;
        this.participant = participant;
    }
    public int getRegistrationId() {
        return registrationId;
    }
    public Event getEvent() {
        return event;
    }
    public User getParticipant() {
        return participant;
    }

    public String getTicketNumber() {
        return ticketNumber;
    }

    public AttendanceStatus getAttendanceStatus() {
        return attendanceStatus;
    }
    public void markAttended(){
        this.attendanceStatus = AttendanceStatus.ATTENDED;
    }
    public void markAbsent(){
        this.attendanceStatus = AttendanceStatus.ABSENT;
    }
    public void cancel(){
        this.attendanceStatus = AttendanceStatus.CANCELLED;
    }
    private static final Map<EventType, Integer> ticketCounters = new EnumMap<>(EventType.class);
    private String generateTicketNumber(){
        EventType type = event.getEventType();
        String prefix = switch (type) {
            case FUNDRAISER -> "FUND";
            case WORKSHOP -> "WORK";
            case OUTREACH_PROGRAM -> "OUTR";
            case OTHER -> "OTHR";
        };
        int nextNumber = ticketCounters.merge(type, 1, Integer::sum);
        return String.format("%s-%04d", prefix, nextNumber);
    }
}
