package com.everywhere.backend.model.dto.analytics;

import lombok.Data;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;
import java.math.RoundingMode;

@Data
@NoArgsConstructor
public class KpiSummaryDTO {
    private BigDecimal ingresoTotal;
    private BigDecimal comisionesTotales;
    private Integer totalVentasCerradas;
    private BigDecimal expedienteDeVenta;   // Promedio por venta cerrada
    private Double tasaConversion;          // % de cotizaciones que se convirtieron
    private Long cotizacionesAbiertas;      // Cotizaciones sin DocCobranza/Recibo y vigentes
    private Long cotizacionesVencidas;      // Cotizaciones sin convertir y ya vencidas

    public KpiSummaryDTO(BigDecimal ingresoTotal, BigDecimal comisionesTotales, Integer totalVentasCerradas) {
        this.ingresoTotal = ingresoTotal;
        this.comisionesTotales = comisionesTotales;
        this.totalVentasCerradas = totalVentasCerradas;
        this.expedienteDeVenta = BigDecimal.ZERO;
        this.tasaConversion = 0.0;
        this.cotizacionesAbiertas = 0L;
        this.cotizacionesVencidas = 0L;
    }

    public void calcularExpediente() {
        if (ingresoTotal != null && totalVentasCerradas != null && totalVentasCerradas > 0) {
            this.expedienteDeVenta = ingresoTotal.divide(
                BigDecimal.valueOf(totalVentasCerradas), 2, RoundingMode.HALF_UP
            );
        } else {
            this.expedienteDeVenta = BigDecimal.ZERO;
        }
    }
}
