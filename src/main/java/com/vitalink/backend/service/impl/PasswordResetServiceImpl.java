package com.vitalink.backend.service.impl;

import com.vitalink.backend.entity.Account;
import com.vitalink.backend.entity.PasswordResetToken;
import com.vitalink.backend.exception.BadCredentialsException;
import com.vitalink.backend.exception.ResourceNotFoundException;
import com.vitalink.backend.repository.AccountRepository;
import com.vitalink.backend.repository.PasswordResetTokenRepository;
import com.vitalink.backend.service.EmailService;
import com.vitalink.backend.service.PasswordResetService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

@Service
public class PasswordResetServiceImpl implements PasswordResetService {

    private static final Logger log = LoggerFactory.getLogger(PasswordResetServiceImpl.class);

    private final PasswordResetTokenRepository tokenRepository;
    private final AccountRepository accountRepository;
    private final EmailService emailService;

    public PasswordResetServiceImpl(PasswordResetTokenRepository tokenRepository,
                                    AccountRepository accountRepository,
                                    EmailService emailService) {
        this.tokenRepository = tokenRepository;
        this.accountRepository = accountRepository;
        this.emailService = emailService;
    }

    @Override
    @Transactional
    public void forgotPassword(String email) {
        // Respuesta genérica siempre para no revelar si el email existe
        Optional<Account> optAccount = accountRepository.findByEmail(email);
        if (optAccount.isEmpty()) {
            log.warn("Password reset requested for non-existent email: {}", email);
            return;
        }

        Account account = optAccount.get();

        tokenRepository.deleteByAccountId(account.getId());

        PasswordResetToken resetToken = new PasswordResetToken();
        resetToken.setToken(UUID.randomUUID().toString());
        resetToken.setAccountId(account.getId());
        resetToken.setExpiresAt(LocalDateTime.now().plusHours(24));
        resetToken.setUsed(false);

        tokenRepository.save(resetToken);
        emailService.sendPasswordResetEmail(account.getEmail(), resetToken.getToken());
        log.info("Password reset token created for account {}", account.getId());
    }

    @Override
    @Transactional
    public void resetPassword(String rawToken, String newPassword) {
        if (newPassword == null || newPassword.length() < 8) {
            throw new BadCredentialsException("La contraseña debe tener al menos 8 caracteres");
        }

        PasswordResetToken resetToken = tokenRepository.findByToken(rawToken)
                .orElseThrow(() -> new ResourceNotFoundException("El enlace expiró o ya fue utilizado."));

        if (resetToken.isUsed()) {
            throw new BadCredentialsException("El enlace expiró o ya fue utilizado.");
        }

        if (resetToken.getExpiresAt().isBefore(LocalDateTime.now())) {
            throw new BadCredentialsException("El enlace expiró o ya fue utilizado.");
        }

        Account account = accountRepository.findById(resetToken.getAccountId())
                .orElseThrow(() -> new ResourceNotFoundException("Cuenta no encontrada"));

        account.setPassword(sha256(newPassword));
        accountRepository.save(account);

        resetToken.setUsed(true);
        tokenRepository.save(resetToken);

        log.info("Password reset successfully for account {}", account.getId());
    }

    private String sha256(String input) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(input.getBytes(StandardCharsets.UTF_8));
            StringBuilder hex = new StringBuilder();
            for (byte b : hash) hex.append(String.format("%02x", b));
            return hex.toString();
        } catch (Exception e) {
            throw new RuntimeException("SHA-256 error", e);
        }
    }
}
