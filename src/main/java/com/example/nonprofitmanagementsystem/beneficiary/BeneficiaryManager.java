package com.example.nonprofitmanagementsystem.beneficiary;

import com.example.nonprofitmanagementsystem.UserOrgManagement.Administrator;
import com.example.nonprofitmanagementsystem.UserOrgManagement.Beneficiary;
import com.example.nonprofitmanagementsystem.UserOrgManagement.EligibilityStatus;
import com.example.nonprofitmanagementsystem.communication.CommunicationManager;
import com.example.nonprofitmanagementsystem.event.Event;
import java.util.ArrayList;
import java.util.List;

// Phase 1 prototype: in-memory, single-threaded, no login/permission system.
public class BeneficiaryManager {
    private List<Beneficiary> beneficiaries = new ArrayList<>();
    private List<Resource> resources = new ArrayList<>();
    private List<ResourceDistribution> distributions = new ArrayList<>();
    private List<ImpactAssessment> assessments = new ArrayList<>();
    private CommunicationManager communicationManager;
    private int nextDistributionId = 1;
    private int nextAssessmentId = 1;

    public BeneficiaryManager(CommunicationManager communicationManager) {
        this.communicationManager = communicationManager;
    }

    public void registerBeneficiary(Beneficiary beneficiary) {
        if (beneficiary == null || findBeneficiaryById(beneficiary.getUserId()) != null)
            throw new IllegalArgumentException("Null beneficiary or duplicate ID");
        beneficiaries.add(beneficiary);
    }

    public boolean deactivateBeneficiary(int userId) {
        Beneficiary beneficiary = findBeneficiaryById(userId);
        if (beneficiary == null) return false;
        beneficiary.deactivateAccount();
        return true;
    }

    public Beneficiary findBeneficiaryById(int userId) {
        for (Beneficiary beneficiary : beneficiaries)
            if (beneficiary.getUserId() == userId) return beneficiary;
        return null;
    }

    public List<Beneficiary> getAllBeneficiaries() {
        return new ArrayList<>(beneficiaries);
    }

    public List<Beneficiary> findBeneficiariesByNeed(int needId) {
        List<Beneficiary> result = new ArrayList<>();
        for (Beneficiary beneficiary : beneficiaries) {
            for (NeedCategory need : beneficiary.getNeeds()) {
                if (need.getNeedCategoryId() == needId) {
                    result.add(beneficiary);
                    break;
                }
            }
        }
        return result;
    }

    public void updateEligibility(int beneficiaryId, EligibilityStatus status, Administrator admin) {
        checkAdmin(admin);
        Beneficiary beneficiary = findBeneficiaryById(beneficiaryId);
        if (beneficiary == null) throw new IllegalArgumentException("Beneficiary not found");
        beneficiary.updateEligibilityStatus(status);
    }

    public void addResource(Resource resource) {
        if (resource == null) throw new IllegalArgumentException("Resource is required");
        for (Resource existing : resources)
            if (existing.getResourceId() == resource.getResourceId())
                throw new IllegalArgumentException("Duplicate resource ID");
        resources.add(resource);
    }

    public List<Resource> getAvailableResources() {
        List<Resource> result = new ArrayList<>();
        for (Resource resource : resources)
            if (resource.isActive() && resource.getAvailableQuantity() > 0) result.add(resource);
        return result;
    }

    public ResourceDistribution requestDistribution(Beneficiary beneficiary, Resource resource, int quantity) {
        if (!beneficiaries.contains(beneficiary) || !resources.contains(resource))
            throw new IllegalArgumentException("Use registered beneficiaries and resources");
        if (!beneficiary.isActive() || !resource.isActive())
            throw new IllegalStateException("Beneficiary and resource must be active");
        ResourceDistribution request = new ResourceDistribution(nextDistributionId, beneficiary, resource, quantity);
        distributions.add(request);
        nextDistributionId++;
        return request;
    }

