package com.vitalink.backend.service.impl;

import com.vitalink.backend.dto.LoginAuditEntry;
import com.vitalink.backend.entity.AuditEventType;
import com.vitalink.backend.entity.LoginAuditLog;
import com.vitalink.backend.repository.LoginAuditLogRepository;
import com.vitalink.backend.service.AuditService;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Service
public class AuditServiceImpl implements AuditService {

    private static final DateTimeFormatter FORMATTER =
            DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss").withZone(ZoneId.of("America/Lima"));

    private final LoginAuditLogRepository repository;

    public AuditServiceImpl(LoginAuditLogRepository repository) {
        this.repository = repository;
    }

    @Override
    public void record(String email, String accountId, AuditEventType eventType,
                       String ipAddress, String userAgent, String failureReason) {
        LoginAuditLog log = new LoginAuditLog();
        log.setEmail(email);
        log.setAccountId(accountId);
        log.setEventType(eventType);
        log.setIpAddress(ipAddress);
        log.setUserAgent(userAgent);
        log.setFailureReason(failureReason);
        repository.save(log);
    }

    @Override
    public List<LoginAuditEntry> getLogs(Instant from, Instant to) {
        List<LoginAuditEntry> list = null;
        try {
            list = repository.findByCreatedAtBetweenOrderByCreatedAtDesc(from, to)
                    .stream()
                    .map(l -> new LoginAuditEntry(
                            l.getId(), l.getAccountId(), l.getEmail(), l.getEventType(),
                            l.getIpAddress(), l.getUserAgent(), l.getFailureReason(),
                            FORMATTER.format(l.getCreatedAt())))
                    .toList();

        } catch (RuntimeException e) {
            throw new RuntimeException(e);
        }

        return list;
    }
}
