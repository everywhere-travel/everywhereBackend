package com.everywhere.backend.service.impl;

import com.everywhere.backend.model.dto.finops.FinOpsReportDTO;
import com.everywhere.backend.service.FinOpsCostService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Instant;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicLong;

@Slf4j
@Service
public class FinOpsCostServiceImpl implements FinOpsCostService {

    // Costo promedio estimado por consulta externa en Peru (S/ 0.08 PEN)
    private static final BigDecimal COSTO_UNITARIO_APIPERU_PEN = new BigDecimal("0.08");
    // Costo promedio por llamada LLM Flash ($ 0.0005 USD aprox S/ 0.0019 PEN)
    private static final BigDecimal COSTO_ESTIMADO_GEMINI_PEN = new BigDecimal("0.002");

    private final AtomicLong dniConsultasLocales = new AtomicLong(0);
    private final AtomicLong dniConsultasExternas = new AtomicLong(0);
    private final AtomicLong rucConsultasLocales = new AtomicLong(0);
    private final AtomicLong rucConsultasExternas = new AtomicLong(0);

    private final AtomicLong geminiLlamadasExitosas = new AtomicLong(0);
    private final AtomicLong geminiFallbacksLocales = new AtomicLong(0);
    private final AtomicLong geminiTokensConsumidos = new AtomicLong(0);

    @Override
    public void registrarConsultaLocalDni() {
        dniConsultasLocales.incrementAndGet();
    }

    @Override
    public void registrarConsultaExternaDni(boolean exitosa) {
        dniConsultasExternas.incrementAndGet();
    }

    @Override
    public void registrarConsultaLocalRuc() {
        rucConsultasLocales.incrementAndGet();
    }

    @Override
    public void registrarConsultaExternaRuc(boolean exitosa) {
        rucConsultasExternas.incrementAndGet();
    }

    @Override
    public void registrarLlamadaGemini(int outputTokens) {
        geminiLlamadasExitosas.incrementAndGet();
        geminiTokensConsumidos.addAndGet(Math.max(0, outputTokens));
    }

    @Override
    public void registrarFallbackGemini() {
        geminiFallbacksLocales.incrementAndGet();
    }

    @Override
    public FinOpsReportDTO obtenerReporte() {
        long totalConsultasLocales = dniConsultasLocales.get() + rucConsultasLocales.get();
        long totalConsultasExternas = dniConsultasExternas.get() + rucConsultasExternas.get();
        long totalConsultasIdentidad = totalConsultasLocales + totalConsultasExternas;

        double tasaAhorroCache = totalConsultasIdentidad > 0
                ? (double) totalConsultasLocales / totalConsultasIdentidad * 100.0
                : 100.0;

        BigDecimal ahorroApiPeruPEN = COSTO_UNITARIO_APIPERU_PEN.multiply(BigDecimal.valueOf(totalConsultasLocales));
        BigDecimal ahorroGeminiPEN = COSTO_ESTIMADO_GEMINI_PEN.multiply(BigDecimal.valueOf(geminiFallbacksLocales.get()));
        BigDecimal ahorroTotalPEN = ahorroApiPeruPEN.add(ahorroGeminiPEN);

        Map<String, Object> apiPeruStats = new LinkedHashMap<>();
        apiPeruStats.put("consultasLocalesEvitadas", totalConsultasLocales);
        apiPeruStats.put("consultasExternasFacturadas", totalConsultasExternas);
        apiPeruStats.put("tasaAciertoCacheLocal", String.format("%.2f%%", tasaAhorroCache));
        apiPeruStats.put("costoUnitarioEstimadoPEN", COSTO_UNITARIO_APIPERU_PEN);

        Map<String, Object> geminiAiStats = new LinkedHashMap<>();
        geminiAiStats.put("llamadasCloudExitosas", geminiLlamadasExitosas.get());
        geminiAiStats.put("fallbacksLocalesSinCosto", geminiFallbacksLocales.get());
        geminiAiStats.put("tokensTotalesConsumidos", geminiTokensConsumidos.get());

        Map<String, Object> resumenAhorro = new LinkedHashMap<>();
        resumenAhorro.put("ahorroIdentidadPEN", ahorroApiPeruPEN.setScale(2, RoundingMode.HALF_UP));
        resumenAhorro.put("ahorroChatbotPEN", ahorroGeminiPEN.setScale(2, RoundingMode.HALF_UP));
        resumenAhorro.put("ahorroTotalOpexPEN", ahorroTotalPEN.setScale(2, RoundingMode.HALF_UP));

        Map<String, Object> sostenibilidad = new LinkedHashMap<>();
        sostenibilidad.put("estrategia", "Zero TIC con Local-First Caching");
        sostenibilidad.put("beneficioAmbiental", "Reducción de llamadas HTTP externas y latencia en red");

        return FinOpsReportDTO.builder()
                .estado("OPTIMO")
                .timestamp(Instant.now())
                .apiPeruStats(apiPeruStats)
                .geminiAiStats(geminiAiStats)
                .resumenAhorroOpex(resumenAhorro)
                .indicadoresSostenibilidad(sostenibilidad)
                .build();
    }
}

