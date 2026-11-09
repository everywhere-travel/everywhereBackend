package com.everywhere.backend.service;

import com.everywhere.backend.model.dto.finops.FinOpsReportDTO;
import com.everywhere.backend.service.impl.FinOpsCostServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

public class FinOpsCostServiceTest {

    private FinOpsCostServiceImpl finOpsCostService;

    @BeforeEach
    void setUp() {
        finOpsCostService = new FinOpsCostServiceImpl();
    }

    @Test
    void testAhorroPorConsultasLocales() {
        finOpsCostService.registrarConsultaLocalDni();
        finOpsCostService.registrarConsultaLocalDni();
        finOpsCostService.registrarConsultaLocalRuc();
        finOpsCostService.registrarConsultaExternaDni(true);

        FinOpsReportDTO reporte = finOpsCostService.obtenerReporte();
        assertNotNull(reporte);
        assertEquals("OPTIMO", reporte.getEstado());

        // 3 locales vs 1 externa = 75% tasa de acierto
        assertEquals("75.00%", reporte.getApiPeruStats().get("tasaAciertoCacheLocal"));
        assertEquals(3L, reporte.getApiPeruStats().get("consultasLocalesEvitadas"));
        assertEquals(1L, reporte.getApiPeruStats().get("consultasExternasFacturadas"));

        BigDecimal ahorroTotal = (BigDecimal) reporte.getResumenAhorroOpex().get("ahorroTotalOpexPEN");
        assertTrue(ahorroTotal.compareTo(BigDecimal.ZERO) > 0);
    }

    @Test
    void testAhorroPorFallbackGemini() {
        finOpsCostService.registrarFallbackGemini();
        finOpsCostService.registrarLlamadaGemini(300);

        FinOpsReportDTO reporte = finOpsCostService.obtenerReporte();
        assertNotNull(reporte);
        assertEquals(1L, reporte.getGeminiAiStats().get("fallbacksLocalesSinCosto"));
        assertEquals(1L, reporte.getGeminiAiStats().get("llamadasCloudExitosas"));
        assertEquals(300L, reporte.getGeminiAiStats().get("tokensTotalesConsumidos"));
    }
}

