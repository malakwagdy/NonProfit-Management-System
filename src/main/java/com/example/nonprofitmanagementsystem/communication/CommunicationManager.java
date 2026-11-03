package com.example.nonprofitmanagementsystem.communication;

import com.example.nonprofitmanagementsystem.user.Donor;
import com.example.nonprofitmanagementsystem.user.User;
import com.example.nonprofitmanagementsystem.config.ConfigurationManager;
import com.example.nonprofitmanagementsystem.donation.Donation;

import java.util.EnumMap;
import java.util.List;
import java.util.Map;

public class CommunicationManager {
    private Map<NotificationChannel, MessageSender> senders;

    public CommunicationManager() {
        this.senders = new EnumMap<>(NotificationChannel.class);
    }

    public void addChannel(NotificationChannel channel, MessageSender sender) {
        if (channel == null || sender == null) {
            throw new IllegalArgumentException("Channel and sender cannot be null");
        }
        senders.put(channel, sender);
    }

    public void removeChannel(NotificationChannel channel) {
        senders.remove(channel);
    }

    public boolean notifyUser(User user, String subject, String message) {
        if (user == null) {
            throw new IllegalArgumentException("User cannot be null");
        }
        NotificationChannel channel = user.getPreferredChannel();
        if (channel == NotificationChannel.SOCIAL_MEDIA) {
            channel = NotificationChannel.EMAIL;
        }
        return sendViaChannel(channel, resolveRecipient(user, channel), subject, message);
    }

    public boolean sendViaChannel(NotificationChannel channel, String recipient, String subject, String message) {
        if (!isChannelEnabled(channel)) {
            return false;
        }
        return senders.get(channel).sendMessage(recipient, subject, message);
    }

    public int sendBulkNotification(List<User> users, String subject, String message) {
        if (users == null) {
            throw new IllegalArgumentException("Users cannot be null");
        }
        int sentCount = 0;
        for (User user : users) {
            if (notifyUser(user, subject, message)) {
                sentCount++;
            }
        }
        return sentCount;
    }

    public boolean sendDonationConfirmation(Donation donation) {
        if (donation == null) {
            throw new IllegalArgumentException("Donation cannot be null");
        }
        Donor donor = donation.getDonor();
        String message = "Dear " + donor.getFullName() + ",\n"
                + "Thank you for your donation.\n"
                + "Donation ID: " + donation.getDonationId() + "\n"
                + "Type: " + donation.getDonationType() + "\n"
                + "Amount: " + donation.getAmount();
        return notifyUser(donor, "Donation Confirmation", message);
    }

    public boolean publishOutreachPost(String content) {
        String pageHandle = ConfigurationManager.getInstance().getOrganizationName();
        return sendViaChannel(NotificationChannel.SOCIAL_MEDIA, pageHandle, "Outreach Post", content);
    }

    private String resolveRecipient(User user, NotificationChannel channel) {
        return channel == NotificationChannel.SMS ? user.getPhoneNumber() : user.getEmail();
    }

    private boolean isChannelEnabled(NotificationChannel channel) {
        return senders.containsKey(channel);
    }
}
