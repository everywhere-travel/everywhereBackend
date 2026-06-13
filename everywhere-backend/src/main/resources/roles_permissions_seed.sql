-- ============================================================
--  SCRIPT DDL + SEED — Sistema de Roles y Permisos Dinámico
--  Everywhere Backend
--  
--  INSTRUCCIONES:
--  1. Ejecuta primero la sección DDL (crear tablas)
--  2. Luego ejecuta el SEED (datos iniciales)
--
--  NOTA: La tabla "user" usa comillas porque es palabra
--        reservada en PostgreSQL.
-- ============================================================


-- ============================================================
--  DDL — Crear tablas
-- ============================================================

-- 1. Tabla de permisos (acciones sobre módulos)
CREATE TABLE IF NOT EXISTS "permission" (
    "perm_id_int"  SERIAL          NOT NULL,
    "perm_name_vc" VARCHAR(100)    NOT NULL UNIQUE,
    "perm_desc_vc" VARCHAR(255),
    "perm_cre_tmp" TIMESTAMP       NOT NULL DEFAULT NOW(),
    "perm_upd_tmp" TIMESTAMP,
    PRIMARY KEY ("perm_id_int")
);

-- 2. Tabla de roles
CREATE TABLE IF NOT EXISTS "role" (
    "rol_id"      SERIAL       NOT NULL,
    "rol_nam_vc"  VARCHAR(100) NOT NULL UNIQUE,
    "rol_cre_tmp" TIMESTAMP    NOT NULL DEFAULT NOW(),
    "rol_upd_tmp" TIMESTAMP,
    PRIMARY KEY ("rol_id")
);

-- 3. Tabla pivote: permisos asignados a cada rol
CREATE TABLE IF NOT EXISTS "role_permission" (
    "rol_perm_id_int" SERIAL    NOT NULL,
    "rol_id"          INTEGER   NOT NULL,
    "perm_id_int"     INTEGER   NOT NULL,
    "rol_perm_cre_tmp" TIMESTAMP NOT NULL DEFAULT NOW(),
    PRIMARY KEY ("rol_perm_id_int"),
    CONSTRAINT "UQ_role_permission"
        UNIQUE ("rol_id", "perm_id_int"),
    CONSTRAINT "FK_role_permission_rol_id"
        FOREIGN KEY ("rol_id") REFERENCES "role" ("rol_id") ON DELETE CASCADE,
    CONSTRAINT "FK_role_permission_perm_id_int"
        FOREIGN KEY ("perm_id_int") REFERENCES "permission" ("perm_id_int") ON DELETE CASCADE
);


-- ============================================================
--  SEED — Datos iniciales (roles migrados del enum anterior)
-- ============================================================

-- Roles base (8 roles del enum original)
INSERT INTO "role" ("rol_nam_vc") VALUES
    ('GERENTE'),
    ('VENTAS'),
    ('ADMINISTRAR'),
    ('ADMIN'),
    ('OPERACIONES'),
    ('SISTEMAS'),
    ('VENTAS_JUNIOR'),
    ('GERENTE_ARGENTINA')
ON CONFLICT ("rol_nam_vc") DO NOTHING;


-- ============================================================
--  Permisos — Formato: MODULO:ACCION
--  Módulos: CLIENTES, VIAJEROS, VIAJEROS_FREC, COTIZACIONES,
--           RECIBOS, LIQUIDACIONES, PRODUCTOS, PROVEEDORES,
--           OPERADOR, CARPETA, PERSONAS, ALL_MODULES
--  Acciones: READ, CREATE, UPDATE, DELETE
-- ============================================================

