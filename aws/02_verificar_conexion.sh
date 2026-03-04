#!/bin/bash
# =============================================================
# PASO 2: Verificar conexión a la base de datos RDS
# Requiere: psql instalado localmente
#   Windows: choco install postgresql
#   Mac:     brew install postgresql
# =============================================================

# ─── CONFIGURACIÓN — edita estos valores ───────────────────
DB_HOST="TU_ENDPOINT_AQUI.rds.amazonaws.com"   # pegar el endpoint del paso 1
DB_NAME="vitalink"
DB_USER="vitalink_admin"
DB_PASSWORD="VitaLink2024!"
# ───────────────────────────────────────────────────────────

echo ">>> Probando conexión a RDS PostgreSQL..."
PGPASSWORD="$DB_PASSWORD" psql \
  -h "$DB_HOST" \
  -U "$DB_USER" \
  -d "$DB_NAME" \
  -c "\conninfo" \
  -c "SELECT version();"

if [ $? -eq 0 ]; then
  echo ""
  echo "✓ Conexión exitosa a la base de datos"
else
  echo ""
  echo "✗ No se pudo conectar. Verifica:"
  echo "  1. Que el Security Group permite el puerto 5432"
  echo "  2. Que 'Publicly Accessible' está activado en RDS"
  echo "  3. Que el endpoint es correcto"
fi
