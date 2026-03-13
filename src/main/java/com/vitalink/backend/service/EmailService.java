package com.vitalink.backend.service;

import java.util.List;

public interface EmailService {
    void sendActivationEmail(String toEmail, String activationToken);
    void sendPasswordResetEmail(String toEmail, String resetToken);
    void sendQrScannedAlertToOwner(String toEmail, String ownerName, String scannedAt);
    void sendQrScannedAlertToContacts(List<String> toEmails, String ownerName, String scannedAt);
}
