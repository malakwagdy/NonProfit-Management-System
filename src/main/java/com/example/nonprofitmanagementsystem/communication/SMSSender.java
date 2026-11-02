package com.example.nonprofitmanagementsystem.communication;

public class SMSSender implements MessageSender {
    private String smsProvider;
    private String senderPhoneNumber;

    public SMSSender(String smsProvider, String senderPhoneNumber) {
        this.smsProvider = smsProvider;
        this.senderPhoneNumber = senderPhoneNumber;
    }

    @Override
    public boolean sendMessage(String recipient, String subject, String message) {
        if (!validatePhoneNumber(recipient)) {
            return false;
        }
        System.out.println("[SMS via " + smsProvider + "] From: " + senderPhoneNumber
                + " | To: " + recipient + "\n" + subject + ": " + message);
        return true;
    }

    private boolean validatePhoneNumber(String phoneNumber) {
        return phoneNumber != null && phoneNumber.matches("\\+?[0-9]{7,15}");
    }
}
