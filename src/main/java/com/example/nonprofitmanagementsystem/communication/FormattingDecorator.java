package com.example.nonprofitmanagementsystem.communication;

public class FormattingDecorator extends MessageDecorator {
    private String headerText;
    private String organizationSignature;

    public FormattingDecorator(MessageSender sender, String headerText, String organizationSignature) {
        super(sender);
        this.headerText = headerText;
        this.organizationSignature = organizationSignature;
    }

    @Override
    public boolean sendMessage(String recipient, String subject, String message) {
        return super.sendMessage(recipient, subject, appendSignature(formatMessage(message)));
    }

    private String formatMessage(String message) {
        return headerText + "\n\n" + message;
    }

    private String appendSignature(String message) {
        return message + "\n\n" + organizationSignature;
    }
}
