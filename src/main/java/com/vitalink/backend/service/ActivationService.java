package com.vitalink.backend.service;

public interface ActivationService {
    void createAndSendToken(String accountId, String email);
    void activateAccount(String token);
    void resendToken(String email);
}