INSERT INTO "permission" ("perm_name_vc", "perm_desc_vc") VALUES
    -- Comodines (acceso total)
    ('ALL_MODULES:READ',   'Lectura en todos los módulos'),
    ('ALL_MODULES:CREATE', 'Creación en todos los módulos'),
    ('ALL_MODULES:UPDATE', 'Actualización en todos los módulos'),
    ('ALL_MODULES:DELETE', 'Eliminación en todos los módulos'),

    -- Clientes
    ('CLIENTES:READ',   'Ver clientes'),
    ('CLIENTES:CREATE', 'Crear clientes'),
    ('CLIENTES:UPDATE', 'Editar clientes'),
    ('CLIENTES:DELETE', 'Eliminar clientes'),

    -- Viajeros
    ('VIAJEROS:READ',   'Ver viajeros'),
    ('VIAJEROS:CREATE', 'Crear viajeros'),
    ('VIAJEROS:UPDATE', 'Editar viajeros'),
    ('VIAJEROS:DELETE', 'Eliminar viajeros'),

    -- Viajeros frecuentes
    ('VIAJEROS_FREC:READ',   'Ver viajeros frecuentes'),
    ('VIAJEROS_FREC:CREATE', 'Crear viajeros frecuentes'),
    ('VIAJEROS_FREC:UPDATE', 'Editar viajeros frecuentes'),
    ('VIAJEROS_FREC:DELETE', 'Eliminar viajeros frecuentes'),

    -- Cotizaciones
    ('COTIZACIONES:READ',   'Ver cotizaciones'),
    ('COTIZACIONES:CREATE', 'Crear cotizaciones'),
    ('COTIZACIONES:UPDATE', 'Editar cotizaciones'),
    ('COTIZACIONES:DELETE', 'Eliminar cotizaciones'),

    -- Recibos
    ('RECIBOS:READ',   'Ver recibos'),
    ('RECIBOS:CREATE', 'Crear recibos'),
    ('RECIBOS:UPDATE', 'Editar recibos'),
    ('RECIBOS:DELETE', 'Eliminar recibos'),

    -- Liquidaciones
    ('LIQUIDACIONES:READ',   'Ver liquidaciones'),
    ('LIQUIDACIONES:CREATE', 'Crear liquidaciones'),
    ('LIQUIDACIONES:UPDATE', 'Editar liquidaciones'),
    ('LIQUIDACIONES:DELETE', 'Eliminar liquidaciones'),

    -- Productos
    ('PRODUCTOS:READ',   'Ver productos'),
    ('PRODUCTOS:CREATE', 'Crear productos'),
    ('PRODUCTOS:UPDATE', 'Editar productos'),
    ('PRODUCTOS:DELETE', 'Eliminar productos'),

    -- Proveedores
    ('PROVEEDORES:READ',   'Ver proveedores'),
    ('PROVEEDORES:CREATE', 'Crear proveedores'),
    ('PROVEEDORES:UPDATE', 'Editar proveedores'),
    ('PROVEEDORES:DELETE', 'Eliminar proveedores'),

    -- Operador
    ('OPERADOR:READ',   'Ver operadores'),
    ('OPERADOR:CREATE', 'Crear operadores'),
    ('OPERADOR:UPDATE', 'Editar operadores'),
    ('OPERADOR:DELETE', 'Eliminar operadores'),

    -- Carpeta
    ('CARPETA:READ',   'Ver carpetas'),
    ('CARPETA:CREATE', 'Crear carpetas'),
    ('CARPETA:UPDATE', 'Editar carpetas'),
    ('CARPETA:DELETE', 'Eliminar carpetas'),

    -- Personas
    ('PERSONAS:READ',   'Ver personas'),
    ('PERSONAS:CREATE', 'Crear personas'),
    ('PERSONAS:UPDATE', 'Editar personas'),
    ('PERSONAS:DELETE', 'Eliminar personas')

ON CONFLICT ("perm_name_vc") DO NOTHING;


-- ============================================================
--  Asignación de permisos a roles
--  (equivalente exacto al enum Role anterior)
-- ============================================================

-- GERENTE → ALL_MODULES (READ, CREATE, UPDATE, DELETE)
INSERT INTO "role_permission" ("rol_id", "perm_id_int")
SELECT r.rol_id, p.perm_id_int
FROM "role" r, "permission" p
WHERE r.rol_nam_vc = 'GERENTE'
  AND p.perm_name_vc IN (
      'ALL_MODULES:READ', 'ALL_MODULES:CREATE',
      'ALL_MODULES:UPDATE', 'ALL_MODULES:DELETE'
  )
ON CONFLICT ON CONSTRAINT "UQ_role_permission" DO NOTHING;


