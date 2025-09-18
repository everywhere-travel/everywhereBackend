package com.everywhere.backend.model.dto;

import lombok.Data;
import java.time.LocalDate;

import jakarta.validation.constraints.NotNull;

@Data
public class LiquidacionRequestDTO {
    private String numero;
    private LocalDate fechaCompra;
    private LocalDate fechaVencimiento;
    private String destino;
    private Integer numeroPasajeros;
    private String observacion;

    @NotNull
    private Integer cotizacionId;

    private Integer productoId;
    private Integer formaPagoId;

    //  private Integer carpetaId;
}
