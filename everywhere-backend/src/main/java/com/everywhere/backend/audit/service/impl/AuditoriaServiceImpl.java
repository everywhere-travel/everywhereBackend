package com.everywhere.backend.audit.service.impl;

import com.everywhere.backend.audit.context.AuditContextHolder;
import com.everywhere.backend.audit.dto.AuditoriaDetalleDTO;
import com.everywhere.backend.audit.dto.AuditoriaResponseDTO;
import com.everywhere.backend.audit.dto.AuditoriaStatsDTO;
import com.everywhere.backend.audit.entity.Auditoria;
import com.everywhere.backend.audit.repository.AuditoriaRepository;
import com.everywhere.backend.audit.service.AuditService;
import com.everywhere.backend.audit.util.AuditDiffUtil;
import com.everywhere.backend.model.entity.User;
import com.everywhere.backend.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuditoriaServiceImpl implements AuditService {

    private final AuditoriaRepository auditoriaRepository;
    private final UserRepository userRepository;

    @Override
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void registrar(String tabla, String registroId, String accion, Object estadoPrevio, Object estadoNuevo) {
        try {
            UserInfo userInfo = resolveCurrentUser();
            AuditContextHolder.AuditMetadata metadata = AuditContextHolder.get();

            String datosPreviosJson = AuditDiffUtil.toJson(estadoPrevio);
            String datosNuevosJson = AuditDiffUtil.toJson(estadoNuevo);
            String cambiosDiff = AuditDiffUtil.calculateDiff(estadoPrevio, estadoNuevo);

            Auditoria auditoria = Auditoria.builder()
                    .tabla(tabla != null ? tabla.trim().toLowerCase() : "desconocido")
                    .registroId(registroId != null ? registroId : "0")
                    .accion(accion != null ? accion.toUpperCase() : "OPERACION")
                    .usuarioId(userInfo.id)
                    .username(userInfo.username)
                    .usuarioNombre(userInfo.nombreCompleto)
                    .datosPrevios(datosPreviosJson)
                    .datosNuevos(datosNuevosJson)
                    .cambiosDiff(cambiosDiff)
                    .ipOrigen(metadata != null ? metadata.getIpOrigen() : "127.0.0.1")
                    .endpoint(metadata != null ? metadata.getEndpoint() : "")
                    .userAgent(metadata != null ? metadata.getUserAgent() : "")
                    .build();

            auditoriaRepository.save(auditoria);
        } catch (Exception e) {
            log.error("Error al registrar auditoría en la tabla maestra: {}", e.getMessage(), e);
        }
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void registrarConsulta(String tabla, String observacion) {
        try {
            UserInfo userInfo = resolveCurrentUser();
            AuditContextHolder.AuditMetadata metadata = AuditContextHolder.get();

            Auditoria auditoria = Auditoria.builder()
                    .tabla(tabla != null ? tabla.trim().toLowerCase() : "auditoria")
                    .registroId("0")
                    .accion("CONSULTA")
                    .usuarioId(userInfo.id)
                    .username(userInfo.username)
                    .usuarioNombre(userInfo.nombreCompleto)
                    .cambiosDiff(observacion)
                    .ipOrigen(metadata != null ? metadata.getIpOrigen() : "127.0.0.1")
                    .endpoint(metadata != null ? metadata.getEndpoint() : "")
                    .userAgent(metadata != null ? metadata.getUserAgent() : "")
                    .build();

            auditoriaRepository.save(auditoria);
        } catch (Exception e) {
            log.error("Error al registrar consulta en auditoría: {}", e.getMessage());
        }
    }

    @Override
    @Transactional(readOnly = true)
    public Page<AuditoriaResponseDTO> listar(
            String tabla,
            String accion,
            String username,
            LocalDateTime fechaInicio,
            LocalDateTime fechaFin,
            String searchTerm,
            Pageable pageable
    ) {
        org.springframework.data.jpa.domain.Specification<Auditoria> spec =
                com.everywhere.backend.audit.repository.AuditoriaSpecification.filterBy(
                        tabla, accion, username, fechaInicio, fechaFin, searchTerm
                );
        return auditoriaRepository.findAll(spec, pageable)
                .map(this::toResponseDTO);
    }

    @Override
    @Transactional(readOnly = true)
    public AuditoriaDetalleDTO obtenerPorId(Long id) {
        Auditoria a = auditoriaRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Registro de auditoría no encontrado con id: " + id));

        return AuditoriaDetalleDTO.builder()
                .id(a.getId())
                .tabla(a.getTabla())
                .registroId(a.getRegistroId())
                .accion(a.getAccion())
                .usuarioId(a.getUsuarioId())
                .username(a.getUsername())
                .usuarioNombre(a.getUsuarioNombre())
                .datosPrevios(a.getDatosPrevios())
                .datosNuevos(a.getDatosNuevos())
                .cambiosDiff(a.getCambiosDiff())
                .ipOrigen(a.getIpOrigen())
                .endpoint(a.getEndpoint())
                .userAgent(a.getUserAgent())
                .fechaHora(a.getFechaHora())
                .build();
    }

    @Override
    @Transactional(readOnly = true)
    public List<String> listarTablas() {
        return auditoriaRepository.findDistinctTablas();
    }

    @Override
    @Transactional(readOnly = true)
    public AuditoriaStatsDTO obtenerEstadisticas() {
        long total = auditoriaRepository.count();
        LocalDateTime inicioHoy = LocalDate.now().atStartOfDay();
        long hoy = auditoriaRepository.countByFechaHoraAfter(inicioHoy);
        long creaciones = auditoriaRepository.countByAccion("CREACION");
        long modificaciones = auditoriaRepository.countByAccion("MODIFICACION");
        long eliminaciones = auditoriaRepository.countByAccion("ELIMINACION");
        long consultas = auditoriaRepository.countByAccion("CONSULTA");

        return AuditoriaStatsDTO.builder()
                .totalEventos(total)
                .eventosHoy(hoy)
                .creaciones(creaciones)
                .modificaciones(modificaciones)
                .eliminaciones(eliminaciones)
                .consultas(consultas)
                .build();
    }

    private AuditoriaResponseDTO toResponseDTO(Auditoria a) {
        return AuditoriaResponseDTO.builder()
                .id(a.getId())
                .tabla(a.getTabla())
                .registroId(a.getRegistroId())
                .accion(a.getAccion())
                .usuarioId(a.getUsuarioId())
                .username(a.getUsername())
                .usuarioNombre(a.getUsuarioNombre())
                .ipOrigen(a.getIpOrigen())
                .endpoint(a.getEndpoint())
                .fechaHora(a.getFechaHora())
                .tieneDiff(a.getCambiosDiff() != null && !a.getCambiosDiff().isBlank())
                .build();
    }

    private UserInfo resolveCurrentUser() {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            if (authentication != null && authentication.isAuthenticated() && !"anonymousUser".equals(authentication.getPrincipal())) {
                String principal = authentication.getName();
                Optional<User> userOpt = userRepository.findByEmail(principal);
                if (userOpt.isPresent()) {
                    User u = userOpt.get();
                    return new UserInfo(u.getId(), u.getEmail(), u.getNombre() != null ? u.getNombre() : u.getEmail());
                }
                return new UserInfo(null, principal, principal);
            }
        } catch (Exception e) {
            log.warn("No se pudo resolver usuario actual para auditoría: {}", e.getMessage());
        }
        return new UserInfo(null, "SISTEMA", "Sistema Automático");
    }

    private record UserInfo(Integer id, String username, String nombreCompleto) {}
}
