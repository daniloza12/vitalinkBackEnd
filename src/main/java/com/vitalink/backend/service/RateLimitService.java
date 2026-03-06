package com.vitalink.backend.service;

import com.vitalink.backend.entity.RateLimit;
import com.vitalink.backend.repository.RateLimitRepository;
import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Service
public class RateLimitService {

    private static final Logger log = LoggerFactory.getLogger(RateLimitService.class);

    private final RateLimitRepository rateLimitRepository;

    @Value("${ratelimit.max-hits:10}")
    private int maxHits;

    @Value("${ratelimit.time-hit:2}")
    private int timeHitMinutes;

    @Value("${ratelimit.block-minutes:4}")
    private int blockMinutes;

    public RateLimitService(RateLimitRepository rateLimitRepository) {
        this.rateLimitRepository = rateLimitRepository;
    }

    /**
     * Retorna true si la IP está bloqueada (debe rechazarse la petición).
     */
    @Transactional
    public boolean isBlocked(HttpServletRequest request, String endpoint) {
        String ip = getClientIp(request);


        long hitsInWindow = rateLimitRepository.countHitsInWindow(ip);
        if (hitsInWindow >= maxHits) {
            log.warn("IP blocked - IP: {} hits: {} ", ip, hitsInWindow);
            return true;
        }


        LocalDateTime now = LocalDateTime.now();
        LocalDateTime blockedUntil = now.plusMinutes(blockMinutes);
        rateLimitRepository.save(new RateLimit(null, ip, endpoint, blockedUntil));

        return false;
    }

    private String getClientIp(HttpServletRequest request) {
        String forwarded = request.getHeader("X-Forwarded-For");
        if (forwarded != null && !forwarded.isEmpty()) {
            return forwarded.split(",")[0].trim();
        }
        return request.getRemoteAddr();
    }
}
