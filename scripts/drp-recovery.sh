#!/usr/bin/env bash
# ==============================================================================
# Script de Respaldo Automatizado y Recuperación ante Desastres (BCP/DRP)
# Proyecto: Everywhere Travel S.A.C.
# Cumplimiento: ISO 22301 / ISO/IEC 25010 (Fiabilidad y Recuperabilidad)
# Métricas Operativas: RTO <= 2 horas | RPO <= 15 minutos
# ==============================================================================

set -euo pipefail

BACKUP_DIR="/var/backups/everywhere"
DATE=$(date +'%Y%m%d_%H%M%S')
LOG_FILE="/var/log/everywhere_drp.log"
RETENTION_DAYS=30

mkdir -p "${BACKUP_DIR}"

log() {
    echo "[$(date +'%Y-%m-%d %H:%M:%S')] $1" | tee -a "${LOG_FILE}"
}

# ------------------------------------------------------------------------------
# 1. Función de Respaldo (Backup) Lógico Comprimido
# ------------------------------------------------------------------------------
hacer_respaldo() {
    log "INICIANDO RESPALDO DE BASE DE DATOS SUPABASE / POSTGRESQL..."
    
    # Variables de conexión (leídas desde variables de entorno seguras)
    DB_HOST="${DB_HOST:-db.supabase.co}"
    DB_PORT="${DB_PORT:-5432}"
    DB_NAME="${DB_NAME:-postgres}"
    DB_USER="${DB_USER:-postgres}"
    
    DUMP_FILE="${BACKUP_DIR}/everywhere_db_${DATE}.sql.gz"
    
    if PGPASSWORD="${DB_PASSWORD:-}" pg_dump -h "${DB_HOST}" -p "${DB_PORT}" -U "${DB_USER}" -d "${DB_NAME}" \
        --no-owner --no-privileges --clean --if-exists | gzip -9 > "${DUMP_FILE}"; then
        
        FILE_SIZE=$(du -h "${DUMP_FILE}" | cut -f1)
        log "Respaldo exitoso: ${DUMP_FILE} (${FILE_SIZE})"
        
        # Rotación: Eliminar respaldos mayores a RETENTION_DAYS días
        find "${BACKUP_DIR}" -type f -name "everywhere_db_*.sql.gz" -mtime +"${RETENTION_DAYS}" -delete
        log "Rotación de respaldos completada (Retención: ${RETENTION_DAYS} días)"
    else
        log "ERROR CRÍTICO: Falló la generación del volcado de base de datos."
        exit 1
    fi
}

# ------------------------------------------------------------------------------
# 2. Función de Recuperación ante Desastres (Disaster Recovery - DRP)
# ------------------------------------------------------------------------------
recuperar_desastre() {
    local ARCHIVO_RESTAURAR="${1:-}"
    
    if [ -z "${ARCHIVO_RESTAURAR}" ]; then
        # Tomar el último backup disponible
        ARCHIVO_RESTAURAR=$(ls -t "${BACKUP_DIR}"/everywhere_db_*.sql.gz | head -n 1)
    fi
    
    if [ ! -f "${ARCHIVO_RESTAURAR}" ]; then
        log "ERROR: No se encontró el archivo de restauración: ${ARCHIVO_RESTAURAR}"
        exit 1
    fi
    
    log "EJECUTANDO RESTAURACIÓN DE EMERGENCIA (DRP) DESDE: ${ARCHIVO_RESTAURAR}"
    log "RTO Objetivo: <= 45 minutos para restauración de datos"
    
    DB_HOST="${DB_HOST:-db.supabase.co}"
    DB_PORT="${DB_PORT:-5432}"
    DB_NAME="${DB_NAME:-postgres}"
    DB_USER="${DB_USER:-postgres}"
    
    if gunzip -c "${ARCHIVO_RESTAURAR}" | PGPASSWORD="${DB_PASSWORD:-}" psql -h "${DB_HOST}" -p "${DB_PORT}" -U "${DB_USER}" -d "${DB_NAME}"; then
        log "RESTAURACIÓN COMPLETADA CON ÉXITO. Validando integridad de tablas críticas..."
        # Validación de integridad de tablas principales
        PGPASSWORD="${DB_PASSWORD:-}" psql -h "${DB_HOST}" -p "${DB_PORT}" -U "${DB_USER}" -d "${DB_NAME}" -c \
            "SELECT count(*) as total_cotizaciones FROM cotizacion; SELECT count(*) as total_personas FROM persona;"
        log "Base de datos restablecida satisfactoriamente."
    else
        log "ERROR CRÍTICO DURANTE LA RESTAURACIÓN."
        exit 1
    fi
}

# ------------------------------------------------------------------------------
# Menú de Ejecución
# ------------------------------------------------------------------------------
case "${1:-backup}" in
    backup)
        hacer_respaldo
        ;;
    restore)
        recuperar_desastre "${2:-}"
        ;;
    *)
        echo "Uso: $0 {backup|restore [archivo.sql.gz]}"
        exit 1
        ;;
esac