-- ADMIN → ALL_MODULES (READ, CREATE, UPDATE, DELETE)
INSERT INTO "role_permission" ("rol_id", "perm_id_int")
SELECT r.rol_id, p.perm_id_int
FROM "role" r, "permission" p
WHERE r.rol_nam_vc = 'ADMIN'
  AND p.perm_name_vc IN (
      'ALL_MODULES:READ', 'ALL_MODULES:CREATE',
      'ALL_MODULES:UPDATE', 'ALL_MODULES:DELETE'
  )
ON CONFLICT ON CONSTRAINT "UQ_role_permission" DO NOTHING;


-- SISTEMAS → ALL_MODULES (READ, CREATE, UPDATE, DELETE)
INSERT INTO "role_permission" ("rol_id", "perm_id_int")
SELECT r.rol_id, p.perm_id_int
FROM "role" r, "permission" p
WHERE r.rol_nam_vc = 'SISTEMAS'
  AND p.perm_name_vc IN (
      'ALL_MODULES:READ', 'ALL_MODULES:CREATE',
      'ALL_MODULES:UPDATE', 'ALL_MODULES:DELETE'
  )
ON CONFLICT ON CONSTRAINT "UQ_role_permission" DO NOTHING;


-- VENTAS → múltiples módulos
INSERT INTO "role_permission" ("rol_id", "perm_id_int")
SELECT r.rol_id, p.perm_id_int
FROM "role" r, "permission" p
WHERE r.rol_nam_vc = 'VENTAS'
  AND p.perm_name_vc IN (
      'CLIENTES:CREATE', 'CLIENTES:READ', 'CLIENTES:UPDATE',
      'VIAJEROS:CREATE', 'VIAJEROS:READ', 'VIAJEROS:UPDATE', 'VIAJEROS:DELETE',
      'VIAJEROS_FREC:CREATE', 'VIAJEROS_FREC:READ', 'VIAJEROS_FREC:UPDATE', 'VIAJEROS_FREC:DELETE',
      'COTIZACIONES:CREATE', 'COTIZACIONES:READ', 'COTIZACIONES:UPDATE', 'COTIZACIONES:DELETE',
      'RECIBOS:CREATE', 'RECIBOS:READ', 'RECIBOS:UPDATE', 'RECIBOS:DELETE',
      'LIQUIDACIONES:CREATE', 'LIQUIDACIONES:READ', 'LIQUIDACIONES:UPDATE', 'LIQUIDACIONES:DELETE',
      'PRODUCTOS:READ',
      'PROVEEDORES:READ',
      'OPERADOR:CREATE', 'OPERADOR:READ', 'OPERADOR:UPDATE', 'OPERADOR:DELETE',
      'CARPETA:CREATE', 'CARPETA:READ', 'CARPETA:UPDATE', 'CARPETA:DELETE'
  )
ON CONFLICT ON CONSTRAINT "UQ_role_permission" DO NOTHING;


-- ADMINISTRAR
INSERT INTO "role_permission" ("rol_id", "perm_id_int")
SELECT r.rol_id, p.perm_id_int
FROM "role" r, "permission" p
WHERE r.rol_nam_vc = 'ADMINISTRAR'
  AND p.perm_name_vc IN (
      'CLIENTES:CREATE', 'CLIENTES:READ', 'CLIENTES:UPDATE',
      'VIAJEROS:CREATE', 'VIAJEROS:READ', 'VIAJEROS:UPDATE',
      'COTIZACIONES:READ',
      'RECIBOS:READ',
      'LIQUIDACIONES:CREATE', 'LIQUIDACIONES:READ', 'LIQUIDACIONES:UPDATE',
      'PRODUCTOS:CREATE', 'PRODUCTOS:READ', 'PRODUCTOS:UPDATE', 'PRODUCTOS:DELETE',
      'PROVEEDORES:CREATE', 'PROVEEDORES:READ', 'PROVEEDORES:UPDATE', 'PROVEEDORES:DELETE',
      'OPERADOR:CREATE', 'OPERADOR:READ', 'OPERADOR:UPDATE', 'OPERADOR:DELETE',
      'CARPETA:CREATE', 'CARPETA:READ', 'CARPETA:UPDATE', 'CARPETA:DELETE'
  )
