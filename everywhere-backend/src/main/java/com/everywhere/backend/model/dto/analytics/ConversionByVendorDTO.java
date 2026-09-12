package com.everywhere.backend.model.dto.analytics;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ConversionByVendorDTO {
    private String nombreVendedor;
    private long totalCotizaciones;
    private long cotizacionesConvertidas;
    private double tasaConversion;     // porcentaje
    private BigDecimal montoVendido;
}
