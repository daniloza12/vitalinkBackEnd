package com.vitalink.backend.controller;

import com.vitalink.backend.dto.LoginAuditEntry;
import com.vitalink.backend.repository.AccountRepository;
import com.vitalink.backend.service.AuditService;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.ZoneOffset;
import java.time.Instant;
import java.util.List;

@RestController
@RequestMapping("/api/v1/audit")
public class AuditController {

    private final AuditService auditService;
    private final AccountRepository accountRepository;

    public AuditController(AuditService auditService, AccountRepository accountRepository) {
        this.auditService = auditService;
        this.accountRepository = accountRepository;
    }

    @GetMapping("/logins")
    public ResponseEntity<List<LoginAuditEntry>> getLogs(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate from,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate to){
            //@RequestHeader("Authorization") String authHeader
            // )


//        String token = authHeader.startsWith("Bearer ") ? authHeader.substring(7) : authHeader;
//        boolean isAdmin = accountRepository.findBySecurityAccount(token)
//                .map(a -> "ADMIN".equals(a.getRole()))
//                .orElse(false);
//        if (!isAdmin) {
//            return ResponseEntity.status(403).build();
//        }

        Instant fromInstant = from.atStartOfDay(ZoneOffset.UTC).toInstant();
        Instant toInstant = to.atTime(23, 59, 59).toInstant(ZoneOffset.UTC);
        return ResponseEntity.ok(auditService.getLogs(fromInstant, toInstant));
    }
}
