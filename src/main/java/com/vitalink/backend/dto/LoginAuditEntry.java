package com.vitalink.backend.dto;

import com.vitalink.backend.entity.AuditEventType;

public class LoginAuditEntry {

    private String id;
    private String accountId;
    private String email;
    private AuditEventType eventType;
    private String ipAddress;
    private String userAgent;
    private String failureReason;
    private String createdAt;

    public LoginAuditEntry(String id, String accountId, String email, AuditEventType eventType,
                           String ipAddress, String userAgent, String failureReason, String createdAt) {
        this.id = id;
        this.accountId = accountId;
        this.email = email;
        this.eventType = eventType;
        this.ipAddress = ipAddress;
        this.userAgent = userAgent;
        this.failureReason = failureReason;
        this.createdAt = createdAt;
    }

    public String getId() { return id; }
    public String getAccountId() { return accountId; }
    public String getEmail() { return email; }
    public AuditEventType getEventType() { return eventType; }
    public String getIpAddress() { return ipAddress; }
    public String getUserAgent() { return userAgent; }
    public String getFailureReason() { return failureReason; }
    public String getCreatedAt() { return createdAt; }
}
