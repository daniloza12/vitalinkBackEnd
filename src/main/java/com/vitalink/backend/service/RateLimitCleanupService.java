package com.vitalink.backend.service;

import com.vitalink.backend.repository.RateLimitRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
public class RateLimitCleanupService {

    private static final Logger log = LoggerFactory.getLogger(RateLimitCleanupService.class);

    private final RateLimitRepository rateLimitRepository;

    public RateLimitCleanupService(RateLimitRepository rateLimitRepository) {
        this.rateLimitRepository = rateLimitRepository;
    }

    // Ejecuta cada 60 segundos
//    @Scheduled(fixedDelay = 60000)
//    @Transactional
    public void cleanupExpiredRecords() {
        LocalDateTime now = LocalDateTime.now();
        int deleted = rateLimitRepository.deleteExpiredBlocks(now);

        if (deleted > 0) {
            log.info("Rate limit cleanup: {} registros eliminados (now: {})", deleted, now);
        }
    }
}
