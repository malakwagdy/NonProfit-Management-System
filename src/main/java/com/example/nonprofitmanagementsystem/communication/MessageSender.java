package com.example.nonprofitmanagementsystem.communication;

public interface MessageSender {
    boolean sendMessage(String recipient, String subject, String message);
}
