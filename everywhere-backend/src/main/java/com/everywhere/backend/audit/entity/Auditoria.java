package com.everywhere.backend.audit.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

/**
 * Tabla Maestra Única de Auditoría del Sistema.
 * Registra centralizadamente todas las acciones (creación, edición, eliminación y consulta sensible)
 * sobre cualquier entidad de EveryWhere Travel.
 */
@Data
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(
    name = "auditoria",
    indexes = {
        @Index(name = "idx_audi_tabla_reg", columnList = "audi_tabla_vac, audi_registro_id_vac"),
        @Index(name = "idx_audi_usuario", columnList = "audi_usuario_id_int"),
        @Index(name = "idx_audi_fecha", columnList = "audi_fec_cre_tmp"),
        @Index(name = "idx_audi_accion", columnList = "audi_accion_vac")
    }
)
public class Auditoria {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "audi_id_int")
    private Long id;

    /**
     * Nombre de la tabla o entidad intervenida (ej. 'cotizaciones', 'personas', 'liquidaciones', 'usuarios', 'auditoria')
     */
    @Column(name = "audi_tabla_vac", length = 100, nullable = false)
    private String tabla;

    /**
     * Identificador del registro modificado o creado (ej: '45')
     */
    @Column(name = "audi_registro_id_vac", length = 100, nullable = false)
    private String registroId;

    /**
     * Tipo de acción: 'CREACION', 'MODIFICACION', 'ELIMINACION', 'CONSULTA'
     */
    @Column(name = "audi_accion_vac", length = 20, nullable = false)
    private String accion;

    /**
     * ID del usuario autenticado que ejecutó la acción
     */
    @Column(name = "audi_usuario_id_int")
    private Integer usuarioId;

    /**
     * Email / username inmutable del usuario
     */
    @Column(name = "audi_usuario_username_vac", length = 150)
    private String username;

    /**
     * Nombre completo del usuario al momento de la acción
     */
    @Column(name = "audi_usuario_nombre_vac", length = 255)
    private String usuarioNombre;

    /**
     * Snapshot JSON de cómo estaban los datos ANTES del cambio (null en CREACION)
     */
    @Column(name = "audi_datos_previos_json", columnDefinition = "TEXT")
    private String datosPrevios;

    /**
     * Snapshot JSON de cómo quedaron los datos DESPUÉS del cambio (null en ELIMINACION)
     */
    @Column(name = "audi_datos_nuevos_json", columnDefinition = "TEXT")
    private String datosNuevos;

    /**
     * Diff resumido con únicamente los campos que cambiaron:
     * { "monto": { "antes": 100.0, "ahora": 150.0 } }
     */
    @Column(name = "audi_cambios_diff_json", columnDefinition = "TEXT")
    private String cambiosDiff;

    /**
     * Dirección IP del cliente que originó la petición (con soporte para proxies)
     */
    @Column(name = "audi_ip_origen_vac", length = 50)
    private String ipOrigen;

    /**
     * Endpoint invocado (ej. 'PUT /api/v1/cotizaciones/45')
     */
    @Column(name = "audi_endpoint_vac", length = 255)
    private String endpoint;

    /**
     * Navegador / Cliente User-Agent
     */
    @Column(name = "audi_user_agent_vac", length = 300)
    private String userAgent;

    /**
     * Fecha y hora exacta del registro
     */
    @CreationTimestamp
    @Column(name = "audi_fec_cre_tmp", updatable = false)
    private LocalDateTime fechaHora;
}
