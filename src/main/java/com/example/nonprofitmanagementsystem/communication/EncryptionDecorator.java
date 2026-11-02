package com.example.nonprofitmanagementsystem.communication;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.security.GeneralSecurityException;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;

public class EncryptionDecorator extends MessageDecorator {
    private String encryptionAlgorithm;
    private SecretKey secretKey;

    public EncryptionDecorator(MessageSender sender, String encryptionAlgorithm) {
        super(sender);
        this.encryptionAlgorithm = encryptionAlgorithm;
        try {
            this.secretKey = KeyGenerator.getInstance(encryptionAlgorithm).generateKey();
        } catch (NoSuchAlgorithmException e) {
            throw new IllegalArgumentException("Unsupported encryption algorithm: " + encryptionAlgorithm, e);
        }
    }

    @Override
    public boolean sendMessage(String recipient, String subject, String message) {
        return super.sendMessage(recipient, subject, encryptMessage(message));
    }

    private String encryptMessage(String message) {
        try {
            Cipher cipher = Cipher.getInstance(encryptionAlgorithm);
            cipher.init(Cipher.ENCRYPT_MODE, secretKey);
            byte[] encrypted = cipher.doFinal(message.getBytes(StandardCharsets.UTF_8));
            return Base64.getEncoder().encodeToString(encrypted);
        } catch (GeneralSecurityException e) {
            throw new IllegalStateException("Could not encrypt message", e);
        }
    }
}
