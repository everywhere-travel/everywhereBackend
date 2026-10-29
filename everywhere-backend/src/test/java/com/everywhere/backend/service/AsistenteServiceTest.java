package com.everywhere.backend.service;

import com.everywhere.backend.mapper.CotizacionMapper;
import com.everywhere.backend.model.dto.chat.ChatMessageRequestDTO;
import com.everywhere.backend.model.dto.chat.ChatMessageResponseDTO;
import com.everywhere.backend.model.entity.Cotizacion;
import com.everywhere.backend.model.entity.EstadoCotizacion;
import com.everywhere.backend.model.entity.Personas;
import com.everywhere.backend.repository.CotizacionRepository;
import com.everywhere.backend.repository.DetalleCotizacionRepository;
import com.everywhere.backend.service.impl.AsistenteServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class AsistenteServiceTest {

    @Mock
    private CotizacionRepository cotizacionRepository;

    @Mock
    private DetalleCotizacionRepository detalleCotizacionRepository;

    @Mock
    private CotizacionMapper cotizacionMapper;

    @Mock
    private AnalyticsService analyticsService;

    @InjectMocks
    private AsistenteServiceImpl asistenteService;

    private Cotizacion cotizacionBarata;
    private Cotizacion cotizacionCara;

    @BeforeEach
    void setUp() {
        EstadoCotizacion estado = new EstadoCotizacion();
        estado.setId(1);
        estado.setDescripcion("Pendiente");

        Personas persona1 = new Personas();
        persona1.setId(10);

        Personas persona2 = new Personas();
        persona2.setId(20);

        cotizacionBarata = new Cotizacion();
        cotizacionBarata.setId(1);
        cotizacionBarata.setCodigoCotizacion("COT-001");
        cotizacionBarata.setNombreCotizacion("Vuelo Lima - Cusco");
        cotizacionBarata.setOrigenDestino("Cusco");
        cotizacionBarata.setMoneda("USD");
        cotizacionBarata.setEstadoCotizacion(estado);
        cotizacionBarata.setPersonas(persona1);
        cotizacionBarata.setFechaEmision(LocalDateTime.now().minusDays(5));
        cotizacionBarata.setFechaVencimiento(LocalDateTime.now().plusDays(3));

        cotizacionCara = new Cotizacion();
        cotizacionCara.setId(2);
        cotizacionCara.setCodigoCotizacion("COT-002");
        cotizacionCara.setNombreCotizacion("Paquete Cancún All Inclusive");
        cotizacionCara.setOrigenDestino("Cancún");
        cotizacionCara.setMoneda("USD");
        cotizacionCara.setEstadoCotizacion(estado);
        cotizacionCara.setPersonas(persona2);
        cotizacionCara.setCantAdultos(2);
        cotizacionCara.setCantNinos(1);
        cotizacionCara.setFechaEmision(LocalDateTime.now().minusDays(2));
        cotizacionCara.setFechaVencimiento(LocalDateTime.now().plusDays(4));
    }

    @Test
    void should_ReturnMostExpensiveQuote_When_AskedForCotizacionMasCara() {
        when(cotizacionRepository.findAll()).thenReturn(List.of(cotizacionBarata, cotizacionCara));
        
        // Simular totales: Cotización 1 = $350.00, Cotización 2 = $9,450.00
        Object[] row1 = new Object[]{1, new BigDecimal("350.00"), new BigDecimal("350.00")};
        Object[] row2 = new Object[]{2, new BigDecimal("9450.00"), new BigDecimal("9450.00")};
        when(detalleCotizacionRepository.findTotalesPorCotizacion()).thenReturn(List.of(row1, row2));

        when(cotizacionMapper.resolveClienteInfo(anyList())).thenReturn(Map.of(
                10, new CotizacionMapper.ClienteInfo("Carlos Mendoza", "12345678"),
                20, new CotizacionMapper.ClienteInfo("Empresa Sol S.A.C.", "20501234567")
        ));

        ChatMessageRequestDTO request = ChatMessageRequestDTO.builder()
                .message("¿Cuál es la cotización más cara que tenemos?")
                .build();

        ChatMessageResponseDTO response = asistenteService.procesarMensaje(request);

        assertNotNull(response);
        assertNotNull(response.getReply());
        assertTrue(response.getReply().contains("COT-002"), "Debe mencionar el código COT-002");
        assertTrue(response.getReply().contains("Paquete Cancún"), "Debe mencionar el paquete");
        assertTrue(response.getReply().contains("9,450.00"), "Debe mostrar el monto exacto");
        assertTrue(response.getReply().contains("Empresa Sol S.A.C."), "Debe mencionar al cliente");
        assertNotNull(response.getSuggestions());
        assertFalse(response.getSuggestions().isEmpty());
    }

    @Test
    void should_ReturnCheapestQuote_When_AskedForCotizacionMasEconomica() {
        when(cotizacionRepository.findAll()).thenReturn(List.of(cotizacionBarata, cotizacionCara));
        
        Object[] row1 = new Object[]{1, new BigDecimal("350.00"), new BigDecimal("350.00")};
        Object[] row2 = new Object[]{2, new BigDecimal("9450.00"), new BigDecimal("9450.00")};
        when(detalleCotizacionRepository.findTotalesPorCotizacion()).thenReturn(List.of(row1, row2));

        ChatMessageRequestDTO request = ChatMessageRequestDTO.builder()
                .message("dame la cotización más económica o barata")
                .build();

        ChatMessageResponseDTO response = asistenteService.procesarMensaje(request);

        assertNotNull(response);
        assertTrue(response.getReply().contains("COT-001"));
        assertTrue(response.getReply().contains("350.00"));
    }

    @Test
    void should_ReturnCommercialFollowupTemplate_When_AskedForRedaccion() {
        when(cotizacionRepository.findAll()).thenReturn(List.of(cotizacionBarata));

        ChatMessageRequestDTO request = ChatMessageRequestDTO.builder()
                .message("ayúdame a redactar un mensaje de seguimiento para un cliente")
                .build();

        ChatMessageResponseDTO response = asistenteService.procesarMensaje(request);

        assertNotNull(response);
        assertTrue(response.getReply().contains("Plantilla de Seguimiento Comercial"));
        assertTrue(response.getReply().contains("EveryWhere Travel"));
    }
}
