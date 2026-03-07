package com.vitalink.backend.service.impl;

import com.vitalink.backend.entity.Account;
import com.vitalink.backend.entity.ActivationToken;
import com.vitalink.backend.exception.BadCredentialsException;
import com.vitalink.backend.exception.ResourceNotFoundException;
import com.vitalink.backend.repository.AccountRepository;
import com.vitalink.backend.repository.ActivationTokenRepository;
import com.vitalink.backend.service.ActivationService;
import com.vitalink.backend.service.EmailService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.UUID;

// Agente 2: Activation — gestiona el ciclo de vida del token de activación
@Service
public class ActivationServiceImpl implements ActivationService {

    private static final Logger log = LoggerFactory.getLogger(ActivationServiceImpl.class);

    private final ActivationTokenRepository tokenRepository;
    private final AccountRepository accountRepository;
    private final EmailService emailService;

    public ActivationServiceImpl(ActivationTokenRepository tokenRepository,
                                 AccountRepository accountRepository,
                                 EmailService emailService) {
        this.tokenRepository = tokenRepository;
        this.accountRepository = accountRepository;
        this.emailService = emailService;
    }

    @Override
    @Transactional
    public void createAndSendToken(String accountId, String email) {
        tokenRepository.deleteByAccountId(accountId);

        ActivationToken token = new ActivationToken();
        token.setId(UUID.randomUUID().toString());
        token.setAccountId(accountId);
        token.setToken(UUID.randomUUID().toString().replace("-", ""));
        token.setCreatedAt(LocalDateTime.now());
        token.setExpiresAt(LocalDateTime.now().plusHours(24));
        token.setUsed(false);

        tokenRepository.save(token);
        emailService.sendActivationEmail(email, token.getToken());
        log.info("Activation token created for account {}", accountId);
    }

    @Override
    @Transactional
    public void activateAccount(String rawToken) {
        ActivationToken token = tokenRepository.findByToken(rawToken)
                .orElseThrow(() -> new ResourceNotFoundException("Token de activación inválido"));

        if (token.isUsed()) {
            throw new BadCredentialsException("El token de activación ya fue utilizado");
        }

        if (token.getExpiresAt().isBefore(LocalDateTime.now())) {
            throw new BadCredentialsException("El token de activación ha expirado");
        }

        Account account = accountRepository.findById(token.getAccountId())
                .orElseThrow(() -> new ResourceNotFoundException("Cuenta no encontrada"));

        account.setStatus("ACTIVO");
        accountRepository.save(account);

        token.setUsed(true);
        tokenRepository.save(token);

        log.info("Account {} activated successfully", account.getId());
    }

    @Override
    @Transactional
    public void resendToken(String email) {
        Account account = accountRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("No existe una cuenta con ese email"));

        if ("ACTIVO".equalsIgnoreCase(account.getStatus())) {
            throw new BadCredentialsException("La cuenta ya está activa");
        }

        createAndSendToken(account.getId(), account.getEmail());
        log.info("Activation token resent for account {}", account.getId());
    }
}
