-- =============================================================
-- PASO 3: Schema inicial para VitaLink en PostgreSQL
-- Hibernate lo crea automáticamente con ddl-auto=update,
-- pero este script sirve para revisión o creación manual.
-- =============================================================

-- Conectar con:
-- psql -h TU_ENDPOINT.rds.amazonaws.com -U vitalink_admin -d vitalink -f 03_init_schema.sql

-- Tabla de cuentas de usuario
CREATE TABLE IF NOT EXISTS accounts (
    id                VARCHAR(36)  PRIMARY KEY,
    email             VARCHAR(255) NOT NULL UNIQUE,
    password          VARCHAR(64)  NOT NULL,         -- SHA-256 hex = 64 chars
    role              VARCHAR(20)  NOT NULL DEFAULT 'USER',
    security_account  VARCHAR(255) UNIQUE,
    qr_data_url       TEXT,
    created_at        VARCHAR(50)
);

-- Tabla de perfiles médicos (datos en JSON como TEXT)
CREATE TABLE IF NOT EXISTS profiles (
    id          BIGSERIAL    PRIMARY KEY,
    account_id  VARCHAR(36)  UNIQUE,
    personal    TEXT,        -- JSON: PersonalData
    medical     TEXT,        -- JSON: MedicalData
    contacts    TEXT,        -- JSON: List<Contact>
    visibility  TEXT         -- JSON: ProfileVisibility
);

-- Índices de búsqueda frecuente
CREATE INDEX IF NOT EXISTS idx_accounts_email            ON accounts(email);
CREATE INDEX IF NOT EXISTS idx_accounts_security_account ON accounts(security_account);
CREATE INDEX IF NOT EXISTS idx_profiles_account_id       ON profiles(account_id);

-- Verificar
SELECT 'accounts' AS tabla, COUNT(*) AS registros FROM accounts
UNION ALL
SELECT 'profiles', COUNT(*) FROM profiles;
