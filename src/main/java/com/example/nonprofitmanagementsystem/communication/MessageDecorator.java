package com.example.nonprofitmanagementsystem.communication;

public abstract class MessageDecorator implements MessageSender {
    protected MessageSender wrappedSender;

    public MessageDecorator(MessageSender sender) {
        if (sender == null) {
            throw new IllegalArgumentException("Sender cannot be null");
        }
        this.wrappedSender = sender;
    }

    @Override
    public boolean sendMessage(String recipient, String subject, String message) {
        return wrappedSender.sendMessage(recipient, subject, message);
    }
}
