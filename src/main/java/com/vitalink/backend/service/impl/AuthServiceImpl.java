package com.vitalink.backend.service.impl;

import com.vitalink.backend.entity.Account;
import com.vitalink.backend.entity.AuditEventType;
import com.vitalink.backend.exception.BadCredentialsException;
import com.vitalink.backend.exception.ConflictException;
import com.vitalink.backend.repository.AccountRepository;
import com.vitalink.backend.service.AuditService;
import com.vitalink.backend.service.AuthService;
import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Optional;
import java.util.UUID;

@Service
public class AuthServiceImpl implements AuthService {

    private static final Logger log = LoggerFactory.getLogger(AuthServiceImpl.class);

    private final AccountRepository accountRepository;
    private final AuditService auditService;

    public AuthServiceImpl(AccountRepository accountRepository, AuditService auditService) {
        this.accountRepository = accountRepository;
        this.auditService = auditService;
    }

    private void safeRecord(String email, String accountId, AuditEventType eventType,
                            String ip, String userAgent, String reason) {
        try {
            auditService.record(email, accountId, eventType, ip, userAgent, reason);
        } catch (Exception e) {
            log.warn("Audit record failed (non-critical): {}", e.getMessage());
        }
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
        account.setStatus("REGISTRADO");
        account.setSecurityAccount(securityAccount);
        account.setQrDataUrl(null);
        account.setCreatedAt(createdAt);

        Account saved = accountRepository.save(account);

        HttpServletRequest req = getCurrentRequest();
        safeRecord(email, saved.getId(), AuditEventType.REGISTER,
                extractIp(req), extractUserAgent(req), null);

        return saved;
    }

    @Override
    public Account login(String email, String password) {
        HttpServletRequest req = getCurrentRequest();
        String ip = extractIp(req);
        String userAgent = extractUserAgent(req);

        Optional<Account> optAccount = accountRepository.findByEmail(email);
        if (optAccount.isEmpty()) {
            safeRecord(email, null, AuditEventType.LOGIN_FAILED, ip, userAgent, "USER_NOT_FOUND");
            throw new BadCredentialsException("Invalid credentials");
        }

        Account account = optAccount.get();
        String hashedPassword = sha256(password);
        if (!hashedPassword.equals(account.getPassword())) {
            safeRecord(email, account.getId(), AuditEventType.LOGIN_FAILED, ip, userAgent, "WRONG_PASSWORD");
            throw new BadCredentialsException("Invalid credentials");
        }

        String status = account.getStatus();
        if (status == null || (!status.equalsIgnoreCase("REGISTRADO") && !status.equalsIgnoreCase("ACTIVO"))) {
            safeRecord(email, account.getId(), AuditEventType.LOGIN_FAILED, ip, userAgent, "ACCOUNT_INACTIVE");
            throw new BadCredentialsException("Account is not active");
        }

        safeRecord(email, account.getId(), AuditEventType.LOGIN_SUCCESS, ip, userAgent, null);
        return account;
    }

    private HttpServletRequest getCurrentRequest() {
        try {
            ServletRequestAttributes attrs = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
            return attrs != null ? attrs.getRequest() : null;
        } catch (Exception e) {
            return null;
        }
    }

    private String extractIp(HttpServletRequest req) {
        if (req == null) return null;
        String forwarded = req.getHeader("X-Forwarded-For");
        String ip = (forwarded != null && !forwarded.isEmpty())
                ? forwarded.split(",")[0].trim()
                : req.getRemoteAddr();
        if (ip != null && ip.contains(".") && ip.contains(":")) {
            ip = ip.substring(0, ip.lastIndexOf(':'));
        }
        return ip;
    }

    private String extractUserAgent(HttpServletRequest req) {
        return req != null ? req.getHeader("User-Agent") : null;
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
