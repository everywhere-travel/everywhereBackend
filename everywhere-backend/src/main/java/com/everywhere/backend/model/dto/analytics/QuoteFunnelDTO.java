package com.everywhere.backend.model.dto.analytics;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class QuoteFunnelDTO {
    private long totalCotizaciones;
    private long convertidas;          // Tienen DocumentoCobranza o Recibo
    private long noConvertidas;        // Sin ningún documento de pago
    private long cotizacionesVigentes; // Sin convertir y aún dentro del plazo
    private long cotizacionesVencidas; // Sin convertir y fecha de vencimiento pasada
    private double tasaConversion;     // (convertidas / totalCotizaciones) * 100
}
