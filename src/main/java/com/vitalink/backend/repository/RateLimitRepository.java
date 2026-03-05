package com.vitalink.backend.repository;

import com.vitalink.backend.entity.RateLimit;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

public interface RateLimitRepository extends JpaRepository<RateLimit, Long> {

    // Cuenta hits de una IP sin importar endpoint
    @Query("SELECT COUNT(r) FROM RateLimit r WHERE r.ip = :ip")
    long countHitsInWindow(@Param("ip") String ip);

    // Elimina registros cuyo blockedUntil ya expiró
    @Modifying
    @Query("DELETE FROM RateLimit r WHERE r.blockedUntil IS NOT NULL AND r.blockedUntil < :now")
    int deleteExpiredBlocks(@Param("now") LocalDateTime now);
}
