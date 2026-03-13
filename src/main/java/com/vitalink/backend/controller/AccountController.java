package com.vitalink.backend.controller;

import com.vitalink.backend.dto.StatusUpdateRequest;
import com.vitalink.backend.entity.Account;
import com.vitalink.backend.service.AccountService;
import com.vitalink.backend.service.QrAlertService;
import com.vitalink.backend.service.RateLimitService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/accounts")
public class AccountController {

    private final AccountService accountService;
    private final RateLimitService rateLimitService;
    private final QrAlertService qrAlertService;

    public AccountController(AccountService accountService,
                             RateLimitService rateLimitService,
                             QrAlertService qrAlertService) {
        this.accountService = accountService;
        this.rateLimitService = rateLimitService;
        this.qrAlertService = qrAlertService;
    }

    @GetMapping
    public ResponseEntity<List<Account>> getAll() {
        return ResponseEntity.ok(accountService.getAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Account> getById(@PathVariable String id) {
        return ResponseEntity.ok(accountService.getById(id));
    }

    @GetMapping("/security/{securityAccount}")
    public ResponseEntity<Account> getBySecurityAccount(@PathVariable String securityAccount,
                                                        HttpServletRequest request) {
//        if (rateLimitService.isBlocked(request, "/api/v1/accounts/security")) {
//            return ResponseEntity.status(429).build();
//        }
        Account account = accountService.getBySecurityAccount(securityAccount);
        qrAlertService.notifyQrScanned(account);
        return ResponseEntity.ok(account);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Account> update(@PathVariable String id, @RequestBody Account data) {
        return ResponseEntity.ok(accountService.update(id, data));
    }

    @PatchMapping("/{id}/status")
    public ResponseEntity<Account> updateStatus(@PathVariable String id,
                                                @RequestBody StatusUpdateRequest request) {
        return ResponseEntity.ok(accountService.updateStatus(id, request.getStatus()));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable String id) {
        accountService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