ON CONFLICT ON CONSTRAINT "UQ_role_permission" DO NOTHING;


-- OPERACIONES
INSERT INTO "role_permission" ("rol_id", "perm_id_int")
SELECT r.rol_id, p.perm_id_int
FROM "role" r, "permission" p
WHERE r.rol_nam_vc = 'OPERACIONES'
  AND p.perm_name_vc IN (
      'CLIENTES:CREATE', 'CLIENTES:READ', 'CLIENTES:UPDATE', 'CLIENTES:DELETE',
      'VIAJEROS:CREATE', 'VIAJEROS:READ', 'VIAJEROS:UPDATE', 'VIAJEROS:DELETE',
      'PRODUCTOS:CREATE', 'PRODUCTOS:READ', 'PRODUCTOS:UPDATE', 'PRODUCTOS:DELETE',
      'PROVEEDORES:CREATE', 'PROVEEDORES:READ', 'PROVEEDORES:UPDATE', 'PROVEEDORES:DELETE',
      'OPERADOR:CREATE', 'OPERADOR:READ', 'OPERADOR:UPDATE', 'OPERADOR:DELETE',
      'PERSONAS:CREATE', 'PERSONAS:READ', 'PERSONAS:UPDATE', 'PERSONAS:DELETE'
  )
ON CONFLICT ON CONSTRAINT "UQ_role_permission" DO NOTHING;


-- VENTAS_JUNIOR
INSERT INTO "role_permission" ("rol_id", "perm_id_int")
SELECT r.rol_id, p.perm_id_int
FROM "role" r, "permission" p
WHERE r.rol_nam_vc = 'VENTAS_JUNIOR'
  AND p.perm_name_vc IN (
      'CLIENTES:CREATE', 'CLIENTES:READ',
      'VIAJEROS:CREATE', 'VIAJEROS:READ',
      'COTIZACIONES:CREATE', 'COTIZACIONES:READ', 'COTIZACIONES:UPDATE',
      'RECIBOS:CREATE', 'RECIBOS:READ', 'RECIBOS:UPDATE',
      'LIQUIDACIONES:CREATE', 'LIQUIDACIONES:READ', 'LIQUIDACIONES:DELETE',
      'CARPETA:CREATE', 'CARPETA:READ',
      'PERSONAS:CREATE', 'PERSONAS:READ'
  )
ON CONFLICT ON CONSTRAINT "UQ_role_permission" DO NOTHING;


-- GERENTE_ARGENTINA
INSERT INTO "role_permission" ("rol_id", "perm_id_int")
SELECT r.rol_id, p.perm_id_int
FROM "role" r, "permission" p
WHERE r.rol_nam_vc = 'GERENTE_ARGENTINA'
  AND p.perm_name_vc IN (
      'CLIENTES:CREATE', 'CLIENTES:READ', 'CLIENTES:UPDATE', 'CLIENTES:DELETE',
      'VIAJEROS:CREATE', 'VIAJEROS:READ', 'VIAJEROS:UPDATE', 'VIAJEROS:DELETE',
      'COTIZACIONES:CREATE', 'COTIZACIONES:READ', 'COTIZACIONES:UPDATE', 'COTIZACIONES:DELETE',
      'RECIBOS:CREATE', 'RECIBOS:READ', 'RECIBOS:UPDATE', 'RECIBOS:DELETE',
      'LIQUIDACIONES:READ',
      'PROVEEDORES:READ',
      'OPERADOR:READ',
      'CARPETA:CREATE', 'CARPETA:READ',
      'PERSONAS:CREATE', 'PERSONAS:READ', 'PERSONAS:UPDATE', 'PERSONAS:DELETE'
  )
ON CONFLICT ON CONSTRAINT "UQ_role_permission" DO NOTHING;


-- ============================================================
--  VERIFICACIÓN — Ejecuta esto para ver el resultado
-- ============================================================
/*
SELECT r.rol_nam_vc AS rol, p.perm_name_vc AS permiso
FROM role_permission rp
JOIN "role" r ON r.rol_id = rp.rol_id
JOIN "permission" p ON p.perm_id_int = rp.perm_id_int
ORDER BY r.rol_nam_vc, p.perm_name_vc;
*/
