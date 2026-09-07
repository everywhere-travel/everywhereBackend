package com.everywhere.backend.model.dto.analytics;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TopProductDTO {
    private String nombreProducto;
    private Integer cantidadVendida;
    private BigDecimal ingresoGenerado;
}
