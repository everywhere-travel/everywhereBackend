package com.everywhere.backend.audit.api;

import com.everywhere.backend.audit.dto.AuditoriaDetalleDTO;
import com.everywhere.backend.audit.dto.AuditoriaResponseDTO;
import com.everywhere.backend.audit.dto.AuditoriaStatsDTO;
import com.everywhere.backend.audit.service.AuditService;
import com.everywhere.backend.security.RequirePermission;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/auditoria")
@RequiredArgsConstructor
public class AuditoriaController {

    private final AuditService auditService;

    @GetMapping
    @RequirePermission(module = "AUDITORIA", permission = "READ")
    public ResponseEntity<Page<AuditoriaResponseDTO>> listarAuditorias(
            @RequestParam(required = false) String tabla,
            @RequestParam(required = false) String accion,
            @RequestParam(required = false) String username,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime fechaInicio,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime fechaFin,
            @RequestParam(required = false) String search,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "15") int size
    ) {
        // Auto-auditoría sobre la misma tabla maestra de auditoría
        auditService.registrarConsulta("auditoria", "Consulta paginada del registro maestro de auditoría");

        Pageable pageable = PageRequest.of(
                Math.max(0, page),
                Math.min(100, Math.max(1, size)),
                org.springframework.data.domain.Sort.by(org.springframework.data.domain.Sort.Direction.DESC, "fechaHora")
        );
        Page<AuditoriaResponseDTO> resultado = auditService.listar(tabla, accion, username, fechaInicio, fechaFin, search, pageable);
        return ResponseEntity.ok(resultado);
    }

    @GetMapping("/{id}")
    @RequirePermission(module = "AUDITORIA", permission = "READ")
    public ResponseEntity<AuditoriaDetalleDTO> obtenerDetalle(@PathVariable Long id) {
        // Auto-auditoría de consulta específica
        auditService.registrarConsulta("auditoria", "Consulta de detalle y diff para el evento ID " + id);

        AuditoriaDetalleDTO detalle = auditService.obtenerPorId(id);
        return ResponseEntity.ok(detalle);
    }

    @GetMapping("/tablas")
    @RequirePermission(module = "AUDITORIA", permission = "READ")
    public ResponseEntity<List<String>> listarTablas() {
        return ResponseEntity.ok(auditService.listarTablas());
    }

    @GetMapping("/stats")
    @RequirePermission(module = "AUDITORIA", permission = "READ")
    public ResponseEntity<AuditoriaStatsDTO> obtenerEstadisticas() {
        return ResponseEntity.ok(auditService.obtenerEstadisticas());
    }
}
