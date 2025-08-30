package com.everywhere.backend.model.dto;

import lombok.Data;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.time.LocalDate;

@Data
public class LiquidacionRequestDTO {
    private String numero;
    private LocalDate fechaCompra;
    private LocalDate fechaVencimiento;
    private String destino;
    private Integer numeroPasajeros;
    private String observacion;

    // Comentado temporalmente para probar CRUD básico
    // @NotNull
    // private Integer cotizacionId;

    private Integer productoId;
    private Integer formaPagoId;

    //  private Integer carpetaId;
}
