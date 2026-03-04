package com.vitalink.backend.service.impl;

import com.vitalink.backend.entity.Account;
import com.vitalink.backend.exception.BadCredentialsException;
import com.vitalink.backend.exception.ConflictException;
import com.vitalink.backend.repository.AccountRepository;
import com.vitalink.backend.service.AuthService;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.UUID;

@Service
public class AuthServiceImpl implements AuthService {

    private final AccountRepository accountRepository;

    public AuthServiceImpl(AccountRepository accountRepository) {
        this.accountRepository = accountRepository;
    }

    @Override
    public Account register(String email, String password) {
        if (accountRepository.findByEmail(email).isPresent()) {
            throw new ConflictException("Email already registered: " + email);
        }

        String hashedPassword = sha256(password);
        String securityAccount = sha256(email + System.currentTimeMillis()).substring(0, 32);
        String id = UUID.randomUUID().toString();
        String createdAt = LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME);

        Account account = new Account();
        account.setId(id);
        account.setEmail(email);
        account.setPassword(hashedPassword);
        account.setRole("USER");
        account.setSecurityAccount(securityAccount);
        account.setQrDataUrl(null);
        account.setCreatedAt(createdAt);

        return accountRepository.save(account);
    }

    @Override
    public Account login(String email, String password) {
        Account account = accountRepository.findByEmail(email)
                .orElseThrow(() -> new BadCredentialsException("Invalid credentials"));

        String hashedPassword = sha256(password);
        if (!hashedPassword.equals(account.getPassword())) {
            throw new BadCredentialsException("Invalid credentials");
        }

        return account;
    }

    private String sha256(String input) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(input.getBytes(StandardCharsets.UTF_8));
            StringBuilder hex = new StringBuilder();
            for (byte b : hash) {
                hex.append(String.format("%02x", b));
            }
            return hex.toString();
        } catch (Exception e) {
            throw new RuntimeException("SHA-256 error", e);
        }
    }
}
