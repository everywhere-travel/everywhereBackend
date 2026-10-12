package com.everywhere.backend.audit.service;

import com.everywhere.backend.audit.dto.AuditoriaDetalleDTO;
import com.everywhere.backend.audit.dto.AuditoriaResponseDTO;
import com.everywhere.backend.audit.dto.AuditoriaStatsDTO;
import com.everywhere.backend.audit.entity.Auditoria;
import com.everywhere.backend.audit.repository.AuditoriaRepository;
import com.everywhere.backend.audit.service.impl.AuditoriaServiceImpl;
import com.everywhere.backend.audit.util.AuditDiffUtil;
import com.everywhere.backend.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuditoriaServiceTest {

    @Mock
    private AuditoriaRepository auditoriaRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private AuditoriaServiceImpl auditoriaService;

    private Auditoria sampleAudit;

    @BeforeEach
    void setUp() {
        sampleAudit = Auditoria.builder()
                .id(1L)
                .tabla("cotizaciones")
                .registroId("100")
                .accion("MODIFICACION")
                .usuarioId(5)
                .username("admin@everywhere.com")
                .usuarioNombre("Administrador General")
                .datosPrevios("{\"monto\":100}")
                .datosNuevos("{\"monto\":150}")
                .cambiosDiff("{\"monto\":{\"antes\":100,\"ahora\":150}}")
                .ipOrigen("192.168.1.10")
                .endpoint("PUT /api/v1/cotizaciones/100")
                .fechaHora(LocalDateTime.now())
                .build();
    }

    @Test
    void testRegistrar_guardaAuditoriaConExito() {
        Map<String, Object> previo = Map.of("estado", "PENDIENTE", "monto", 500);
        Map<String, Object> nuevo = Map.of("estado", "CONFIRMADO", "monto", 500);

        auditoriaService.registrar("cotizaciones", "101", "MODIFICACION", previo, nuevo);

        ArgumentCaptor<Auditoria> captor = ArgumentCaptor.forClass(Auditoria.class);
        verify(auditoriaRepository).save(captor.capture());

        Auditoria guardada = captor.getValue();
        assertEquals("cotizaciones", guardada.getTabla());
        assertEquals("101", guardada.getRegistroId());
        assertEquals("MODIFICACION", guardada.getAccion());
        assertNotNull(guardada.getDatosPrevios());
        assertNotNull(guardada.getDatosNuevos());
        assertNotNull(guardada.getCambiosDiff());
        assertTrue(guardada.getCambiosDiff().contains("CONFIRMADO"));
    }

    @Test
    void testRegistrarConsulta_guardaAccionConsulta() {
        auditoriaService.registrarConsulta("auditoria", "Consulta de lista");

        ArgumentCaptor<Auditoria> captor = ArgumentCaptor.forClass(Auditoria.class);
        verify(auditoriaRepository).save(captor.capture());

        Auditoria guardada = captor.getValue();
        assertEquals("auditoria", guardada.getTabla());
        assertEquals("CONSULTA", guardada.getAccion());
        assertEquals("Consulta de lista", guardada.getCambiosDiff());
    }

    @Test
    void testListar_retornaPaginaCorrecta() {
        Page<Auditoria> page = new PageImpl<>(List.of(sampleAudit));
        when(auditoriaRepository.findAll(any(org.springframework.data.jpa.domain.Specification.class), any(org.springframework.data.domain.Pageable.class)))
                .thenReturn(page);

        Page<AuditoriaResponseDTO> resultado = auditoriaService.listar(
                "cotizaciones", null, null, null, null, null, PageRequest.of(0, 10));

        assertNotNull(resultado);
        assertEquals(1, resultado.getTotalElements());
        assertEquals("cotizaciones", resultado.getContent().get(0).getTabla());
        assertTrue(resultado.getContent().get(0).isTieneDiff());
    }

    @Test
    void testObtenerPorId_retornaDetalle() {
        when(auditoriaRepository.findById(1L)).thenReturn(Optional.of(sampleAudit));

        AuditoriaDetalleDTO detalle = auditoriaService.obtenerPorId(1L);

        assertNotNull(detalle);
        assertEquals(1L, detalle.getId());
        assertEquals("cotizaciones", detalle.getTabla());
        assertEquals("{\"monto\":100}", detalle.getDatosPrevios());
    }

    @Test
    void testAuditDiffUtil_detectaCambiosYEnmascaraPassword() {
        Map<String, Object> antes = Map.of("nombre", "Ana", "password", "secreto123", "edad", 25);
        Map<String, Object> ahora = Map.of("nombre", "Ana Maria", "password", "nuevo456", "edad", 25);

        String diff = AuditDiffUtil.calculateDiff(antes, ahora);

        assertNotNull(diff);
        assertTrue(diff.contains("nombre"));
        assertTrue(diff.contains("Ana Maria"));
        // El campo password no debe exponer el contenido real en el diff
        assertFalse(diff.contains("secreto123"));
        assertFalse(diff.contains("nuevo456"));
    }

    @Test
    void testObtenerEstadisticas() {
        when(auditoriaRepository.count()).thenReturn(100L);
        when(auditoriaRepository.countByFechaHoraAfter(any())).thenReturn(25L);
        when(auditoriaRepository.countByAccion("CREACION")).thenReturn(40L);
        when(auditoriaRepository.countByAccion("MODIFICACION")).thenReturn(50L);
        when(auditoriaRepository.countByAccion("ELIMINACION")).thenReturn(10L);
        when(auditoriaRepository.countByAccion("CONSULTA")).thenReturn(5L);

        AuditoriaStatsDTO stats = auditoriaService.obtenerEstadisticas();

        assertNotNull(stats);
        assertEquals(100L, stats.getTotalEventos());
        assertEquals(25L, stats.getEventosHoy());
        assertEquals(40L, stats.getCreaciones());
    }
}
