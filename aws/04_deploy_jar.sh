#!/bin/bash
# =============================================================
# Build y run del JAR — selecciona perfil dev o prod
# Uso:
#   bash 04_deploy_jar.sh        → perfil dev  (por defecto)
#   bash 04_deploy_jar.sh prod   → perfil prod
# =============================================================

MVN="/c/Users/de_a_/.m2/wrapper/dists/apache-maven-3.9.10-bin/53h08a94dg6djh6umvruv7q564/apache-maven-3.9.10/bin/mvn"
JAVA_HOME="/c/Program Files/Java/jdk-17"
export JAVA_HOME

PROFILE="${1:-dev}"

# ─── CONFIGURACIÓN — edita el endpoint según el ambiente ───
if [ "$PROFILE" = "prod" ]; then
  export DB_HOST="TU_ENDPOINT_PROD.rds.amazonaws.com"
  export DB_NAME="vitalink"
else
  export DB_HOST="TU_ENDPOINT_DEV.rds.amazonaws.com"   # endpoint del paso 1
  export DB_NAME="vitalink_dev"
fi

export DB_USER="vitalink_admin"
export DB_PASSWORD="VitaLink2024!"
# ───────────────────────────────────────────────────────────

echo ">>> Perfil: $PROFILE"
echo ">>> DB_HOST: $DB_HOST / DB_NAME: $DB_NAME"
echo ""

echo ">>> [1/2] Construyendo JAR..."
"$MVN" clean package -DskipTests -q || { echo "ERROR: Build fallido"; exit 1; }

JAR=$(ls target/*.jar | grep -v original | head -1)
echo "JAR: $JAR"

echo ""
echo ">>> [2/2] Iniciando backend..."
"$JAVA_HOME/bin/java" -jar "$JAR" \
  --spring.profiles.active="$PROFILE" \
  --DB_HOST="$DB_HOST" \
  --DB_NAME="$DB_NAME" \
  --DB_USER="$DB_USER" \
  --DB_PASSWORD="$DB_PASSWORD"
