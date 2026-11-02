package com.example.nonprofitmanagementsystem.communication;

import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDateTime;

public class LoggingDecorator extends MessageDecorator {
    private String logFilePath;

    public LoggingDecorator(MessageSender sender, String logFilePath) {
        super(sender);
        if (logFilePath == null || logFilePath.isBlank()) {
            throw new IllegalArgumentException("Log file path cannot be empty");
        }
        this.logFilePath = logFilePath;
    }

    @Override
    public boolean sendMessage(String recipient, String subject, String message) {
        logMessage(recipient, message);
        boolean success = super.sendMessage(recipient, subject, message);
        logResult(success);
        return success;
    }

    private void logMessage(String recipient, String message) {
        writeLog("Sending to " + recipient + ": " + message);
    }

    private void logResult(boolean success) {
        writeLog(success ? "Delivered" : "Failed");
    }

    private void writeLog(String entry) {
        try (FileWriter writer = new FileWriter(logFilePath, true)) {
            writer.write(LocalDateTime.now() + " " + entry + System.lineSeparator());
        } catch (IOException e) {
            System.err.println("Could not write to log file " + logFilePath + ": " + e.getMessage());
        }
    }
}
