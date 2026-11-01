package com.example.nonprofitmanagementsystem.donation;

import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;

public class DonationManager {
    private Map<DonationType, DonationCreator> creators;
    private Map<DonationType, DonationStrategy> strategies;
    private List<Donation> donations;
    private final CommunicationManager communicationManager;

    public DonationManager(CommunicationManager communicationManager) {
        if (communicationManager == null) {
            throw new IllegalArgumentException(
                    "Communication manager cannot be null"
            );
        }
        this.communicationManager = communicationManager;
        this.creators = new EnumMap<>(DonationType.class);
        this.strategies = new EnumMap<>(DonationType.class);
        this.donations = new ArrayList<>();
    }

    public void setDonationStrategy(DonationStrategy strategy) {
        if (strategy == null) {
            throw new IllegalArgumentException("Strategy cannot be null");
        }

        strategies.put(strategy.getSupportedType(), strategy);
    }

    public void registerCreator(DonationCreator creator) {
        if (creator == null) {
            throw new IllegalArgumentException("Creator cannot be null");
        }

        creators.put(creator.getSupportedType(), creator);
    }

    public Donation createDonation(DonationRequest request) {
        if (request == null || request.getType() == null) {
            throw new IllegalArgumentException("Invalid donation request");
        }
        DonationCreator creator = creators.get(request.getType());
        if (creator != null) {
            Donation donation = creator.newDonation(request);
            donations.add(donation);
            return donation;
        } else {
            throw new IllegalArgumentException("No creator registered for donation type: " + request.getType());
        }
    }

    public boolean processDonation(Donation donation) {
        if (donation == null) {
            throw new IllegalArgumentException("Donation cannot be null");
        }
        if (donation.getDonationType() == null) {
            throw new IllegalArgumentException("Donation type cannot be null");
        }

        DonationStrategy strategy = strategies.get(donation.getDonationType());
        if (strategy == null) return false;

        if (!strategy.validateDonation(donation)) {
            donation.updateStatus(DonationStatus.REJECTED);
            return false;
        }
        boolean success = strategy.processDonation(donation);
        if (success) {
            donation.getDonor().addToTotal(donation.getAmount());
//            communicationManager.sendDonationConfirmation(donation);
        }
        return success;
    }

    public Donation getDonationById(int donationId) {
        for (Donation donation : donations) {
            if (donation.getDonationId() == donationId) {
                return donation;
            }
        }
        return null;
    }

    public List<Donation> getAllDonations() {
        return donations;
    }

    public List<Donation> getDonationsByDonor(int donorId) {
        List<Donation> donorDonations = new ArrayList<>();
        for (Donation d : donations) {
            if (d.getDonor().getUserId() == donorId) {
                donorDonations.add(d);
            }
        }
        return donorDonations;
    }

    public List<Donation> getDonationsByDonorAndYear(int donorId, int year) {
        List<Donation> result = new ArrayList<>();
        for (Donation d : donations) {
            if (d.getDonor().getUserId() == donorId && d.getDonationDate() != null && d.getDonationDate().getYear() == year) {
                result.add(d);
            }
        }
        return result;
    }

    public double calculateTotalDonations() {
        double sum = 0.0;
        for (Donation donation : donations) {
            if (donation.getStatus() == DonationStatus.PROCESSED) {
                sum += donation.getAmount();
            }        }
        return sum;
    }
}
