package com.everywhere.backend.audit.service;

import com.everywhere.backend.audit.dto.AuditoriaDetalleDTO;
import com.everywhere.backend.audit.dto.AuditoriaResponseDTO;
import com.everywhere.backend.audit.dto.AuditoriaStatsDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;
import java.util.List;

public interface AuditService {

    /**
     * Registra una acción de inserción, modificación o eliminación sobre una tabla/entidad.
     */
    void registrar(String tabla, String registroId, String accion, Object estadoPrevio, Object estadoNuevo);

    /**
     * Registra una acción de consulta sensible (ej. auditoría consultando la misma auditoría).
     */
    void registrarConsulta(String tabla, String observacion);

    /**
     * Listado paginado con filtros multicriterio.
     */
    Page<AuditoriaResponseDTO> listar(
            String tabla,
            String accion,
            String username,
            LocalDateTime fechaInicio,
            LocalDateTime fechaFin,
            String searchTerm,
            Pageable pageable
    );

    /**
     * Detalle completo de un registro de auditoría, incluyendo los snapshots JSON y el diff.
     */
    AuditoriaDetalleDTO obtenerPorId(Long id);

    /**
     * Lista de tablas/módulos únicos registrados en el log.
     */
    List<String> listarTablas();

    /**
     * Estadísticas globales y del día para el panel de control.
     */
    AuditoriaStatsDTO obtenerEstadisticas();
}
