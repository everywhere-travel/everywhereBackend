package com.everywhere.backend.model.dto.finops;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.Map;

/**
 * DTO para la telemetria de costos operativos (OPEX) y eficiencia en integraciones cloud.
 * Cumplimiento: FinOps Framework y Gobernanza de Costos Cloud.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FinOpsReportDTO {

    private String estado;
    private Instant timestamp;
    private Map<String, Object> apiPeruStats;
    private Map<String, Object> geminiAiStats;
    private Map<String, Object> resumenAhorroOpex;
    private Map<String, Object> indicadoresSostenibilidad;
}

