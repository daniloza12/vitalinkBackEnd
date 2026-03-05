//package com.vitalink.backend.filter;
//
//import com.vitalink.backend.entity.RateLimit;
//import com.vitalink.backend.repository.RateLimitRepository;
//import jakarta.servlet.FilterChain;
//import jakarta.servlet.ServletException;
//import jakarta.servlet.http.HttpServletRequest;
//import jakarta.servlet.http.HttpServletResponse;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//import org.springframework.beans.factory.annotation.Value;
//import org.springframework.stereotype.Component;
//import org.springframework.web.filter.OncePerRequestFilter;
//
//import java.io.IOException;
//import java.time.LocalDateTime;
//import java.util.List;
//import java.util.Optional;
//
//@Component
//public class RateLimitFilter extends OncePerRequestFilter {
//
//    private static final Logger log = LoggerFactory.getLogger(RateLimitFilter.class);
//
//    private static final List<String> PROTECTED_ENDPOINTS = List.of(
//        "/api/v1/accounts/security/",
//        "/api/v1/profiles/"
//    );
//
//    private final RateLimitRepository rateLimitRepository;
//
//    @Value("${ratelimit.max-hits:10}")
//    private int maxHits;
//
//    @Value("${ratelimit.time-hit:5}")
//    private int timeHitMinutes;
//
//    @Value("${ratelimit.block-minutes:120}")
//    private int blockMinutes;
//
//    public RateLimitFilter(RateLimitRepository rateLimitRepository) {
//        this.rateLimitRepository = rateLimitRepository;
//    }
//
//    @Override
//    protected void doFilterInternal(HttpServletRequest request,
//                                    HttpServletResponse response,
//                                    FilterChain chain) throws ServletException, IOException {
//
//        String uri = request.getRequestURI();
//        String matchedEndpoint = PROTECTED_ENDPOINTS.stream()
//                .filter(uri::startsWith)
//                .findFirst()
//                .orElse(null);
//
//        if (matchedEndpoint == null) {
//            chain.doFilter(request, response);
//            return;
//        }
//
//        String ip = getClientIp(request);
//        LocalDateTime now = LocalDateTime.now();
//
//        // 1. Verificar si la IP está bloqueada actualmente
//        Optional<RateLimit> activeBlock = rateLimitRepository.findActiveBlock(ip, now);
//        if (activeBlock.isPresent()) {
//            log.warn("Blocked IP attempt - IP: {} endpoint: {} blocked until: {}", ip, matchedEndpoint, activeBlock.get().getBlockedUntil());
//            sendTooManyRequests(response, "Access blocked. Try again later.");
//            return;
//        }
//
//        // 2. Registrar el hit actual
//        RateLimit hit = new RateLimit(null, ip, matchedEndpoint, now, null);
//        rateLimitRepository.save(hit);
//
//        // 3. Contar hits dentro de la ventana de tiempo
//        LocalDateTime windowStart = now.minusMinutes(timeHitMinutes);
//        long hitsInWindow = rateLimitRepository.countHitsInWindow(ip, matchedEndpoint, windowStart);
//
//        // 4. Si supera el límite, bloquear la IP
//        if (hitsInWindow >= maxHits) {
//            LocalDateTime blockedUntil = now.plusMinutes(blockMinutes);
//            rateLimitRepository.blockIp(ip, matchedEndpoint, blockedUntil);
//            log.warn("IP blocked - IP: {} endpoint: {} hits: {} blocked until: {}", ip, matchedEndpoint, hitsInWindow, blockedUntil);
//            sendTooManyRequests(response, "Too many requests. Access blocked for " + blockMinutes + " minutes.");
//            return;
//        }
//
//        chain.doFilter(request, response);
//    }
//
//    private String getClientIp(HttpServletRequest request) {
//        String forwarded = request.getHeader("X-Forwarded-For");
//        if (forwarded != null && !forwarded.isEmpty()) {
//            return forwarded.split(",")[0].trim();
//        }
//        return request.getRemoteAddr();
//    }
//
//    private void sendTooManyRequests(HttpServletResponse response, String message) throws IOException {
//        response.setStatus(429);
//        response.setContentType("application/json");
//        response.getWriter().write("{\"error\":\"" + message + "\"}");
//    }
//}
