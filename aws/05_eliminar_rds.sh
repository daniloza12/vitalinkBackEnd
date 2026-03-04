#!/bin/bash
# =============================================================
# PASO 5 (OPCIONAL): Eliminar la instancia RDS para evitar cobros
# ⚠ ADVERTENCIA: Esto borra todos los datos permanentemente
# =============================================================

DB_INSTANCE_ID="vitalink-db"
REGION="us-east-1"

echo "⚠ ADVERTENCIA: Vas a eliminar la instancia RDS '$DB_INSTANCE_ID'"
echo "Todos los datos serán borrados permanentemente."
read -p "¿Continuar? (escribe 'ELIMINAR' para confirmar): " CONFIRM

if [ "$CONFIRM" != "ELIMINAR" ]; then
  echo "Cancelado."
  exit 0
fi

echo ">>> Eliminando instancia RDS..."
aws rds delete-db-instance \
  --db-instance-identifier "$DB_INSTANCE_ID" \
  --skip-final-snapshot \
  --region "$REGION"

echo ">>> Esperando eliminación completa..."
aws rds wait db-instance-deleted \
  --db-instance-identifier "$DB_INSTANCE_ID" \
  --region "$REGION"

echo "✓ Instancia eliminada."

# Opcional: eliminar también el Security Group
read -p "¿Eliminar el Security Group 'vitalink-rds-sg'? (s/n): " DEL_SG
if [ "$DEL_SG" = "s" ]; then
  SG_ID=$(aws ec2 describe-security-groups \
    --filters "Name=group-name,Values=vitalink-rds-sg" \
    --query 'SecurityGroups[0].GroupId' \
    --output text \
    --region "$REGION")
  aws ec2 delete-security-group --group-id "$SG_ID" --region "$REGION"
  echo "✓ Security Group eliminado."
fi