    public boolean approveDistribution(int distributionId, Administrator admin, String reason) {
        checkAdmin(admin);
        ResourceDistribution request = findDistribution(distributionId);
        if (request == null) return false;
        checkEligible(request.getBeneficiary());
        request.approve(admin, reason);
        notifyBeneficiary(request.getBeneficiary(), "Request approved");
        return true;
    }

    public boolean rejectDistribution(int distributionId, Administrator admin, String reason) {
        checkAdmin(admin);
        ResourceDistribution request = findDistribution(distributionId);
        if (request == null) return false;
        request.reject(admin, reason);
        notifyBeneficiary(request.getBeneficiary(), "Request reviewed; please contact the organization");
        return true;
    }

    public boolean distributeResource(int distributionId, Administrator admin) {
        checkAdmin(admin);
        ResourceDistribution request = findDistribution(distributionId);
        if (request == null) return false;
        if (request.getStatus() != DistributionStatus.APPROVED)
            throw new IllegalStateException("Request must be approved and not already delivered");
        checkEligible(request.getBeneficiary());
        if (!request.getResource().reduceStock(request.getQuantity())) return false;
        request.markDistributed(admin);
        notifyBeneficiary(request.getBeneficiary(), "Assistance delivered");
        return true;
    }

    public List<ResourceDistribution> getDistributionHistory(int userId) {
        List<ResourceDistribution> result = new ArrayList<>();
        for (ResourceDistribution request : distributions)
            if (request.getBeneficiary().getUserId() == userId) result.add(request);
        return result;
    }

    public int calculateTotalDistributed(int resourceId) {
        int total = 0;
        for (ResourceDistribution request : distributions)
            if (request.getResource().getResourceId() == resourceId
                    && request.getStatus() == DistributionStatus.DISTRIBUTED) total += request.getQuantity();
        return total;
    }

    public ImpactAssessment recordAssessment(Beneficiary beneficiary, Event program,
            ImpactIndicator indicator, double baseline, double current, String notes) {
        if (!beneficiaries.contains(beneficiary)) throw new IllegalArgumentException("Beneficiary not registered");
        ImpactAssessment assessment = new ImpactAssessment(nextAssessmentId, beneficiary, program,
                indicator, baseline, current, notes);
        assessments.add(assessment);
        nextAssessmentId++;
        return assessment;
    }

    public List<ImpactAssessment> getAssessmentsForBeneficiary(int userId) {
        List<ImpactAssessment> result = new ArrayList<>();
        for (ImpactAssessment assessment : assessments)
            if (assessment.getBeneficiary().getUserId() == userId) result.add(assessment);
        return result;
    }

    public List<ImpactAssessment> getAssessmentsForProgram(int programId) {
        List<ImpactAssessment> result = new ArrayList<>();
        for (ImpactAssessment assessment : assessments)
            if (assessment.getProgram().getEventId() == programId) result.add(assessment);
        return result;
    }

    private ResourceDistribution findDistribution(int id) {
        for (ResourceDistribution request : distributions)
            if (request.getDistributionId() == id) return request;
        return null;
    }

    private void checkAdmin(Administrator admin) {
        if (admin == null || !admin.isActive()) throw new SecurityException("An active administrator is required");
    }

    private void checkEligible(Beneficiary beneficiary) {
        if (!beneficiary.isActive() || beneficiary.getEligibilityStatus() != EligibilityStatus.ELIGIBLE)
            throw new IllegalStateException("Beneficiary must be active and eligible");
    }

    private void notifyBeneficiary(Beneficiary beneficiary, String message) {
        if (communicationManager == null) return;
        try {
            if (!communicationManager.notifyUser(beneficiary, "Assistance update", message))
                System.out.println("Notification not sent; operation completed.");
        } catch (RuntimeException exception) {
            System.out.println("Notification failed; operation completed.");
        }
    }
}
