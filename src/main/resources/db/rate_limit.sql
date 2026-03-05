-- Tabla de rate limiting
-- Registra cada petición por IP y endpoint
-- Permite bloqueo temporal configurable

CREATE TABLE IF NOT EXISTS rate_limit (
    id            BIGSERIAL       PRIMARY KEY,
    ip            VARCHAR(45)     NOT NULL,
    endpoint      VARCHAR(100)    NOT NULL,
    hit_time      VARCHAR(16)     NOT NULL,  -- formato: dd/MM/yyyy HH:mm
    blocked_until TIMESTAMP       NULL
);

-- Índices para optimizar las consultas frecuentes
CREATE INDEX IF NOT EXISTS idx_rate_limit_ip ON rate_limit (ip);
CREATE INDEX IF NOT EXISTS idx_rate_limit_ip_endpoint ON rate_limit (ip, endpoint);
CREATE INDEX IF NOT EXISTS idx_rate_limit_blocked_until ON rate_limit (blocked_until);
CREATE INDEX IF NOT EXISTS idx_rate_limit_hit_time ON rate_limit (hit_time);
