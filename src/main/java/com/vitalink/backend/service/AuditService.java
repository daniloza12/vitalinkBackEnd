package com.vitalink.backend.service;

import com.vitalink.backend.dto.LoginAuditEntry;
import com.vitalink.backend.entity.AuditEventType;

import java.time.Instant;
import java.util.List;

public interface AuditService {

    void record(String email, String accountId, AuditEventType eventType,
                String ipAddress, String userAgent, String failureReason);

    List<LoginAuditEntry> getLogs(Instant from, Instant to);
}
