package com.everywhere.backend.model.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class PagoPaxRequestDTO {
    
    private Integer id;
    
    @NotNull(message = "El monto es obligatorio")
    @DecimalMin(value = "0.00", inclusive = false, message = "El monto debe ser mayor a 0")
    private BigDecimal monto;

    private String moneda;

    private String detalle;

    @NotNull(message = "El ID de liquidación es obligatorio")
    private Integer liquidacionId;

    @NotNull(message = "El ID de forma de pago es obligatorio")
    private Integer formaPagoId;

    private Integer proveedorId;
}
