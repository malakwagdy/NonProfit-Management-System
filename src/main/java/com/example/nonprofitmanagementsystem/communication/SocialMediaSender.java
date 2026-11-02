package com.example.nonprofitmanagementsystem.communication;

public class SocialMediaSender implements MessageSender {
    private String platformName;
    private String accountId;

    public SocialMediaSender(String platformName, String accountId) {
        this.platformName = platformName;
        this.accountId = accountId;
    }

    @Override
    public boolean sendMessage(String recipient, String subject, String message) {
        if (!validateAccount(accountId) || recipient == null || recipient.isBlank()) {
            return false;
        }
        System.out.println("[" + platformName + "] Account: " + accountId
                + " | Page: " + recipient
                + " | Title: " + subject + "\n" + message);
        return true;
    }

    private boolean validateAccount(String accountId) {
        return accountId != null && !accountId.isBlank();
    }
}
