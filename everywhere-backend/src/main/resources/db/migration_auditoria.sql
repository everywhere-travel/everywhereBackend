-- ==============================================================================
-- EVERYWHERE TRAVEL - SCRIPT DE CREACIÓN DE TABLA MAESTRA DE AUDITORÍA
-- ==============================================================================

-- 1. Creación de la tabla maestra centralizada
CREATE TABLE IF NOT EXISTS auditoria (
    audi_id_int             BIGSERIAL PRIMARY KEY,
    audi_tabla_vac          VARCHAR(100) NOT NULL,
    audi_registro_id_vac    VARCHAR(100) NOT NULL,
    audi_accion_vac         VARCHAR(20) NOT NULL,
    audi_usuario_id_int     INTEGER,
    audi_usuario_username_vac VARCHAR(150),
    audi_usuario_nombre_vac VARCHAR(255),
    audi_datos_previos_json TEXT,
    audi_datos_nuevos_json  TEXT,
    audi_cambios_diff_json  TEXT,
    audi_ip_origen_vac      VARCHAR(50),
    audi_endpoint_vac       VARCHAR(255),
    audi_user_agent_vac     VARCHAR(300),
    audi_fec_cre_tmp        TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT NOW()
);

-- 2. Índices para acelerar búsquedas y filtros en producción
CREATE INDEX IF NOT EXISTS idx_audi_tabla_reg ON auditoria (audi_tabla_vac, audi_registro_id_vac);
CREATE INDEX IF NOT EXISTS idx_audi_usuario ON auditoria (audi_usuario_id_int);
CREATE INDEX IF NOT EXISTS idx_audi_fecha ON auditoria (audi_fec_cre_tmp DESC);
CREATE INDEX IF NOT EXISTS idx_audi_accion ON auditoria (audi_accion_vac);

-- 3. Registro modular del permiso AUDITORIA:READ
INSERT INTO permission (perm_name_vc, perm_desc_vc, perm_cre_tmp, perm_upd_tmp)
SELECT 'AUDITORIA:READ', 'Permiso para visualizar el registro maestro de auditoría', NOW(), NOW()
WHERE NOT EXISTS (SELECT 1 FROM permission WHERE perm_name_vc = 'AUDITORIA:READ');

-- 4. Asignación automática del permiso al rol ADMIN (si existe)
INSERT INTO role_permission (rol_id, perm_id_int, rol_perm_cre_tmp)
SELECT r.rol_id, p.perm_id_int, NOW()
FROM role r
CROSS JOIN permission p
WHERE r.rol_nam_vc IN ('ADMIN', 'ROLE_ADMIN')
  AND p.perm_name_vc = 'AUDITORIA:READ'
  AND NOT EXISTS (
      SELECT 1 FROM role_permission rp 
      WHERE rp.rol_id = r.rol_id AND rp.perm_id_int = p.perm_id_int
  );
