package com.example.nonprofitmanagementsystem.documents;
import com.example.nonprofitmanagementsystem.user.Volunteer;
import java.time.LocalDate;

public class VolunteerCertificate extends Document {
    private Volunteer volunteer;
    private double totalHours;
    private LocalDate issueDate;

    public VolunteerCertificate(int documentId, Volunteer volunteer, double totalHours) {
        super(documentId);
        this.volunteer = volunteer;
        this.totalHours = totalHours;
        this.issueDate = LocalDate.now();
    }

    public Volunteer getVolunteer() {return volunteer;}

    public double getTotalHours() {return totalHours;}

    @Override
    public String generateContent() {
        return "Volunteer Certificate\n"
                + "Volunteer: " + volunteer.getFullName() + "\n"
                + "Total hours: " + totalHours + "\n"
                + "Issue date: " + issueDate;
    }

    @Override
    public String getDocumentType() {return "VolunteerCertificate";}
}