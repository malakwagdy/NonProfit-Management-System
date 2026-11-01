package VolunteerManagement;

import UserOrgManagement.Volunteer;
import event.Event;

import java.time.LocalDateTime;

public class volunteerAssignment {
    private int assignmentId;
    private Volunteer volunteer;
    private Event event;
    private String taskDescription;
    private LocalDateTime assignmentDate;
    private double hoursCompleted;
    private AssignmentStatus status;

    public volunteerAssignment(int assignmentId, Volunteer volunteer, Event event, String taskDescription, LocalDateTime assignmentDate) {
        this.assignmentId = assignmentId;
        this.volunteer = volunteer;
        this.event = event;
        this.taskDescription = taskDescription;
        this.assignmentDate = assignmentDate;
        this.hoursCompleted = 0.0;
        // this.status = AssignmentStatus.PENDING;
    }
    public int getAssignmentId() {
        return assignmentId;
    }
    public Volunteer getVolunteer() {
        return volunteer;
    }
    public Event getEvent() {
        return event;
    }
    public String getTaskDescription() {
        return taskDescription;
    }
    public void updateTaskDescription(String taskDescription) {
        this.taskDescription = taskDescription;
    }
    public LocalDateTime getAssignmentDate() {
        return assignmentDate;
    }
    public double getHoursCompleted() {
        return hoursCompleted;
    }
    public void recordHoursCompleted(double hours) {
        this.hoursCompleted += hours;
        volunteer.addToTotalHours(hours);
    }
    public AssignmentStatus getStatus() {
        return status;
    }
    public void updateStatus(AssignmentStatus status) {
        this.status = status;
    }
}
