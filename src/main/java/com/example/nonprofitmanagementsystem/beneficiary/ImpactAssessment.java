package com.example.nonprofitmanagementsystem.beneficiary;

import com.example.nonprofitmanagementsystem.user.Beneficiary;
import com.example.nonprofitmanagementsystem.event.Event;
import java.time.LocalDate;

public class ImpactAssessment {
    private int assessmentId;
    private Beneficiary beneficiary;
    private Event program;
    private ImpactIndicator indicator;
    private LocalDate assessmentDate;
    private double baselineValue;
    private double currentValue;
    private String notes;

    public ImpactAssessment(int assessmentId, Beneficiary beneficiary, Event program,
                            ImpactIndicator indicator, double baselineValue, double currentValue, String notes) {
        if (assessmentId <= 0 || beneficiary == null || program == null || indicator == null
                || !Double.isFinite(baselineValue)) {
            throw new IllegalArgumentException("Invalid assessment details");
        }
        this.assessmentId = assessmentId;
        this.beneficiary = beneficiary;
        this.program = program;
        this.indicator = indicator;
        this.baselineValue = baselineValue;
        assessmentDate = LocalDate.now();
        updateCurrentValue(currentValue);
        updateNotes(notes);
    }

    public int getAssessmentId() { return assessmentId; }
    public Beneficiary getBeneficiary() { return beneficiary; }
    public Event getProgram() { return program; }
    public ImpactIndicator getIndicator() { return indicator; }
    public LocalDate getAssessmentDate() { return assessmentDate; }
    public double getBaselineValue() { return baselineValue; }
    public double getCurrentValue() { return currentValue; }

    public void updateCurrentValue(double value) {
        if (!Double.isFinite(value)) {
            throw new IllegalArgumentException("Measurement must be a finite number");
        }
        currentValue = value;
    }

    public void updateNotes(String notes) {
        if (notes == null) {
            throw new IllegalArgumentException("Notes cannot be null");
        }
        this.notes = notes;
    }

    public double calculateImprovement() {
        if (indicator.isHigherBetter()) {
            return currentValue - baselineValue;
        }
        return baselineValue - currentValue;
    }
}
