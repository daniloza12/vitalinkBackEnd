package com.vitalink.backend.repository;

import com.vitalink.backend.entity.LoginAuditLog;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.Instant;
import java.util.List;

public interface LoginAuditLogRepository extends JpaRepository<LoginAuditLog, String> {

    List<LoginAuditLog> findByCreatedAtBetweenOrderByCreatedAtDesc(Instant from, Instant to);
}
