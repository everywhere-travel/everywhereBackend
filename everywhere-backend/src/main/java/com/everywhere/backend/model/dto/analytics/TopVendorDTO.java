package com.everywhere.backend.model.dto.analytics;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TopVendorDTO {
    private String nombreVendedor;
    private BigDecimal montoVendido;
    private Integer cantidadCotizaciones;
}
