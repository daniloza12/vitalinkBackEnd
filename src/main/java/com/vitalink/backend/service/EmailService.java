package com.vitalink.backend.service;

public interface EmailService {
    void sendActivationEmail(String toEmail, String activationToken);
    void sendPasswordResetEmail(String toEmail, String resetToken);
}
