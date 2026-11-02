package com.example.nonprofitmanagementsystem.communication;

public class EmailSender implements MessageSender {
    private String smtpServer;
    private String senderEmail;

    public EmailSender(String smtpServer, String senderEmail) {
        this.smtpServer = smtpServer;
        this.senderEmail = senderEmail;
    }

    @Override
    public boolean sendMessage(String recipient, String subject, String message) {
        if (!validateEmail(recipient)) {
            return false;
        }
        System.out.println("[EMAIL via " + smtpServer + "] From: " + senderEmail
                + " | To: " + recipient
                + " | Subject: " + subject + "\n" + message);
        return true;
    }

    private boolean validateEmail(String email) {
        return email != null && email.matches("[^@\\s]+@[^@\\s]+\\.[^@\\s]+");
    }
}
