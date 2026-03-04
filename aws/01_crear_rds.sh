#!/bin/bash
# =============================================================
# Crear instancia RDS PostgreSQL — Ambiente DEV
# Ejecutar desde cualquier terminal con AWS CLI instalado
# =============================================================

# ─── CONFIGURACIÓN — edita estos valores ───────────────────
DB_INSTANCE_ID="vitalink-db-dev"
DB_NAME="vitalink_dev"
DB_USER="vitalink_admin"
DB_PASSWORD="VitaLink2024!"       # Cambia esto por una contraseña segura
REGION="us-east-1"                # Cambia a tu región preferida
# ───────────────────────────────────────────────────────────

echo ">>> [1/4] Verificando AWS CLI..."
aws --version || { echo "ERROR: AWS CLI no está instalado"; exit 1; }
aws sts get-caller-identity || { echo "ERROR: Ejecuta: aws configure"; exit 1; }

echo ""
echo ">>> [2/4] Creando Security Group para RDS DEV..."
SG_ID=$(aws ec2 create-security-group \
  --group-name "vitalink-rds-dev-sg" \
  --description "Security group VitaLink RDS DEV" \
  --region "$REGION" \
  --query 'GroupId' \
  --output text)

echo "Security Group creado: $SG_ID"

aws ec2 authorize-security-group-ingress \
  --group-id "$SG_ID" \
  --protocol tcp \
  --port 5432 \
  --cidr 0.0.0.0/0 \
  --region "$REGION"

echo "Regla de entrada creada (puerto 5432)"

echo ""
echo ">>> [3/4] Creando instancia RDS PostgreSQL DEV (esto tarda ~5 min)..."
aws rds create-db-instance \
  --db-instance-identifier "$DB_INSTANCE_ID" \
  --db-instance-class db.t3.micro \
  --engine postgres \
  --engine-version "16.3" \
  --master-username "$DB_USER" \
  --master-user-password "$DB_PASSWORD" \
  --db-name "$DB_NAME" \
  --allocated-storage 20 \
  --storage-type gp2 \
  --no-multi-az \
  --publicly-accessible \
  --vpc-security-group-ids "$SG_ID" \
  --backup-retention-period 3 \
  --region "$REGION" \
  --no-deletion-protection \
  --tags Key=Environment,Value=dev Key=Project,Value=VitaLink

echo ""
echo ">>> [4/4] Esperando a que la instancia esté disponible..."
aws rds wait db-instance-available \
  --db-instance-identifier "$DB_INSTANCE_ID" \
  --region "$REGION"

ENDPOINT=$(aws rds describe-db-instances \
  --db-instance-identifier "$DB_INSTANCE_ID" \
  --region "$REGION" \
  --query 'DBInstances[0].Endpoint.Address' \
  --output text)

echo ""
echo "=============================================="
echo " RDS PostgreSQL DEV listo"
echo "=============================================="
echo " Endpoint  : $ENDPOINT"
echo " Puerto    : 5432"
echo " Base de datos: $DB_NAME"
echo " Usuario   : $DB_USER"
echo " Contraseña: $DB_PASSWORD"
echo ""
echo " JDBC URL:"
echo " jdbc:postgresql://$ENDPOINT:5432/$DB_NAME"
echo "=============================================="
echo ""
echo "Pega el endpoint en application-dev.properties:"
echo "  DB_HOST=$ENDPOINT"
